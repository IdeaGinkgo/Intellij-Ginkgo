package com.github.taylorono.intellij.ginkgo;

import com.goide.GoOsManager;
import com.goide.project.DefaultGoRootsProvider;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configurations.LocatableRunConfigurationOptions;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GinkgoRunConfigurationOptions extends LocatableRunConfigurationOptions {
    private final EnvironmentVariablesData envData;
    private final List<String> testNames;
    private final String workingDir;
    private final String ginkgoExecutable;
    private final String ginkgoOptions;

    public GinkgoRunConfigurationOptions(Project project) {
        envData = EnvironmentVariablesData.DEFAULT;
        testNames = new ArrayList<>();
        workingDir = project.getBasePath();
        ginkgoExecutable = findGinkgoExecutable(project);
        ginkgoOptions = "";
    }

    public GinkgoRunConfigurationOptions(RunConfigBuilder builder) {
        this.envData = builder.envData;
        this.testNames = builder.testNames;
        this.workingDir = builder.workingDir;
        this.ginkgoExecutable = builder.ginkgoExecutable;
        this.ginkgoOptions = builder.ginkgoOptions;
    }

    public EnvironmentVariablesData getEnvData() {
        return envData;
    }

    public List<String> getTestNames() {
        return testNames;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public String getGinkgoExecutable() {
        return ginkgoExecutable;
    }

    public String getGinkgoOptions() {
        return ginkgoOptions;
    }

    @NotNull
    private String findGinkgoExecutable(Project project) {
        String goBinPath = new DefaultGoRootsProvider().getGoPathBinRoots(project, null).stream().findFirst().get().getPath();
        if(GoOsManager.isWindows()) {
            return Paths.get(goBinPath, "ginkgo.exe").toString();
        }
        return Paths.get(goBinPath, "ginkgo").toString();
    }

    public static class RunConfigBuilder {
        private EnvironmentVariablesData envData;
        private List<String> testNames;
        private String workingDir;
        private String ginkgoExecutable;
        private String ginkgoOptions;

        public RunConfigBuilder setEnvData(EnvironmentVariablesData envData) {
            this.envData = envData;
            return this;
        }

        public GinkgoRunConfigurationOptions build() {
            return new GinkgoRunConfigurationOptions(this);
        }

        public RunConfigBuilder setTestNames(List<String> testNames) {
            this.testNames = testNames;
            return this;
        }

        public RunConfigBuilder setWorkingDir(String workingDir) {
            this.workingDir = workingDir;
            return this;
        }

        public RunConfigBuilder setGinkgoExecutable(String ginkgoExecutable) {
            this.ginkgoExecutable = ginkgoExecutable;
            return this;
        }

        public RunConfigBuilder setGinkgoOptions(String ginkgoOptions) {
            this.ginkgoOptions = ginkgoOptions;
            return this;
        }
    }
}
