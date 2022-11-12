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
import com.intellij.openapi.fileEditor.FileDocumentManager;
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

import java.net.InetSocketAddress;

import static org.jetbrains.concurrency.Promises.resolvedPromise;

public class GinkgoRunner extends AsyncProgramRunner<RunnerSettings> {

    public static final String DIRECTORY_COMPILE_ERROR = "go.test.cannot.run.compiling.on.directory.kind.run.configurations.error";
    public static final String DEBUGGING_PORT_ERROR = "go.execution.could.not.bind.remote.debugging.port.error";

    @Override
    public @NotNull @NonNls String getRunnerId() {
        return "GinkoRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return profile instanceof GinkgoRunConfiguration && executorIsSupported(executorId);
    }

    private boolean executorIsSupported(String executorId) {
        return "Run".equals(executorId) || "Debug".equals(executorId);
    }

    @NotNull
    @Override
    protected Promise<RunContentDescriptor> execute(@NotNull ExecutionEnvironment environment, @NotNull RunProfileState state) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();

        // Go Build and Debug
        if (state instanceof GinkgoRunningState && ((GinkgoRunningState)state).isDebug()) {
            return debugContentDescriptor(environment, (GinkgoRunningState) state);
        }

        // Ginkgo Execute
        return ginkgoContentDescriptor(environment, state);
    }

    @NotNull
    private Promise<RunContentDescriptor> ginkgoContentDescriptor(@NotNull ExecutionEnvironment environment, @NotNull RunProfileState state) throws ExecutionException {
        ExecutionResult runResult = state.execute(environment.getExecutor(), this);
        if (runResult == null) {
            return resolvedPromise(null);
        }

        RunContentBuilder runContentBuilder = new RunContentBuilder(runResult, environment);
        RunContentDescriptor contentToReuse = environment.getContentToReuse();
        return resolvedPromise(runContentBuilder.showRunContent(contentToReuse));
    }

    @NotNull
    private AsyncPromise<RunContentDescriptor> debugContentDescriptor(@NotNull ExecutionEnvironment environment, @NotNull GinkgoRunningState state) {
        final AsyncPromise<RunContentDescriptor> buildResult = new AsyncPromise<>();
        final AsyncPromise<RunContentDescriptor> runResult = new AsyncPromise<>();

        // Can only debug at package level for now
        if (!state.isPackageLevel()) {
            runResult.setError(new ExecutionException(GoBundle.message(DIRECTORY_COMPILE_ERROR)));
            return runResult;
        }

        // Start Build Phase
        // In order to attach the debugger we must precompile the test binaries
        GinkgoBuildRunningState buildingState = new GinkgoBuildRunningState(environment, buildResult, state);
        Task.Backgroundable buildTask = createBackgroundBuildTask(environment, buildResult, buildingState);

        // Run the build buildTask in the background
        runOnEdt(() -> ProgressManager.getInstance().runProcessWithProgressAsynchronously(buildTask, new BackgroundableProcessIndicator(buildTask)));
        // End Build Phase

        // Start Debug Phase
        // After the test binaries have been compiled we run the test file with and attach the debugger
        buildResult
                .onSuccess(it -> runWithDebuggerAttached(environment, state, runResult, buildingState))
                .onError(runResult::setError);
        // End Debug Phase

        return runResult;
    }

    private void runWithDebuggerAttached(@NotNull ExecutionEnvironment environment, @NotNull GinkgoRunningState state, AsyncPromise<RunContentDescriptor> runResult, GinkgoBuildRunningState buildingState) {
        state.getDebugServerAddress();
        state.setOutputFile(buildingState.getOutputFile());
        state.setBuildCommand(buildingState.getBuildCommand());
        state.getDebugClientAddress()
                .thenAsync(address -> startDebuggerProcess(environment, state, runResult, address))
                .onError(throwable -> runResult.setError(new ExecutionException(GoBundle.message(DEBUGGING_PORT_ERROR), throwable)));
    }

    private AsyncPromise<RunContentDescriptor> startDebuggerProcess(@NotNull ExecutionEnvironment environment, @NotNull RunProfileState state, AsyncPromise<RunContentDescriptor> runResult, InetSocketAddress address) {
        if (address == null) {
            runResult.setError(new ExecutionException(GoBundle.message(DEBUGGING_PORT_ERROR)));
        }

        runOnEdt(() -> runDebugger(environment, state, runResult, address));
        return runResult;
    }

    private void runDebugger(@NotNull ExecutionEnvironment environment, @NotNull RunProfileState state, AsyncPromise<RunContentDescriptor> runResult, InetSocketAddress address) {
        try {
            ExecutionResult executionResult = state.execute(environment.getExecutor(), this);
            XDebugProcessStarter starter = createDebugProcessStarter(executionResult, address);
            runResult.setResult(XDebuggerManager.getInstance(environment.getProject()).startSession(environment, starter).getRunContentDescriptor());
        } catch (ExecutionException executionException) {
            runResult.setError(executionException);
        }
    }

    @NotNull
    private Task.Backgroundable createBackgroundBuildTask(@NotNull ExecutionEnvironment environment, AsyncPromise<RunContentDescriptor> buildResult, GinkgoBuildRunningState buildingState) {
        return new Task.Backgroundable(environment.getProject(), "Building test") {
            public void run(@NotNull ProgressIndicator progressIndicator) {
                try {
                    runOnEdt(this::executeBuild);
                } catch (ProcessCanceledException processCanceledException) {
                    throw processCanceledException;
                } catch (Throwable throwable) {
                    buildResult.setError(throwable);
                }
            }

            public void onCancel() {
                buildResult.setError(GoBundle.message("go.execution.process.cancelled"));
            }

            private void executeBuild() {
                try {
                    buildingState.execute(environment.getExecutor(), GinkgoRunner.this);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @NotNull
    protected XDebugProcessStarter createDebugProcessStarter(@Nullable ExecutionResult executionResult, @Nullable InetSocketAddress socket) {
        return GoRunUtil.createDelveXDebugStarter(socket, executionResult, DlvDisconnectOption.KILL, false);
    }

    protected void runOnEdt(@NotNull Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(runnable, ModalityState.NON_MODAL);
    }
}
