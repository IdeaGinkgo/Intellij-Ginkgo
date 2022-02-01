package com.github.idea.ginkgo.execution;

import com.github.idea.ginkgo.GinkgoRunConfiguration;
import com.github.idea.ginkgo.execution.testing.GinkgoBuildRunningState;
import com.github.idea.ginkgo.execution.testing.GinkgoRunningState;
import com.goide.dlv.DlvDisconnectOption;
import com.goide.execution.GoRunUtil;
import com.goide.i18n.GoBundle;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.AsyncProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebuggerManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;

import java.net.InetSocketAddress;

public class GinkgoRunner extends AsyncProgramRunner<RunnerSettings> {

    @Override
    public @NotNull @NonNls String getRunnerId() {
        return "GinkoRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        if (!(profile instanceof GinkgoRunConfiguration)) {
            return false;
        }
        return "Run".equals(executorId) || "Debug".equals(executorId);
    }

    @NotNull
    @Override
    //See GoBuildingRunningState.java:execute
    protected Promise<RunContentDescriptor> execute(@NotNull ExecutionEnvironment environment, @NotNull RunProfileState state) throws ExecutionException {
        //Default executor
        if (!(state instanceof GinkgoRunningState)) {
            ExecutionResult result = state.execute(environment.getExecutor(), this);
            return Promises.resolvedPromise(result != null ? environment.getContentToReuse() : null);
        }

        GinkgoRunningState ginkgoRunningState = (GinkgoRunningState) state;

        //Ginkgo Debug
        final AsyncPromise<RunContentDescriptor> buildingPromise = new AsyncPromise();
        if (ginkgoRunningState.isDebug()) {
            // Start Build Phase
            // In order to attach the debugger we must precompile the test binaries
            GinkgoBuildRunningState buildingState = new GinkgoBuildRunningState(environment, ginkgoRunningState.getProject(), ginkgoRunningState.getConfiguration());
            Task.Backgroundable task = new Task.Backgroundable(environment.getProject(), "Ginkgo Test") {
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    try {
                        execute(environment, buildingState).processed(buildingPromise);
                    } catch (ProcessCanceledException processCanceledException) {
                        throw processCanceledException;
                    } catch (Throwable throwable) {
                        buildingPromise.setError(throwable);
                    }
                }

                public void onCancel() {
                    buildingPromise.setError(GoBundle.message("go.execution.process.cancelled", new Object[0]));
                }
            };
            this.runOnEdt(() -> {
                ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new BackgroundableProcessIndicator(task));
            }, ModalityState.defaultModalityState());
            // End Build Phase

            // Start Debug Phase
            // After the test binaries have been compiled we run the test file with and attach the debugger
            AsyncPromise<RunContentDescriptor> result = new AsyncPromise();
            buildingPromise.then(f -> {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ginkgoRunningState.getDebugServerAddress();
                ginkgoRunningState.setOutputFile(buildingState.getOutputFile());
                ginkgoRunningState.getDebugClientAddress().thenAsync((address) -> {
                    if (address == null) {
                        result.setError(new ExecutionException(GoBundle.message("go.execution.could.not.bind.remote.debugging.port.error", new Object[0])));
                    }

                    runOnEdt(() -> {
                        try {
                            ExecutionResult executionResult = state.execute(environment.getExecutor(), this);
                            XDebugProcessStarter starter = createDebugProcessStarter(executionResult, address);
                            result.setResult(XDebuggerManager.getInstance(environment.getProject()).startSession(environment, starter).getRunContentDescriptor());
                        } catch (ExecutionException executionException) {
                            result.setError(executionException);
                        }

                    }, ModalityState.NON_MODAL);
                    return result;
                }).onError((t) -> {
                    result.setError(new ExecutionException(GoBundle.message("go.execution.could.not.bind.remote.debugging.port.error", new Object[0]), t));
                });
                return null;
            });
            // End Debug Phase

            return result;
        }

        //Ginkgo Execute
        ExecutionResult result = state.execute(environment.getExecutor(), this);
        RunContentDescriptor contentToReuse = environment.getContentToReuse();
        return Promises.resolvedPromise(result != null ? (new RunContentBuilder(result, environment)).showRunContent(contentToReuse) : null);
    }

    @NotNull
    protected XDebugProcessStarter createDebugProcessStarter(@Nullable ExecutionResult executionResult, @Nullable InetSocketAddress socket) {
        return GoRunUtil.createDelveXDebugStarter(socket, executionResult, DlvDisconnectOption.KILL, false);
    }

    protected void runOnEdt(@NotNull Runnable runnable, @NotNull ModalityState modalityState) {
        ApplicationManager.getApplication().invokeLater(runnable, modalityState);
    }
}
