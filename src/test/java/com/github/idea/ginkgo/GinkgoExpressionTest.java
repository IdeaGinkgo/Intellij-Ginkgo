package com.github.idea.ginkgo;

import com.goide.psi.GoFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GinkgoExpressionTest extends BasePlatformTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Test
    public void test_fromPsiElement_parses_valid_active_describe() {
        LeafPsiElement testPsiElement = getLeafPsiElement("marker_ginkgo.go", "Describe");

        GinkgoExpression ginkgoExpression = GinkgoExpression.fromPsiElement(testPsiElement);
        assertTrue(ginkgoExpression.isValid());
        assertTrue(ginkgoExpression.isActive());
        assertEquals("Describe", ginkgoExpression.getSpecType());
        assertEquals("Ginkgo", ginkgoExpression.getFocusExpression());
    }

    @Test
    public void test_fromPsiElement_parses_valid_active_context() {
        LeafPsiElement testPsiElement = getLeafPsiElement("marker_ginkgo.go", "Context");

        GinkgoExpression ginkgoExpression = GinkgoExpression.fromPsiElement(testPsiElement);
        assertTrue(ginkgoExpression.isValid());
        assertTrue(ginkgoExpression.isActive());
        assertEquals("Context", ginkgoExpression.getSpecType());
        assertEquals("Ginkgo Describe MultipleContext", ginkgoExpression.getFocusExpression());
    }

    @Test
    public void test_fromPsiElement_parses_valid_active_it() {
        LeafPsiElement testPsiElement = getLeafPsiElement("marker_ginkgo.go", "It");

        GinkgoExpression ginkgoExpression = GinkgoExpression.fromPsiElement(testPsiElement);
        assertTrue(ginkgoExpression.isValid());
        assertTrue(ginkgoExpression.isActive());
        assertEquals("It", ginkgoExpression.getSpecType());
        assertEquals("Ginkgo Describe MultipleContext it should be true", ginkgoExpression.getFocusExpression());
        assertEquals("gotest://Ginkgo#Ginkgo Describe MultipleContext/it should be true", ginkgoExpression.getTestURL());
    }

    @Test
    public void test_fromPsiElement_parses_valid_active_when() {
        LeafPsiElement testPsiElement = getLeafPsiElement("marker_ginkgo.go", "When");

        GinkgoExpression ginkgoExpression = GinkgoExpression.fromPsiElement(testPsiElement);
        assertTrue(ginkgoExpression.isValid());
        assertTrue(ginkgoExpression.isActive());
        assertEquals("When", ginkgoExpression.getSpecType());
        assertEquals("Ginkgo Describe MultipleContext (when )?when true", ginkgoExpression.getFocusExpression());
        assertEquals("gotest://Ginkgo#Ginkgo Describe MultipleContext/when true", ginkgoExpression.getTestURL());
    }

    @Test
    public void test_fromPsiElement_parses_valid_active_specify() {
        LeafPsiElement testPsiElement = getLeafPsiElement("marker_ginkgo.go", "Specify");

        GinkgoExpression ginkgoExpression = GinkgoExpression.fromPsiElement(testPsiElement);
        assertTrue(ginkgoExpression.isValid());
        assertTrue(ginkgoExpression.isActive());
        assertEquals("Specify", ginkgoExpression.getSpecType());
        assertEquals("Ginkgo Describe MultipleContext specify true", ginkgoExpression.getFocusExpression());
        assertEquals("gotest://Ginkgo#Ginkgo Describe MultipleContext/specify true", ginkgoExpression.getTestURL());
    }

    @Test
    public void test_fromPsiElement_parses_valid_active_table() {
        LeafPsiElement testPsiElement = getLeafPsiElement("marker_ginkgo.go", "DescribeTable");

        GinkgoExpression ginkgoExpression = GinkgoExpression.fromPsiElement(testPsiElement);
        assertTrue(ginkgoExpression.isValid());
        assertTrue(ginkgoExpression.isActive());
        assertEquals("DescribeTable", ginkgoExpression.getSpecType());
        assertEquals("Ginkgo Describe MultipleContext Table", ginkgoExpression.getFocusExpression());
    }

    @Test
    public void test_fromPsiElement_parses_valid_active_table_entity() {
        LeafPsiElement testPsiElement = getLeafPsiElement("marker_ginkgo.go", "Entry");
        GinkgoExpression ginkgoExpression = GinkgoExpression.fromPsiElement(testPsiElement);
        assertTrue(ginkgoExpression.isValid());
        assertTrue(ginkgoExpression.isActive());
        assertFalse(ginkgoExpression.isDynamicTableEntry());
        assertEquals("Entry", ginkgoExpression.getSpecType());
        assertEquals("Ginkgo Describe MultipleContext Table true", ginkgoExpression.getFocusExpression());
        assertEquals("gotest://Ginkgo#Ginkgo Describe MultipleContext Table/true", ginkgoExpression.getTestURL());
    }

    private LeafPsiElement getLeafPsiElement(String filePath, String spec) {
        GoFile file = (GoFile) myFixture.configureByFile(filePath);
        return PsiTreeUtil.findChildrenOfType(file, LeafPsiElement.class).stream()
                .filter(e -> e.getText().equals(spec))
                .findFirst()
                .orElseThrow(() -> new AssertionFailedError("Ginkgo Expression Not Found"));
    }
}