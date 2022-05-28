package com.github.idea.ginkgo.util;

import com.goide.psi.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class GinkgoUtilTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
    }

    void assertSpecNames(String testFile, String expectedFocusExpression) {
        myFixture.configureByFile(testFile);
        ApplicationManager.getApplication().runReadAction(() -> {
            GoFile file = (GoFile) myFixture.getFile();
            int offset = myFixture.getEditor().getCaretModel().getOffset();
            PsiElement elementAt = file.findElementAt(offset);
            List<String> specNames = GinkgoUtil.getSpecNames(elementAt, true);
            assertEquals(expectedFocusExpression, String.join(" ", specNames));
        });
    }

    @Test
    void getSpecNames_generates_correct_focus_expression() {
        assertSpecNames("focus_expression_l0.go", "Book");
        assertSpecNames("focus_expression_l1.go", "Book Categorizing book length");
        assertSpecNames("focus_expression_l2.go", "Book Categorizing book length With more than 300 pages");
        assertSpecNames("focus_expression_l3.go", "Book Categorizing book length With more than 300 pages should be a novel");
        assertSpecNames("focus_expression_when.go", "Book Categorizing book length (when )?When Test should be a short story");
        assertSpecNames("focus_expression_table.go", "Book Category Table Novel");
        assertSpecNames("focus_expression_special_characters.go", "Book Categorizing book length (when )?When Test should be a \\(short\\) story");
    }
}