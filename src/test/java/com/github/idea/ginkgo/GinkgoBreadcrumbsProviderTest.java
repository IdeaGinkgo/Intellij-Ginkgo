package com.github.idea.ginkgo;

import com.goide.GoLanguage;
import com.goide.psi.GoCallExpr;
import com.goide.psi.GoFile;
import com.goide.psi.GoStructType;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import junit.framework.AssertionFailedError;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;

import static com.github.idea.ginkgo.icons.GinkgoIcons.DISABLED_TEST_ICON;

@RunWith(JUnit4.class)
public class GinkgoBreadcrumbsProviderTest extends BasePlatformTestCase {
    GinkgoBreadcrumbsProvider ginkgoBreadcrumbsProvider;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ginkgoBreadcrumbsProvider = new GinkgoBreadcrumbsProvider();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Test
    public void getLanguages() {
        assertContainsElements(Arrays.asList(ginkgoBreadcrumbsProvider.getLanguages()), GoLanguage.INSTANCE);
    }

    @Test
    public void acceptElement() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_ginkgo.go");
        acceptsElement(file, "Describe");
        acceptsElement(file, "DescribeTable");
        acceptsElement(file, "Context");
        acceptsElement(file, "It");
        acceptsElement(file, "Entry");
        acceptsElement(file, "Specify");
        acceptsElement(file, "FDescribe");
        acceptsElement(file, "FDescribeTable");
        acceptsElement(file, "FContext");
        acceptsElement(file, "FIt");
        acceptsElement(file, "FEntry");
        acceptsElement(file, "FSpecify");

        GoFile nonTestFile = (GoFile) myFixture.configureByFile("go_file.go");
        assertTrue(ginkgoBreadcrumbsProvider.acceptElement(getStructFieldElement(nonTestFile, "Title")));
    }

    private void acceptsElement(GoFile file, String spec) {
        assertTrue(ginkgoBreadcrumbsProvider.acceptElement(getSpecElement(file, spec)));
    }

    @Test
    public void getElementInfo() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_ginkgo.go");
        assertReturnsDescription(file, "Describe", "Describe Ginkgo");
        assertReturnsDescription(file, "DescribeTable", "DescribeTable Table");
        assertReturnsDescription(file, "Context", "Context MultipleContext");
        assertReturnsDescription(file, "It", "It it should be true");
        assertReturnsDescription(file, "Entry", "Entry true");
        assertReturnsDescription(file, "Specify", "Specify specify true");
        assertReturnsDescription(file, "FDescribe", "FDescribe Describe");
        assertReturnsDescription(file, "FDescribeTable", "FDescribeTable Table");
        assertReturnsDescription(file, "FContext", "FContext MultipleContext");
        assertReturnsDescription(file, "FIt", "FIt it should be true");
        assertReturnsDescription(file, "FEntry", "FEntry true");
        assertReturnsDescription(file, "FSpecify", "FSpecify specify true");

        GoFile nonTestFile = (GoFile) myFixture.configureByFile("go_file.go");
        assertEquals("Title: string", ginkgoBreadcrumbsProvider.getElementInfo(getStructFieldElement(nonTestFile, "Title")));
    }

    private void assertReturnsDescription(GoFile file, String spec, String expected) {
        assertEquals(expected, ginkgoBreadcrumbsProvider.getElementInfo(getSpecElement(file, spec)));
    }

    @Test
    public void getElementIcon() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_ginkgo.go");
        assertElementIcon(file, "Describe", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "DescribeTable", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "Context", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "It", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "Entry", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "Specify", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "FDescribe", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "FDescribeTable", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "FContext", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "FIt", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "FEntry", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "FSpecify", AllIcons.RunConfigurations.TestState.Run);
        assertElementIcon(file, "PDescribe", DISABLED_TEST_ICON);
        assertElementIcon(file, "PDescribeTable", DISABLED_TEST_ICON);
        assertElementIcon(file, "PContext", DISABLED_TEST_ICON);
        assertElementIcon(file, "PIt", DISABLED_TEST_ICON);
        assertElementIcon(file, "PEntry", DISABLED_TEST_ICON);
        assertElementIcon(file, "PSpecify", DISABLED_TEST_ICON);
    }

    private void assertElementIcon(GoFile file, String spec, Icon expected) {
        assertEquals(expected, ginkgoBreadcrumbsProvider.getElementIcon(getSpecElement(file, spec)));
    }

    @Test
    public void getContextActions() {
    }

    private @NotNull PsiElement getSpecElement(GoFile file, String specType) {
        return PsiTreeUtil.findChildrenOfType(file, GoCallExpr.class).stream()
                .filter(e-> specType.equals(e.getExpression().getText()))
                .findFirst()
                .orElseThrow(()->new AssertionFailedError(String.format("Not found: %s", specType)));
    }

    private @NotNull PsiElement getStructFieldElement(GoFile file, String fieldName) {
        return PsiTreeUtil.findChildrenOfType(file, GoStructType.class).stream()
                .flatMap(e-> e.getFieldDefinitions().stream().filter(f -> Objects.equals(f.getName(), fieldName)) )
                .findFirst()
                .orElseThrow(()->new AssertionFailedError(String.format("Not found: %s", fieldName)));
    }
}