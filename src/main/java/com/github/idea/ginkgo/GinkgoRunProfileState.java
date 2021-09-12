package com.github.idea.ginkgo;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class GinkgoRunProfileState implements RunProfileState {
    private final ExecutionEnvironment environment;
    private final Project project;
    private final GinkgoRunConfiguration configuration;
    private final Executor executor;

    public GinkgoRunProfileState(@NotNull ExecutionEnvironment env, @Nullable Project project, @NotNull Executor executor, @NotNull GinkgoRunConfiguration configuration) {
        this.environment = env;
        this.project = project;
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public @Nullable ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        return execute(executor, runner, startProcess());
    }

    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner, @NotNull ProcessHandler processHandler) throws ExecutionException {
        GinkgoConsoleProperties consoleProperties = new GinkgoConsoleProperties(configuration, "Ginkgo", executor);
        SMTRunnerConsoleView consoleView = new SMTRunnerConsoleView(consoleProperties);
        SMTestRunnerConnectionUtil.initConsoleView(consoleView, "Ginkgo");
        if (consoleView != null) {
            consoleView.attachToProcess(processHandler);
        }
        return new DefaultExecutionResult(consoleView, processHandler);
    }

    @NotNull
    public ProcessHandler startProcess() throws ExecutionException {
        GinkgoRunConfigurationOptions runOptions = configuration.getOptions();

        GeneralCommandLine commandLine = createCommandLine(runOptions);
        commandLine
                .withEnvironment(runOptions.getEnvData().getEnvs())
                .withWorkDirectory(runOptions.getWorkingDir());

        KillableColoredProcessHandler processHandler = new KillableColoredProcessHandler(commandLine) {
            public void startNotify() {
                notifyTextAvailable("WORKING_DIRECTORY=" + runOptions.getWorkingDir() + " #gosetup\n", ProcessOutputTypes.SYSTEM);
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

        if (StringUtils.isNotEmpty(runOptions.getGinkgoAdditionalOptions())) {
            commandList.add(runOptions.getGinkgoAdditionalOptions());
        }

        switch (runOptions.getGinkgoScope()) {
            case All:
                commandList.add("-r");
                break;
            case FOCUS:
                commandList.add(String.format("--focus=%s", runOptions.getFocusTestExpression()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + runOptions.getGinkgoScope());
        }
        return new GeneralCommandLine(commandList.stream().toArray(String[]::new));
    }
}
