package com.github.idea.ginkgo;

import com.github.idea.ginkgo.scope.GinkgoScope;
import com.github.idea.ginkgo.util.GinkgoUtil;
import com.goide.psi.GoCallExpr;
import com.goide.psi.GoFile;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GinkgoRunConfigurationProducer extends LazyRunConfigurationProducer<GinkgoRunConfiguration> {
    public static final String GINKGO = "Ginkgo";
    public static final String WHEN_REGEX = "(when )?";
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

        List<String> specNames = getSpecNames(context);
        GinkgoRunConfigurationOptions options = configuration.getOptions();

        GinkgoRunConfigurationOptions ginkgoRunConfigurationOptions = new GinkgoRunConfigurationOptions();
        ginkgoRunConfigurationOptions.setGinkgoExecutable(options.getGinkgoExecutable());
        ginkgoRunConfigurationOptions.setWorkingDir(context.getPsiLocation().getContainingFile().getContainingDirectory().getVirtualFile().getPath());
        ginkgoRunConfigurationOptions.setEnvData(options.getEnvData());
        ginkgoRunConfigurationOptions.setGinkgoAdditionalOptions("");
        ginkgoRunConfigurationOptions.setGinkgoScope(GinkgoScope.FOCUS);
        ginkgoRunConfigurationOptions.setTestNames(specNames);
        ginkgoRunConfigurationOptions.setFocusTestExpression(String.join(" ", specNames));
        GoFile file = (GoFile) context.getPsiLocation().getContainingFile();
        ginkgoRunConfigurationOptions.setPackageName(file.getPackageName());

        configuration.setOptions(ginkgoRunConfigurationOptions);
        configuration.setGeneratedName();
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull GinkgoRunConfiguration configuration, @NotNull ConfigurationContext context) {
        if (!configuration.isGeneratedName()) {
            return false;
        }

        GinkgoRunConfigurationOptions ginkgoOptions = configuration.getOptions();
        List<String> specNames = getSpecNames(context);
        return specNames.equals(ginkgoOptions.getTestNames());
    }

    private List<String> getSpecNames(ConfigurationContext context) {
        return GinkgoUtil.getSpecNames(context.getPsiLocation(), true);
    }

    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return ginkgoConfigurationFactory;
    }
}
