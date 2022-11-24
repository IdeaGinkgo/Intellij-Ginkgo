package com.github.idea.ginkgo;

import com.github.idea.ginkgo.execution.testing.GinkgoRunningState;
import com.github.idea.ginkgo.scope.GinkgoScope;
import com.github.idea.ginkgo.util.GinkgoSerializationUtil;
import com.goide.execution.target.GoLanguageRuntimeType;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.target.LanguageRuntimeType;
import com.intellij.execution.target.TargetEnvironmentAwareRunProfile;
import com.intellij.execution.target.TargetEnvironmentConfiguration;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static com.intellij.openapi.util.text.Strings.isEmpty;

public class GinkgoRunConfiguration extends LocatableConfigurationBase<GinkgoRunConfiguration> implements TargetEnvironmentAwareRunProfile {
    @NotNull
    private GinkgoRunConfigurationOptions myOptions;

    public GinkgoRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, @NotNull String name) {
        super(project, factory, name);
        myOptions = new GinkgoRunConfigurationOptions(project);
    }

    @Override
    public @NotNull GinkgoRunConfigurationOptions getOptions() {
        return myOptions;
    }

    public void setOptions(GinkgoRunConfigurationOptions myOptions) {
        this.myOptions = myOptions;
    }

    @Override
    public @NotNull GinkgoConfigurationEditor getConfigurationEditor() {
        return new GinkgoConfigurationEditor(getProject());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (isEmpty(myOptions.getGinkgoExecutable())) {
            throw new RuntimeConfigurationException("Ginkgo executable is required");
        }

        if (!new File(myOptions.getGinkgoExecutable()).exists()) {
            throw new RuntimeConfigurationException("Ginkgo executable is invalid");
        }

        if (myOptions.getGinkgoScope().equals(GinkgoScope.FOCUS) && myOptions.getPackageName().isBlank()) {
            throw new RuntimeConfigurationException("Package is not specified");
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        //return new GinkgoRemoteRunningState
        return new GinkgoRunningState(environment, getProject(), this);
    }

    @Override
    public String suggestedName() {
        if (myOptions.getTestNames().isEmpty()) {
            return "All Test";
        }

        return String.join(" ", myOptions.getTestNames());
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        GinkgoSerializationUtil.writeXml(element, myOptions);
    }

    @Override
    public void readExternal(@NotNull Element element) {
        super.readExternal(element);
        myOptions = GinkgoSerializationUtil.readXml(element);
    }

    @Override
    public boolean canRunOn(@NotNull TargetEnvironmentConfiguration target) {
        return true;
    }

    @Override
    public @Nullable LanguageRuntimeType<?> getDefaultLanguageRuntimeType() {
        return LanguageRuntimeType.EXTENSION_NAME.findExtension(GoLanguageRuntimeType.class);
    }

    @Override
    public @Nullable String getDefaultTargetName() {
        return this.getOptions().getRemoteTarget();
    }

    @Override
    public void setDefaultTargetName(@Nullable String targetName) {
        this.getOptions().setRemoteTarget(targetName);
    }
}
