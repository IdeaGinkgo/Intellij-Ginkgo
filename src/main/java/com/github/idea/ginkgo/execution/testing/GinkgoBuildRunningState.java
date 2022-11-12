package com.github.idea.ginkgo.execution.testing;

import com.github.idea.ginkgo.GinkgoRunConfigurationOptions;
import com.goide.GoEnvironmentUtil;
import com.goide.GoOsManager;
import com.goide.i18n.GoBundle;
import com.goide.sdk.GoSdkUtil;
import com.goide.util.GoUtil;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.EnvironmentUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.AsyncPromise;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GinkgoBuildRunningState extends GinkgoState {
    public static final String COMPILATION_FAILED = "go.execution.compilation.failed.notification.title";
    public static final String FILE_CREATION_FAILED = "go.execution.cannot.create.temp.output.file.error";
    private final AsyncPromise<RunContentDescriptor> buildingPromise;
    private File outputFile;
    private String buildCommand;

    public GinkgoBuildRunningState(@NotNull ExecutionEnvironment env, AsyncPromise<RunContentDescriptor> buildingPromise, GinkgoRunningState runningState) {
        super(env, runningState.getProject(), runningState.getConfiguration());
        this.buildingPromise = buildingPromise;
    }

    @Override
    @Nullable
    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner<?> runner) throws ExecutionException {
        return new DefaultExecutionResult(startBuildProcess());
    }

    @NotNull
    public ProcessHandler startBuildProcess() throws ExecutionException {
        GinkgoRunConfigurationOptions runOptions = configuration.getOptions();

        Couple<String> pathEntry = updatePath(EnvironmentUtil.getEnvironmentMap());
        Map<String, String> environmentFromExtensions = getProjectEnvironmentExtensions();

        GeneralCommandLine commandLine = createBuildCommandLine()
                .withEnvironment(pathEntry.first, pathEntry.second)
                .withEnvironment("GOROOT", sdkRoot.getPath())
                .withEnvironment(runOptions.getEnvData().getEnvs())
                .withEnvironment(environmentFromExtensions)
                .withWorkDirectory(runOptions.getWorkingDir())
                .withCharset(StandardCharsets.UTF_8);

        buildCommand = commandLine.getCommandLineString();
        KillableColoredProcessHandler processHandler = new KillableColoredProcessHandler(commandLine);
        ProcessTerminatedListener.attach(processHandler);
        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                if (event.getExitCode() != 0) {
                    buildingPromise.setError(new ExecutionException(GoBundle.message(COMPILATION_FAILED)));
                }
                buildingPromise.setResult(environment.getContentToReuse());
            }
        });
        processHandler.startNotify();
        return processHandler;
    }

    /**
     * @param packageName Name of the go package to be compiled for test
     * @return Temp compiled test file target
     * @throws ExecutionException when can't create temporary file
     */
    private @NotNull File getOutputFile(@NotNull String packageName) throws ExecutionException {
        String binaryName = GoEnvironmentUtil.getBinaryFileNameForPath(FileUtil.sanitizeFileName(packageName), ".test", GoOsManager.isWindows());
        File outputDirectory = GoUtil.getGoLandTempDirectory().toFile();
        try {
            return FileUtil.createTempFile(outputDirectory, "", binaryName, true);
        } catch (IOException ioException) {
            throw new ExecutionException(GoBundle.message(FILE_CREATION_FAILED), ioException);
        }
    }

    private GeneralCommandLine createBuildCommandLine() throws ExecutionException {
        GinkgoRunConfigurationOptions options = configuration.getOptions();
        outputFile = getOutputFile(options.getPackageName());
        String defaultExecutable = GoEnvironmentUtil.getBinaryFileNameForPath("go");
        VirtualFile executable = GoSdkUtil.getGoExecutable(sdkRoot);
        String goExecutable = executable != null ? executable.getPath() : defaultExecutable;

        List<String> commandList = new ArrayList<>();
        commandList.add(goExecutable);
        commandList.add("test");
        commandList.add("-c");
        commandList.addAll(options.getGoToolOptionsList());
        commandList.add("-o");
        commandList.add(outputFile.getPath());
        commandList.add("-gcflags");
        commandList.add("all=-N -l");
        commandList.add(".");

        return new GeneralCommandLine(commandList.toArray(new String[0]));
    }

    public File getOutputFile() {
        return outputFile;
    }

    public String getBuildCommand() {
        return buildCommand;
    }
}
