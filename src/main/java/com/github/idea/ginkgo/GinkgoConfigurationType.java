package com.github.idea.ginkgo;

import com.github.idea.ginkgo.icons.GinkgoIcons;
import com.intellij.execution.configurations.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GinkgoConfigurationType extends SimpleConfigurationType implements DumbAware {
    private static final String NAME = "Ginkgo";

    @NotNull
    public static GinkgoConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(GinkgoConfigurationType.class);
    }

    public GinkgoConfigurationType() {
        super("GinkgoRunConfigurationType", NAME, NAME, NotNullLazyValue.lazy(GinkgoIcons.INSTANCE));
    }

    @Override
    @NotNull
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new GinkgoRunConfiguration(project, this, NAME.toLowerCase());
    }

    @Override
    @NotNull
    @NonNls
    public String getTag() {
        return NAME;
    }

    @Override
    @NotNull
    @NonNls
    public String getHelpTopic() {
        return "Ginkgo Spec Test";
    }

    @Override
    public boolean isManaged() {
        return true;
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
