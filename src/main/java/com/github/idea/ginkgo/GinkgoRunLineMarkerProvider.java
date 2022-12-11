package com.github.idea.ginkgo;

import com.github.idea.ginkgo.icons.GinkgoIcons;
import com.github.idea.ginkgo.util.GinkgoUtil;
import com.intellij.execution.TestStateStorage;
import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GinkgoRunLineMarkerProvider extends RunLineMarkerContributor {
    private static final Function<PsiElement, String> TOOLTIP_PROVIDER = element -> "Ginkgo Test";
    private static final Function<PsiElement, String> TOOLTIP_WARNING = element -> "Unable to focus dynamic test names";
    private static final AnAction ENABLE_SPEC_ACTION = ActionManager.getInstance().getAction("GinkgoEnableSpec");
    private static final AnAction DISABLE_SPEC_ACTION = ActionManager.getInstance().getAction("GinkgoDisableSpec");
    @Override
    @Nullable
    public Info getInfo(@NotNull PsiElement e) {
        PsiFile file = e.getContainingFile();
        if (!GinkgoUtil.isGinkgoTestFile(file)) {
            return null;
        }

        GinkgoExpression ginkgoExpression = GinkgoExpression.fromPsiElement(e);
        if (ginkgoExpression.isValid()) {
            if (ginkgoExpression.isDynamicTableEntry()) {
                return new Info(GinkgoIcons.DISABLE_SPEC_ICON, TOOLTIP_WARNING, DISABLE_SPEC_ACTION);
            }

            if (!ginkgoExpression.isActive()) {
                return new Info(GinkgoIcons.DISABLED_TEST_ICON, TOOLTIP_PROVIDER, ENABLE_SPEC_ACTION);
            }

            if (ginkgoExpression.isActive()) {
                return new Info(getTestIcon(ginkgoExpression), TOOLTIP_PROVIDER, getRunActions());
            }
        }

        return null;
    }

    @NotNull
    private static AnAction[] getRunActions() {
        //Default executor actions [Run, Debug, DlvRecordAndDebug, Coverage, Profiler, Android Profiler]
        AnAction[] defaultRunActions = ExecutorAction.getActions(0);
        return ArrayUtil.append(defaultRunActions, DISABLE_SPEC_ACTION);
    }

    @NotNull
    private static Icon getTestIcon(GinkgoExpression ginkgoExpression) {
        TestStateStorage testStateStorage = TestStateStorage.getInstance(ginkgoExpression.getProject());
        TestStateStorage.Record record = testStateStorage.getState(ginkgoExpression.getTestURL());
        if (record != null) {
            return getTestStateIcon(record, false);
        }
        record = testStateStorage.getState(ginkgoExpression.getTestURLV2());
        if (record != null) {
            return getTestStateIcon(record, false);
        }
        return AllIcons.RunConfigurations.TestState.Run;
    }
}
