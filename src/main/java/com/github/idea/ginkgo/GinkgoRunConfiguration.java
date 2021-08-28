package com.github.idea.ginkgo;

import com.github.idea.ginkgo.util.GinkgoSerializationUtil;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class GinkgoRunConfiguration extends LocatableConfigurationBase<GinkgoRunConfiguration> {
    @NotNull
    private GinkgoRunConfigurationOptions myOptions;

    public GinkgoRunConfiguration(@NotNull Project project, @Nullable ConfigurationFactory factory, @Nullable String name) {
        super(project, factory, name);
        myOptions = new GinkgoRunConfigurationOptions(project);
    }

    @Override
    public GinkgoRunConfigurationOptions getOptions() {
        return myOptions;
    }

    public void setOptions(GinkgoRunConfigurationOptions myOptions) {
        this.myOptions = myOptions;
    }

    @Override
    public GinkgoConfigurationEditor getConfigurationEditor() {
        return new GinkgoConfigurationEditor(getProject());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (StringUtils.isEmpty(myOptions.getGinkgoExecutable())) {
            throw new RuntimeConfigurationException("Ginkgo executable is required");
        }

        if (!new File(myOptions.getGinkgoExecutable()).exists()) {
            throw new RuntimeConfigurationException("Ginkgo executable is invalid");
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return new GinkgoRunProfileState(environment, getProject(), executor, this);
    }

    @Override
    public String suggestedName() {
        if (myOptions.getTestNames().isEmpty()) {
            return "All Test";
        }

        return String.join("_", myOptions.getTestNames());
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);
        GinkgoSerializationUtil.INSTANCE.writeXml(element, myOptions);
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        myOptions = GinkgoSerializationUtil.INSTANCE.readXml(element);
    }
}
