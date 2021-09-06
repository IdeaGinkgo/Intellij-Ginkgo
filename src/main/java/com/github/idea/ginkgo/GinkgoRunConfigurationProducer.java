package com.github.idea.ginkgo;

import com.github.idea.ginkgo.scope.GinkgoScope;
import com.goide.psi.GoCallExpr;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GinkgoRunConfigurationProducer extends LazyRunConfigurationProducer<GinkgoRunConfiguration> {
    public static final String GINKGO = "Ginkgo";
    private final ConfigurationFactory ginkgoConfigurationFactory;
    private final List<String> SpecNodes = Arrays.asList("Describe", "Context", "When", "It", "Specify", "FDescribe", "FContext", "FWhen", "FIt", "FSpecify");

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
        Collections.reverse(specNames);
        GinkgoRunConfigurationOptions options = configuration.getOptions();

        GinkgoRunConfigurationOptions ginkgoRunConfigurationOptions = new GinkgoRunConfigurationOptions();
        ginkgoRunConfigurationOptions.setGinkgoExecutable(options.getGinkgoExecutable());
        ginkgoRunConfigurationOptions.setWorkingDir(context.getPsiLocation().getContainingFile().getContainingDirectory().getVirtualFile().getPath());
        ginkgoRunConfigurationOptions.setEnvData(options.getEnvData());
        ginkgoRunConfigurationOptions.setGinkgoScope(GinkgoScope.FOCUS);
        ginkgoRunConfigurationOptions.setTestNames(specNames);
        ginkgoRunConfigurationOptions.setFocusTestExpression(String.join(" ", specNames));

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
        Collections.reverse(specNames);
        return specNames.equals(ginkgoOptions.getTestNames());
    }

    private List<String> getSpecNames(ConfigurationContext context) {
        Stack<String> specTree = new Stack();
        PsiElement location = context.getPsiLocation();
        while (location.getParent() != null) {
            location = location.getParent();
            if (location.getParent() instanceof GoCallExpr) {
                GoCallExpr parent = (GoCallExpr) location.getParent();
                specTree.push(parent.getArgumentList().getExpressionList().get(0).getText().replace("\"", ""));
            }
        }

        return specTree.isEmpty() ? Arrays.asList(GINKGO) : new ArrayList(specTree);
    }

    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return ginkgoConfigurationFactory;
    }
}
