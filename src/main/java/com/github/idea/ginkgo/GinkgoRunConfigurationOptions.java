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
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GinkgoRunConfigurationOptions extends LocatableRunConfigurationOptions implements Serializable {
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
        ginkgoScope = GinkgoScope.ALL;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GinkgoRunConfigurationOptions that = (GinkgoRunConfigurationOptions) o;

        if (!getGinkgoExecutable().equals(that.getGinkgoExecutable())) return false;
        if (!getWorkingDir().equals(that.getWorkingDir())) return false;
        if (!getEnvData().equals(that.getEnvData())) return false;
        if (!getGinkgoAdditionalOptions().equals(that.getGinkgoAdditionalOptions())) return false;
        if (getGinkgoScope() != that.getGinkgoScope()) return false;
        if (!getFocusTestExpression().equals(that.getFocusTestExpression())) return false;
        return getTestNames().equals(that.getTestNames());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getGinkgoExecutable().hashCode();
        result = 31 * result + getWorkingDir().hashCode();
        result = 31 * result + getEnvData().hashCode();
        result = 31 * result + getGinkgoAdditionalOptions().hashCode();
        result = 31 * result + getGinkgoScope().hashCode();
        result = 31 * result + getFocusTestExpression().hashCode();
        result = 31 * result + getTestNames().hashCode();
        return result;
    }

    @NotNull
    private String findGinkgoExecutable(Project project) {
        String executable = findGinkgoInGoPath(project);
        return StringUtils.isNotEmpty(executable) ? executable : findGinkgoByEnv();
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
