package com.github.idea.ginkgo;

import com.github.idea.ginkgo.scope.GinkgoScope;
import com.goide.GoEnvironmentUtil;
import com.goide.GoOsManager;
import com.goide.sdk.GoSdkUtil;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configurations.LocatableRunConfigurationOptions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GinkgoRunConfigurationOptions extends LocatableRunConfigurationOptions {
    private String ginkgoExecutable;
    private String workingDir;
    private EnvironmentVariablesData envData;
    private String ginkgoAdditionalOptions;
    private GinkgoScope ginkgoScope;
    private String focusTestExpression;
    private List<String> testNames;

    public GinkgoRunConfigurationOptions() {
    }

    public GinkgoRunConfigurationOptions(Project project) {
        ginkgoExecutable = findGinkgoExecutable(project);
        workingDir = project.getBasePath();
        envData = EnvironmentVariablesData.DEFAULT;
        ginkgoAdditionalOptions = "";
        ginkgoScope = GinkgoScope.All;
        focusTestExpression = "";
        testNames = new ArrayList<>();
    }

    public String getGinkgoExecutable() {
        return ginkgoExecutable;
    }

    public void setGinkgoExecutable(String ginkgoExecutable) {
        this.ginkgoExecutable = ginkgoExecutable;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public EnvironmentVariablesData getEnvData() {
        return envData;
    }

    public void setEnvData(EnvironmentVariablesData envData) {
        this.envData = envData;
    }

    public String getGinkgoAdditionalOptions() {
        return ginkgoAdditionalOptions;
    }

    public void setGinkgoAdditionalOptions(String ginkgoAdditionalOptions) {
        this.ginkgoAdditionalOptions = ginkgoAdditionalOptions;
    }

    public GinkgoScope getGinkgoScope() {
        return ginkgoScope;
    }

    public void setGinkgoScope(GinkgoScope ginkgoScope) {
        this.ginkgoScope = ginkgoScope;
    }

    public String getFocusTestExpression() {
        return focusTestExpression;
    }

    public void setFocusTestExpression(String focusTestExpression) {
        this.focusTestExpression = focusTestExpression;
    }

    public List<String> getTestNames() {
        return testNames;
    }

    public void setTestNames(List<String> testNames) {
        this.testNames = testNames;
    }

    @NotNull
    private String findGinkgoExecutable(Project project) {
        String ginkgoExecutable = findGinkgoInGoPath(project);
        return StringUtils.isNotEmpty(ginkgoExecutable) ? ginkgoExecutable : findGinkgoByEnv();
    }

    @NotNull
    public String findGinkgoInGoPath(Project project) {
        String ginkgoExecutableName = GoOsManager.isWindows() ? "ginkgo.exe" : "ginkgo";
        VirtualFile foundFile = GoSdkUtil.findExecutableInGoPath(ginkgoExecutableName, project, null);
        if (foundFile != null) {
            return foundFile.getPath();
        }
        return "";
    }

    @NotNull
    public String findGinkgoByEnv() {
        String goBinPath = GoEnvironmentUtil.retrieveGoBinFromEnvironment();
        if (GoOsManager.isWindows()) {
            String ginkgoPath = Paths.get(goBinPath, "ginkgo.exe").toString();
            if (new File(ginkgoPath).exists()) {
                return ginkgoPath;
            }
        }

        if (GoOsManager.isLinux() || GoOsManager.isMac()) {
            String ginkgoPath = Paths.get(goBinPath, "ginkgo").toString();
            if (new File(ginkgoPath).exists()) {
                return ginkgoPath;
            }
        }

        return "";
    }
}
