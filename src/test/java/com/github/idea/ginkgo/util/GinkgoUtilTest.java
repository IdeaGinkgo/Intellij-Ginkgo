package com.github.idea.ginkgo.util;

import com.goide.psi.GoFile;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.goide.psi.impl.manipulator.GoStringManipulator.unquote;

public class GinkgoUtilTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void test_getSpecNames_generates_correct_focus_expression() {
        assertSpecNames("focus_expression_l0.go", "Book");
        assertSpecNames("focus_expression_l1.go", "Book Categorizing book length");
        assertSpecNames("focus_expression_l2.go", "Book Categorizing book length With more than 300 pages");
        assertSpecNames("focus_expression_l3.go", "Book Categorizing book length With more than 300 pages should be a novel");
        assertSpecNames("focus_expression_when.go", "Book Categorizing book length (when )?When Test should be a short story");
        assertSpecNames("focus_expression_table.go", "Book Category Table Novel");
        assertSpecNames("focus_expression_special_characters.go", "Book Library \\\\| Categorizing book length (when )?When Test should be a \\(short\\) story\\[\\]");
    }

    @Test
    public void testUnquote() {
        assertEquals("test", unquote("\"test\""));
    }

    private void assertSpecNames(String testFile, String expectedFocusExpression) {
        myFixture.configureByFile(testFile);
        GoFile file = (GoFile) myFixture.getFile();
        int offset = myFixture.getEditor().getCaretModel().getOffset();
        PsiElement elementAt = file.findElementAt(offset);
        List<String> specNames = GinkgoUtil.getSpecNames(elementAt, true);
        assertEquals(expectedFocusExpression, String.join(" ", specNames));
    }
}