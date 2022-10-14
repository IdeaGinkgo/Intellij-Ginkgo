package com.github.idea.ginkgo;

import com.goide.psi.GoFile;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import junit.framework.AssertionFailedError;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

public class GinkgoRunLineMarkerProviderTest extends BasePlatformTestCase {
    GinkgoRunLineMarkerProvider ginkgoRunLineMarkerProvider;

    @Before
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
    public void test_ginkgo_spec_functions_display_as_runnable() {
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
    public void test_ginkgo_full_spec_functions_display_as_runnable() {
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
    public void test_ginkgo_specs_in_non_test_files() {
        GoFile file = (GoFile) myFixture.configureByFile("ginkgo.go");
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
    public void test_ginkgo_marks_pending_test_with_enable() {
        GoFile file = (GoFile) myFixture.configureByFile("pending_test.go");
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

    private void verifyRunMarker(GoFile file, String spec) {
        RunLineMarkerContributor.Info info = ginkgoRunLineMarkerProvider.getInfo(getSpecElement(file, spec));
        assertNotNull(info);
        assertEquals(AllIcons.RunConfigurations.TestState.Run, info.icon);
        assertEquals(7, info.actions.length);
    }

    private void verifyEnableTestMarker(GoFile file, String spec) {
        RunLineMarkerContributor.Info info = ginkgoRunLineMarkerProvider.getInfo(getSpecElement(file, spec));
        assertNotNull(info);
        assertEquals(1, info.actions.length);
    }

    private @NotNull PsiElement getSpecElement(GoFile file, String specType) {
        return PsiTreeUtil.findChildrenOfType(file, LeafPsiElement.class).stream()
                .filter(e -> specType.equals(e.getText()))
                .findFirst()
                .orElseThrow(()->new AssertionFailedError(String.format("Not found: %s", specType)));
    }
}