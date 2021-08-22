package com.github.idea.ginkgo;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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
        commandLine.setWorkDirectory(runOptions.getWorkingDir());

        KillableColoredProcessHandler processHandler = new KillableColoredProcessHandler(commandLine);
        ProcessTerminatedListener.attach(processHandler);

        return processHandler;
    }

    private GeneralCommandLine createCommandLine(GinkgoRunConfigurationOptions runOptions) {
        if (StringUtils.isEmpty(runOptions.getGinkgoOptions())) {
            return new GeneralCommandLine(runOptions.getGinkgoExecutable(), "-r", "-v");
        }
        return new GeneralCommandLine(runOptions.getGinkgoExecutable(), "-r", "-v", runOptions.getGinkgoOptions());
    }
}
