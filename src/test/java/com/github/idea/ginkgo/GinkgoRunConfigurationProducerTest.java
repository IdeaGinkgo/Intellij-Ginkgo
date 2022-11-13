package com.github.idea.ginkgo;

import com.goide.psi.GoFile;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import junit.framework.AssertionFailedError;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GinkgoRunConfigurationProducerTest extends BasePlatformTestCase {

    GinkgoRunConfigurationProducer ginkgoRunConfigurationProducer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ginkgoRunConfigurationProducer = new GinkgoRunConfigurationProducer();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Test
    public void setupConfigurationFromContext() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_ginkgo.go");
        createsConfigurationWithFocusExpression(file, "Describe", "Ginkgo");
        createsConfigurationWithFocusExpression(file, "DescribeTable", "Ginkgo Describe MultipleContext Table");
        createsConfigurationWithFocusExpression(file, "Context", "Ginkgo Describe MultipleContext");
        createsConfigurationWithFocusExpression(file, "It", "Ginkgo Describe MultipleContext it should be true");
        createsConfigurationWithFocusExpression(file, "Entry", "Ginkgo Describe MultipleContext Table true");
        createsConfigurationWithFocusExpression(file, "Specify", "Ginkgo Describe MultipleContext specify true");
        createsConfigurationWithFocusExpression(file, "FDescribe", "Ginkgo Describe");
        createsConfigurationWithFocusExpression(file, "FDescribeTable", "Ginkgo Describe MultipleContext Table");
        createsConfigurationWithFocusExpression(file, "FContext", "Ginkgo Describe MultipleContext");
        createsConfigurationWithFocusExpression(file, "FIt", "Ginkgo Describe MultipleContext it should be true");
        createsConfigurationWithFocusExpression(file, "FEntry", "Ginkgo Describe MultipleContext Table true");
        createsConfigurationWithFocusExpression(file, "FSpecify", "Ginkgo Describe MultipleContext specify true");
    }

    private void createsConfigurationWithFocusExpression(GoFile file, String spec, String focusExpression) {
        GinkgoRunConfiguration config = (GinkgoRunConfiguration) new GinkgoConfigurationType().createTemplateConfiguration(getProject());
        PsiElement specElement = getSpecElement(file, spec);
        ConfigurationContext context = new ConfigurationContext(specElement);
        boolean result = ginkgoRunConfigurationProducer.setupConfigurationFromContext(config, context, new Ref<>(specElement));

        assertTrue(result);
        assertEquals(focusExpression, config.getOptions().getFocusTestExpression());
    }

    private @NotNull PsiElement getSpecElement(GoFile file, String specType) {
        return PsiTreeUtil.findChildrenOfType(file, LeafPsiElement.class).stream()
                .filter(e -> specType.equals(e.getText()))
                .findFirst()
                .orElseThrow(()->new AssertionFailedError(String.format("Not found: %s", specType)));
    }
}