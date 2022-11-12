package com.github.idea.ginkgo.execution.testing;

import com.github.idea.ginkgo.GinkgoRunConfiguration;
import com.goide.GoEnvironmentUtil;
import com.goide.execution.extension.GoExecutorExtension;
import com.goide.sdk.GoSdkService;
import com.goide.sdk.GoSdkUtil;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public abstract class GinkgoState implements RunProfileState {
    final ExecutionEnvironment environment;
    final Project project;
    final GinkgoRunConfiguration configuration;
    final VirtualFile sdkRoot;

    protected GinkgoState(@NotNull ExecutionEnvironment env, @Nullable Project project, @NotNull GinkgoRunConfiguration configuration) {
        this.environment = env;
        this.project = project;
        this.configuration = configuration;
        assert project != null;
        this.sdkRoot = GoSdkService.getInstance(project).getSdk(null).getSdkRoot();
    }

    /**
     * Uses the Goland executor extension to get Go module environment variables from the project settings setup
     * under Go -> Modules -> Environment
     *
     * @return Map<String, String> of Environmental entries from project configuration
     */
    @NotNull Map<String, String> getProjectEnvironmentExtensions() {
        Map<String, String> environmentFromExtensions = new HashMap<>();

        for (GoExecutorExtension extension : GoExecutorExtension.EP_NAME.getExtensionList()) {
            environmentFromExtensions.putAll(extension.getExtraEnvironment(project, null, environmentFromExtensions));
        }
        return environmentFromExtensions;
    }

    /**
     * Updates the environment path with the go bin paths as determined by framework configuration.
     *
     * @param env map of existing environment variables
     * @return Couple<String>
     */
    @NotNull Couple<String> updatePath(Map<String, String> env) {
        Collection<String> paths = new ArrayList<>();
        String goBinPaths = GoSdkUtil.retrieveEnvironmentPathForGo(project, null);
        Couple<String> pathEntry = GoEnvironmentUtil.getPathEntry(env);

        ContainerUtil.addIfNotNull(paths, StringUtil.nullize(goBinPaths, true));
        ContainerUtil.addIfNotNull(paths, StringUtil.nullize(pathEntry.second, true));

        return new Couple<>(pathEntry.first, StringUtil.join(paths, File.pathSeparator));
    }

}
