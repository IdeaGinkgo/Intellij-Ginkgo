package com.github.idea.ginkgo.execution.testing;

import com.github.idea.ginkgo.GinkgoConsoleProperties;
import com.github.idea.ginkgo.GinkgoRunConfiguration;
import com.github.idea.ginkgo.GinkgoRunConfigurationOptions;
import com.github.idea.ginkgo.scope.GinkgoScope;
import com.goide.dlv.DlvVm;
import com.goide.execution.GoRunUtil;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.target.value.TargetValue;
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Couple;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.net.NetUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GinkgoRunningState extends GinkgoState {
    private InetSocketAddress myDebugAddress;
    private File outputFile;
    private String buildCommand;
    @Nullable
    private volatile TargetValue<Integer> myDebugPortValue;


    public GinkgoRunningState(@NotNull ExecutionEnvironment env, @Nullable Project project, @NotNull GinkgoRunConfiguration configuration) {
        super(env, project, configuration);
    }

    @Override
    @Nullable
    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner<?> runner) throws ExecutionException {
        if (executor.getId().equals("Debug")) {
            return execute(executor, startDebugProcess());
        }
        return execute(executor, startProcess());
    }

    @NotNull
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProcessHandler processHandler) {
        GinkgoConsoleProperties consoleProperties = new GinkgoConsoleProperties(configuration, "Ginkgo", executor);
        SMTRunnerConsoleView consoleView = new SMTRunnerConsoleView(consoleProperties);
        SMTestRunnerConnectionUtil.initConsoleView(consoleView, "Ginkgo");
        consoleView.attachToProcess(processHandler);
        AbstractRerunFailedTestsAction rerunAction = consoleProperties.createRerunFailedTestsAction(consoleView);
        DefaultExecutionResult defaultExecutionResult = new DefaultExecutionResult(consoleView, processHandler);
        if (rerunAction != null) {
            defaultExecutionResult.setRestartActions(rerunAction);
        }
        return defaultExecutionResult;
    }

    @NotNull
    public ProcessHandler startProcess() throws ExecutionException {
        GinkgoRunConfigurationOptions runOptions = configuration.getOptions();

        Couple<String> pathEntry = updatePath(EnvironmentUtil.getEnvironmentMap());
        Map<String, String> environmentFromExtensions = getProjectEnvironmentExtensions();

        GeneralCommandLine commandLine = createCommandLine(runOptions)
                .withEnvironment(pathEntry.first, pathEntry.second)
                .withEnvironment("GOROOT", sdkRoot.getPath())
                .withEnvironment(runOptions.getEnvData().getEnvs())
                .withEnvironment(environmentFromExtensions)
                .withWorkDirectory(runOptions.getWorkingDir())
                .withCharset(StandardCharsets.UTF_8);

        KillableColoredProcessHandler processHandler = new KillableColoredProcessHandler(commandLine) {
            @Override
            public void startNotify() {
                notifyTextAvailable("GOROOT=" + commandLine.getEnvironment().get("GOROOT") + " #gosetup\n", ProcessOutputTypes.SYSTEM);
                notifyTextAvailable("WORKING_DIRECTORY=" + runOptions.getWorkingDir() + " #gosetup\n", ProcessOutputTypes.SYSTEM);
                super.startNotify();
            }
        };
        ProcessTerminatedListener.attach(processHandler);

        return processHandler;
    }

    @NotNull
    private ProcessHandler startDebugProcess() throws ExecutionException {
        GinkgoRunConfigurationOptions runOptions = configuration.getOptions();

        //Can not debug multiple packages at the same time each package is compiled into its own test package for debugging
        if(runOptions.getGinkgoScope() == GinkgoScope.ALL) {
            throw new ExecutionException("Can not debug on test scope all");
        }

        Couple<String> pathEntry = updatePath(EnvironmentUtil.getEnvironmentMap());
        Map<String, String> environmentFromExtensions = getProjectEnvironmentExtensions();
        GeneralCommandLine commandLine = createDebugCommandLine()
                .withEnvironment(pathEntry.first, pathEntry.second)
                .withEnvironment("GOROOT", sdkRoot.getPath())
                .withEnvironment(runOptions.getEnvData().getEnvs())
                .withEnvironment(environmentFromExtensions)
                .withWorkDirectory(runOptions.getWorkingDir())
                .withCharset(StandardCharsets.UTF_8);

        KillableColoredProcessHandler processHandler = new KillableColoredProcessHandler(commandLine) {
            @Override
            public void startNotify() {
                notifyTextAvailable("GOROOT=" + commandLine.getEnvironment().get("GOROOT") + " #gosetup\n", ProcessOutputTypes.SYSTEM);
                notifyTextAvailable("WORKING_DIRECTORY=" + runOptions.getWorkingDir() + " #gosetup\n", ProcessOutputTypes.SYSTEM);
                notifyTextAvailable(buildCommand + " #gosetup\n", ProcessOutputTypes.SYSTEM);
                super.startNotify();
            }
        };
        ProcessTerminatedListener.attach(processHandler);

        return processHandler;
    }

    private GeneralCommandLine createCommandLine(GinkgoRunConfigurationOptions runOptions) {
        List<String> commandList = new ArrayList<>();
        commandList.add(runOptions.getGinkgoExecutable());
        commandList.add("-v");
        commandList.addAll(runOptions.getGoToolOptionsList());
        commandList.addAll(runOptions.getGinkgoAdditionalOptionsList());

        if (runOptions.isRerun()) {
            commandList.add("-r");
        }

        switch (runOptions.getGinkgoScope()) {
            case ALL -> commandList.add("-r");
            case FOCUS -> commandList.add(String.format("--focus=%s", runOptions.getFocusTestExpression()));
            default -> throw new IllegalStateException("Unexpected value: " + runOptions.getGinkgoScope());
        }
        return new GeneralCommandLine(commandList);
    }

    private GeneralCommandLine createDebugCommandLine() throws ExecutionException {
        GinkgoRunConfigurationOptions options = configuration.getOptions();
        File dlvExecutable = GoRunUtil.dlv(null);

        List<String> commandList = new ArrayList<>();
        commandList.add(dlvExecutable.getPath());
        commandList.add(String.format("--listen=%s:%s", myDebugAddress.getAddress().getHostAddress(), myDebugAddress.getPort()));
        commandList.add("--headless=true");
        commandList.add("--api-version=2");
        commandList.add("--check-go-version=false");
        commandList.add("--only-same-user=false");
        commandList.add("exec");
        commandList.add(outputFile.getPath());
        commandList.add("--");
        commandList.add("-ginkgo.v");
        commandList.addAll(options.getGinkgoAdditionalOptionsList());

        switch (options.getGinkgoScope()) {
            case ALL -> throw new ExecutionException("Can not debug on test scope all");
            case FOCUS -> commandList.add(String.format("-ginkgo.focus=%s", options.getFocusTestExpression()));
            default -> throw new IllegalStateException("Unexpected value: " + options.getGinkgoScope());
        }
        return new GeneralCommandLine(commandList);
    }


    @NotNull
    public Promise<InetSocketAddress> getDebugClientAddress() {
        InetSocketAddress serverAddress = myDebugAddress;
        if (serverAddress == null) {
            return Promises.resolvedPromise(null);
        } else if (myDebugPortValue == null) {
            return Promises.resolvedPromise(serverAddress);
        } else {
            return myDebugPortValue.getLocalValue().then(p -> new InetSocketAddress(serverAddress.getAddress(), p));
        }
    }

    public boolean isDebug() {
        return environment.getExecutor().getId().equals("Debug");
    }

    public void getDebugServerAddress() {
        if (myDebugAddress == null) {
            try {
                myDebugAddress = new InetSocketAddress(resolveLocalAddress(), NetUtils.findAvailableSocketPort());
            } catch (UnknownHostException unknownHostException) {
                DlvVm.LOG.warn("Cannot resolve localhost", unknownHostException);
            } catch (IOException ioException) {
                DlvVm.LOG.warn("Cannot find free port", ioException);
            }
        }
    }

    private InetAddress resolveLocalAddress() throws UnknownHostException {
        InetAddress[] addresses = InetAddress.getAllByName("localhost");
        return addresses[0];
    }

    public Project getProject() {
        return project;
    }

    public GinkgoRunConfiguration getConfiguration() {
        return configuration;
    }

    public boolean isPackageLevel() {
        return configuration.getOptions().getGinkgoScope() != GinkgoScope.ALL;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public void setBuildCommand(String buildCommand) {
        this.buildCommand = buildCommand;
    }
}
