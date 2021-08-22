package com.github.taylorono.intellij.ginkgo;

import com.goide.psi.GoCallExpr;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class GinkgoRunConfigurationProducer extends LazyRunConfigurationProducer<GinkgoRunConfiguration> {
    public static final String GINKGO = "Ginkgo";
    private final ConfigurationFactory ginkgoConfigurationFactory;

    public GinkgoRunConfigurationProducer() {
        super();
        ginkgoConfigurationFactory = new GinkgoConfigurationType();
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull GinkgoRunConfiguration configuration, @NotNull ConfigurationContext context, @NotNull Ref<PsiElement> sourceElement) {
        if (!(context.getPsiLocation().getParent().getParent() instanceof GoCallExpr)) {
            return false;
        }

        String name = getSpecName(context);
        GinkgoRunConfigurationOptions options = configuration.getOptions();
        GinkgoRunConfigurationOptions ginkgoRunConfigurationOptions = new GinkgoRunConfigurationOptions.RunConfigBuilder()
                .setEnvData(options.getEnvData())
                .setTestNames(Arrays.asList(GINKGO + ": " + name))
                .setWorkingDir(context.getPsiLocation().getContainingFile().getContainingDirectory().getVirtualFile().getPath())
                .setGinkgoExecutable(options.getGinkgoExecutable())
                .setGinkgoOptions(String.format("--focus=%s", name))
                .build();
        configuration.setOptions(ginkgoRunConfigurationOptions);
        configuration.setGeneratedName();
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull GinkgoRunConfiguration configuration, @NotNull ConfigurationContext context) {
        if (!(context.getPsiLocation().getParent().getParent() instanceof GoCallExpr)) {
            return false;
        }
        return configuration.getName().equalsIgnoreCase(GINKGO + ": " + getSpecName(context));
    }

    @Nullable
    @Override
    public RunnerAndConfigurationSettings findExistingConfiguration(@NotNull ConfigurationContext context) {
        final RunManager runManager = RunManager.getInstance(context.getProject());
        final List<RunnerAndConfigurationSettings> configurations = runManager.getConfigurationSettingsList(GinkgoConfigurationType.class);
        for (RunnerAndConfigurationSettings configurationSettings : configurations) {
            if (isConfigurationFromContext((GinkgoRunConfiguration) configurationSettings.getConfiguration(), context)) {
                return configurationSettings;
            }
        }
        return null;
    }

    private String getSpecName(ConfigurationContext context) {
        GoCallExpr e = (GoCallExpr) context.getPsiLocation().getParent().getParent();
        String specName = e.getArgumentList().getExpressionList().get(0).getText();
        return StringUtils.isEmpty(specName) ? GINKGO : specName.replace("\"", "");
    }

    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return ginkgoConfigurationFactory;
    }
}
