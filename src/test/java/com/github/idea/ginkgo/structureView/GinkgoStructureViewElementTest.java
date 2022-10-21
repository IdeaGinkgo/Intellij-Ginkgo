package com.github.idea.ginkgo.structureView;

import com.goide.psi.GoFile;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GinkgoStructureViewElementTest extends BasePlatformTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Test
    public void getChildren_non_test_file() {
        GoFile file = (GoFile) myFixture.configureByFile("go_file.go");
        GinkgoStructureViewElement ginkgoStructureViewElement = new GinkgoStructureViewElement(file);
        TreeElement[] children = ginkgoStructureViewElement.getChildren();
        assertEquals(0, children.length);
    }

    @Test
    public void getPresentation_rootLevel() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_dot_import_test.go");
        GinkgoStructureViewElement ginkgoStructureViewElement = new GinkgoStructureViewElement(file);
        ItemPresentation presentation = ginkgoStructureViewElement.getPresentation();
        assertEquals("Ginkgo marker_dot_import_test.go", presentation.getPresentableText());
    }

    @Test
    public void getPresentation_specLevel() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_dot_import_test.go");
        GinkgoStructureViewElement ginkgoStructureViewElement = new GinkgoStructureViewElement(file);
        TreeElement[] children = ginkgoStructureViewElement.getChildren();
        assertEquals(1, children.length);
        assertEquals("Describe Ginkgo", children[0].getPresentation().getPresentableText());
    }
}