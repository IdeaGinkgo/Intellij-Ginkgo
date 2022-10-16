package com.github.idea.ginkgo;

import com.github.idea.ginkgo.icons.GinkgoIcons;
import com.goide.psi.GoFile;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import junit.framework.AssertionFailedError;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class GinkgoRunLineMarkerProviderTest extends BasePlatformTestCase {
    GinkgoRunLineMarkerProvider ginkgoRunLineMarkerProvider;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ginkgoRunLineMarkerProvider = new GinkgoRunLineMarkerProvider();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Test
    public void ginkgo_dot_imports_specs_display_as_runnable() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_dot_import_test.go");
        verifyRunMarker(file, "Describe");
        verifyRunMarker(file, "DescribeTable");
        verifyRunMarker(file, "Context");
        verifyRunMarker(file, "It");
        verifyRunMarker(file, "Entry");
        verifyRunMarker(file, "Specify");
        verifyRunMarker(file, "FDescribe");
        verifyRunMarker(file, "FDescribeTable");
        verifyRunMarker(file, "FContext");
        verifyRunMarker(file, "FIt");
        verifyRunMarker(file, "FEntry");
        verifyRunMarker(file, "FSpecify");
    }

    @Test
    public void ginkgo_specs_display_as_runnable() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_test.go");
        verifyRunMarker(file, "Describe");
        verifyRunMarker(file, "DescribeTable");
        verifyRunMarker(file, "Context");
        verifyRunMarker(file, "It");
        verifyRunMarker(file, "Entry");
        verifyRunMarker(file, "Specify");
        verifyRunMarker(file, "FDescribe");
        verifyRunMarker(file, "FDescribeTable");
        verifyRunMarker(file, "FContext");
        verifyRunMarker(file, "FIt");
        verifyRunMarker(file, "FEntry");
        verifyRunMarker(file, "FSpecify");
    }

    @Test
    public void ginkgo_specs_in_non_test_files() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_ginkgo.go");
        verifyRunMarker(file, "Describe");
        verifyRunMarker(file, "DescribeTable");
        verifyRunMarker(file, "Context");
        verifyRunMarker(file, "It");
        verifyRunMarker(file, "Entry");
        verifyRunMarker(file, "Specify");
        verifyRunMarker(file, "FDescribe");
        verifyRunMarker(file, "FDescribeTable");
        verifyRunMarker(file, "FContext");
        verifyRunMarker(file, "FIt");
        verifyRunMarker(file, "FEntry");
        verifyRunMarker(file, "FSpecify");
    }

    @Test
    public void ginkgo_marks_pending_specs_with_enable() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_dot_import_test.go");
        verifyEnableTestMarker(file, "PDescribe");
        verifyEnableTestMarker(file, "PDescribeTable");
        verifyEnableTestMarker(file, "PContext");
        verifyEnableTestMarker(file, "PIt");
        verifyEnableTestMarker(file, "PEntry");
        verifyEnableTestMarker(file, "PSpecify");
        verifyEnableTestMarker(file, "XDescribe");
        verifyEnableTestMarker(file, "XDescribeTable");
        verifyEnableTestMarker(file, "XContext");
        verifyEnableTestMarker(file, "XIt");
        verifyEnableTestMarker(file, "XEntry");
        verifyEnableTestMarker(file, "XSpecify");
    }

    @Test
    public void ginkgo_marks_dynamic_table_specs_with_warning() {
        GoFile file = (GoFile) myFixture.configureByFile("marker_dynamic_table_test.go");
        verifyWarningTestMarker(file, "Entry", 0);
        verifyWarningTestMarker(file, "Entry", 1);
        verifyWarningTestMarker(file, "Entry", 2);
        verifyWarningTestMarker(file, "FEntry", 0);
        verifyWarningTestMarker(file, "FEntry", 1);
        verifyWarningTestMarker(file, "FEntry", 2);
    }

    private void verifyRunMarker(GoFile file, String spec) {
        RunLineMarkerContributor.Info info = ginkgoRunLineMarkerProvider.getInfo(getSpecElement(file, spec));
        assertNotNull(info);
        assertEquals(AllIcons.RunConfigurations.TestState.Run, info.icon);
        assertEquals(7, info.actions.length);
    }

    private void verifyEnableTestMarker(GoFile file, String spec) {
        RunLineMarkerContributor.Info info = ginkgoRunLineMarkerProvider.getInfo(getSpecElement(file, spec));
        assertNotNull(info);
        assertEquals(GinkgoIcons.DISABLED_TEST_ICON, info.icon);
        assertEquals(1, info.actions.length);
    }

    private void verifyWarningTestMarker(GoFile file, String spec, int index) {
        RunLineMarkerContributor.Info info = ginkgoRunLineMarkerProvider.getInfo(getSpecElement(file, spec, index));
        assertNotNull(info);
        assertEquals(1, info.actions.length);
    }

    private @NotNull PsiElement getSpecElement(GoFile file, String specType) {
        return PsiTreeUtil.findChildrenOfType(file, LeafPsiElement.class).stream()
                .filter(e -> specType.equals(e.getText()))
                .findFirst()
                .orElseThrow(()->new AssertionFailedError(String.format("Not found: %s", specType)));
    }

    private @NotNull PsiElement getSpecElement(GoFile file, String specType, int index) {
        List<LeafPsiElement> leafElements = PsiTreeUtil.findChildrenOfType(file, LeafPsiElement.class).stream()
                .filter(e -> specType.equals(e.getText()))
                .collect(Collectors.toList());

        if (leafElements.size() < index) {
            throw new AssertionFailedError(String.format("Not found: %s index %s", specType, index));
        }

        return leafElements.get(index);
    }
}