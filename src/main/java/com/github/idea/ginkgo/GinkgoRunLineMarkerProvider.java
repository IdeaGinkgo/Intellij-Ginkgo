package com.github.idea.ginkgo;

import com.github.idea.ginkgo.icons.GinkgoIcons;
import com.github.idea.ginkgo.util.GinkgoUtil;
import com.goide.GoTypes;
import com.goide.psi.*;
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
import java.util.List;

import static com.github.idea.ginkgo.GinkgoSpecs.*;


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

        if (e.getNode().getElementType() == GoTypes.IDENTIFIER && e.getParent().getParent() instanceof GoCallExpr) {
            GoCallExpr parent = (GoCallExpr) e.getParent().getParent();
            List<GoExpression> args = parent.getArgumentList().getExpressionList();
            String ginkgoSpec = parent.getExpression().getText();

            if (GinkgoSpecs.isTableEntity(ginkgoSpec) && args.size() >= 2 && !(args.get(0) instanceof GoStringLiteral)) {
                return new Info(GinkgoIcons.DISABLE_SPEC_ICON, TOOLTIP_WARNING, DISABLE_SPEC_ACTION);
            }

            if (isGinkgoPendingSpec(ginkgoSpec)) {
                return new Info(GinkgoIcons.DISABLED_TEST_ICON, TOOLTIP_PROVIDER, ENABLE_SPEC_ACTION);
            }

            if (isGinkgoActiveSpec(ginkgoSpec) && args.size() >= 2) {
                return new Info(getTestIcon(e, ginkgoSpec), TOOLTIP_PROVIDER, getRunActions());
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
    private static Icon getTestIcon(@NotNull PsiElement e, String ginkgoSpec) {
        Icon icon = AllIcons.RunConfigurations.TestState.Run;
        if (getSpec(ginkgoSpec) == IT) {
            List<String> specNames = GinkgoUtil.getSpecNames(e, false);
            String specName = specNames.remove(specNames.size() - 1);
            String specContext = String.join(" ", specNames);
            String testUrl = "gotest://" + GinkgoRunConfigurationProducer.GINKGO + "#" + specContext + "/" + specName;
            TestStateStorage.Record record = TestStateStorage.getInstance(e.getProject()).getState(testUrl);
            if (record != null) {
                icon = getTestStateIcon(record, false);
            }
        }
        return icon;
    }
}
