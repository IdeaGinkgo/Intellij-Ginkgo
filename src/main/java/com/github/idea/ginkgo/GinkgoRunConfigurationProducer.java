package com.github.idea.ginkgo;

import com.github.idea.ginkgo.scope.GinkgoScope;
import com.github.idea.ginkgo.util.GinkgoUtil;
import com.goide.execution.testing.GoTestFinder;
import com.goide.psi.GoCallExpr;
import com.goide.util.GoUtil;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GinkgoRunConfigurationProducer extends LazyRunConfigurationProducer<GinkgoRunConfiguration> {
    public static final String GINKGO = "Ginkgo";
    public static final String WHEN = "when";
    private final ConfigurationFactory ginkgoConfigurationFactory;

    public GinkgoRunConfigurationProducer() {
        super();
        ginkgoConfigurationFactory = new GinkgoConfigurationType();
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull GinkgoRunConfiguration configuration, @NotNull ConfigurationContext context, @NotNull Ref<PsiElement> sourceElement) {
        PsiElement contextElement = getContextElement(context);

        // Provides configuration for directories
        if (contextElement instanceof PsiDirectory) {
            PsiDirectory directory = (PsiDirectory) contextElement;
            if (Arrays.stream(directory.getFiles()).filter(GoTestFinder::isTestFile).count() > 0) {
                GinkgoRunConfigurationOptions options = configuration.getOptions();

                GinkgoRunConfigurationOptions ginkgoRunConfigurationOptions = new GinkgoRunConfigurationOptions();
                ginkgoRunConfigurationOptions.setGinkgoExecutable(options.getGinkgoExecutable());
                ginkgoRunConfigurationOptions.setWorkingDir(directory.getVirtualFile().getPath());
                ginkgoRunConfigurationOptions.setEnvData(options.getEnvData());
                ginkgoRunConfigurationOptions.setGinkgoScope(GinkgoScope.ALL);
                ginkgoRunConfigurationOptions.setTestNames(Arrays.asList("ginkgo", directory.getName()));

                configuration.setOptions(ginkgoRunConfigurationOptions);
                configuration.setGeneratedName();
                return true;
            }
            return false;
        }

        // Provides configuration for Specs and Suites
        if ((contextElement.getParent().getParent() instanceof GoCallExpr)) {
            GoCallExpr function = (GoCallExpr) contextElement.getParent().getParent();
            GinkgoRunConfigurationOptions options = configuration.getOptions();
            GinkgoRunConfigurationOptions ginkgoRunConfigurationOptions = new GinkgoRunConfigurationOptions();
            ginkgoRunConfigurationOptions.setGinkgoExecutable(options.getGinkgoExecutable());
            ginkgoRunConfigurationOptions.setWorkingDir(context.getPsiLocation().getContainingFile().getContainingDirectory().getVirtualFile().getPath());
            ginkgoRunConfigurationOptions.setEnvData(options.getEnvData());
            if (GinkgoUtil.isGinkgoFunction(function)) {
                List<String> specNames = getSpecNames(context);
                ginkgoRunConfigurationOptions.setGinkgoScope(GinkgoScope.FOCUS);
                ginkgoRunConfigurationOptions.setTestNames(specNames);
                ginkgoRunConfigurationOptions.setFocusTestExpression(String.join(" ", specNames));
            }

            if (GinkgoUtil.isGinkgoSuite(function)) {
                String suiteName = getSuiteName(function);
                ginkgoRunConfigurationOptions.setGinkgoScope(GinkgoScope.ALL);
                ginkgoRunConfigurationOptions.setTestNames(Arrays.asList("ginkgo", suiteName));
            }

            configuration.setOptions(ginkgoRunConfigurationOptions);
            configuration.setGeneratedName();
            return true;
        }

        return false;
    }

    @NotNull
    private String getSuiteName(GoCallExpr function) {
        return function.getArgumentList().getExpressionList().get(1).getText().replace("\"", "");
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
        Deque<String> specTree = new ArrayDeque<>();
        PsiElement location = context.getPsiLocation();
        while (location.getParent() != null) {
            location = location.getParent();
            if (location.getParent() instanceof GoCallExpr) {
                GoCallExpr parent = (GoCallExpr) location.getParent();
                specTree.push(parent.getArgumentList().getExpressionList().get(0).getText().replace("\"", ""));

                //Special case append when for When blocks
                if (parent.getExpression().getText().equalsIgnoreCase(WHEN)) {
                    specTree.push(WHEN);
                }
            }
        }

        return specTree.isEmpty() ? Arrays.asList(GINKGO) : new ArrayList<>(specTree);
    }

    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return ginkgoConfigurationFactory;
    }

    @Nullable
    protected PsiElement getContextElement(@Nullable ConfigurationContext context) {
        if (context == null) {
            return null;
        } else {
            PsiElement psiElement = context.getPsiLocation();
            if (psiElement != null && psiElement.isValid()) {
                PsiFileSystemItem psiFile = psiElement instanceof PsiFileSystemItem ? (PsiFileSystemItem) psiElement : psiElement.getContainingFile();
                return !GoUtil.isInProject(psiElement.getProject(), psiFile != null ? psiFile.getVirtualFile() : null) ? null : psiElement;
            } else {
                return null;
            }
        }
    }
}
