package com.github.idea.ginkgo.execution.testing;

import com.github.idea.ginkgo.GinkgoRunConfiguration;
import com.github.idea.ginkgo.GinkgoRunConfigurationOptions;
import com.goide.GoEnvironmentUtil;
import com.goide.GoOsManager;
import com.goide.execution.extension.GoExecutorExtension;
import com.goide.i18n.GoBundle;
import com.goide.sdk.GoSdkService;
import com.goide.sdk.GoSdkUtil;
import com.goide.util.GoUtil;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.AsyncPromise;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GinkgoBuildRunningState implements RunProfileState {
    private final ExecutionEnvironment environment;
    private final Project project;
    private final GinkgoRunConfiguration configuration;
    private final AsyncPromise<RunContentDescriptor> buildingPromise;
    private VirtualFile sdkRoot;
    private File outputFile;
    private String buildCommand;


    public GinkgoBuildRunningState(@NotNull ExecutionEnvironment env, @Nullable Project project, @NotNull GinkgoRunConfiguration configuration, AsyncPromise<RunContentDescriptor> buildingPromise) {
        this.environment = env;
        this.project = project;
        this.configuration = configuration;
        this.buildingPromise = buildingPromise;
        this.sdkRoot = GoSdkService.getInstance(project).getSdk(null).getSdkRoot();
    }

    @Override
    @Nullable
    public ExecutionResult execute(Executor executor, ProgramRunner<?> runner) throws ExecutionException {
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
                    buildingPromise.setError(new ExecutionException(GoBundle.message("go.execution.compilation.failed.notification.title", new Object[0])));
                }
                buildingPromise.setResult(environment.getContentToReuse());
            }
        });
        processHandler.startNotify();
        return processHandler;
    }

    /**
     * @param packageName   Name of the go package to be compiled for test
     * @return              Temp compiled test file target
     * @throws ExecutionException
     */
    private @NotNull File getOutputFile(String packageName) throws ExecutionException {
        String binaryName = GoEnvironmentUtil.getBinaryFileNameForPath(FileUtil.sanitizeFileName(packageName), ".test", GoOsManager.isWindows());
        File outputDirectory = GoUtil.getGoLandTempDirectory().toFile();
        try {
            return FileUtil.createTempFile(outputDirectory, "", binaryName, true);
        } catch (IOException ioException) {
            throw new ExecutionException(GoBundle.message("go.execution.cannot.create.temp.output.file.error", new Object[0]), ioException);
        }
    }

    /**
     * Uses the Goland executor extension to get Go module environment variables from the project settings setup
     * under Go -> Modules -> Environment
     *
     * @return Map<String, String> of Environmental entries from project configuration
     */
    @NotNull
    private Map<String, String> getProjectEnvironmentExtensions() {
        Map<String, String> environmentFromExtensions = new HashMap<>();
        Iterator goExtensionIterator = GoExecutorExtension.EP_NAME.getExtensionList().iterator();

        while (goExtensionIterator.hasNext()) {
            GoExecutorExtension extension = (GoExecutorExtension) goExtensionIterator.next();
            environmentFromExtensions.putAll(extension.getExtraEnvironment(project, null, environmentFromExtensions));
        }
        return environmentFromExtensions;
    }

    /**
     * Updates the environment path with the go bin paths as determined by framework configuration.
     *
     * @param env
     * @return Couple<String>
     */
    private Couple<String> updatePath(Map<String, String> env) {
        Collection<String> paths = new ArrayList<>();
        String goBinPaths = GoSdkUtil.retrieveEnvironmentPathForGo(project, null);
        Couple<String> pathEntry = GoEnvironmentUtil.getPathEntry(env);

        ContainerUtil.addIfNotNull(paths, StringUtil.nullize(goBinPaths, true));
        ContainerUtil.addIfNotNull(paths, StringUtil.nullize(pathEntry.second, true));

        return new Couple<>(pathEntry.first, StringUtil.join(paths, File.pathSeparator));
    }

    public GeneralCommandLine createBuildCommandLine() throws ExecutionException {
        GinkgoRunConfigurationOptions options = configuration.getOptions();
        outputFile = getOutputFile(options.getPackageName());
        String defaultExecutable = GoEnvironmentUtil.getBinaryFileNameForPath("go");
        VirtualFile executable = GoSdkUtil.getGoExecutable(sdkRoot);
        String goExecutable = executable != null ? executable.getPath() : defaultExecutable;

        List<String> commandList = new ArrayList<>();
        commandList.add(goExecutable);
        commandList.add("test");
        commandList.add("-c");
        commandList.add("-o");
        commandList.add(outputFile.getPath());
        commandList.add("-gcflags");
        commandList.add("all=-N -l");
        commandList.add(".");

        return new GeneralCommandLine(commandList.stream().toArray(String[]::new));
    }

    public File getOutputFile() {
        return outputFile;
    }

    public String getBuildCommand() {
        return buildCommand;
    }
}
