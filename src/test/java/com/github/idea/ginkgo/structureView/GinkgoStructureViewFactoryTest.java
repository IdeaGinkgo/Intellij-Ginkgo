package com.github.idea.ginkgo.structureView;

import com.goide.psi.GoFile;
import com.goide.tree.GoStructureViewFactory;
import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GinkgoStructureViewFactoryTest extends BasePlatformTestCase {

    GinkgoStructureViewFactory ginkgoStructureViewFactory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ginkgoStructureViewFactory = new GinkgoStructureViewFactory();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Test
    public void getStructureViewBuilder_ginkgo() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_test.go");
        StructureViewBuilder structureViewBuilder = ginkgoStructureViewFactory.getStructureViewBuilder(file);
        assertNotNull(structureViewBuilder);
        StructureView structureView = structureViewBuilder.createStructureView(null, file.getProject());
        assertTrue(structureView.getTreeModel() instanceof GinkgoStructureViewModel);
    }

    @Test
    public void getStructureViewBuilder_go() {
        GoFile file = (GoFile) myFixture.configureByFile("go_file.go");
        StructureViewBuilder structureViewBuilder = ginkgoStructureViewFactory.getStructureViewBuilder(file);
        assertNotNull(structureViewBuilder);
        StructureView structureView = structureViewBuilder.createStructureView(null, file.getProject());
        assertTrue(structureView.getTreeModel() instanceof GoStructureViewFactory.Model);
    }
}