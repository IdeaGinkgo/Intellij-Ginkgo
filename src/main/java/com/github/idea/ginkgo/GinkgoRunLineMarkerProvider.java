package com.github.idea.ginkgo;

import com.github.idea.ginkgo.util.GinkgoUtil;
import com.goide.GoIcons;
import com.goide.GoTypes;
import com.goide.psi.GoCallExpr;
import com.goide.psi.GoExpression;
import com.goide.psi.GoFile;
import com.goide.psi.GoReferenceExpression;
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


public class GinkgoRunLineMarkerProvider extends RunLineMarkerContributor {
    private static final Function<PsiElement, String> TOOLTIP_PROVIDER = element -> "Ginkgo Test";
    private static final Function<PsiElement, String> TOOLTIP_WARNING = element -> "Unable to focus dynamic test names";
    private static final Icon DISABLED_TEST_ICON = GoIcons.Helper.createIconWithShift(AllIcons.RunConfigurations.TestIgnored, AllIcons.Nodes.RunnableMark);
    private static final Icon DISABLE_SPEC_ICON = AllIcons.RunConfigurations.TestState.Yellow2;
    private static final AnAction ENABLE_SPEC_ACTION = ActionManager.getInstance().getAction("GinkgoEnableSpec");
    private static final AnAction DISABLE_SPEC_ACTION = ActionManager.getInstance().getAction("GinkgoDisableSpec");
    @Override
    @Nullable
    public Info getInfo(@NotNull PsiElement e) {
        PsiFile file = e.getContainingFile();
        if (!isGinkgoTestFile(file)) {
            return null;
        }

        if (e.getNode().getElementType() == GoTypes.IDENTIFIER && e.getParent().getParent() instanceof GoCallExpr) {
            GoCallExpr parent = (GoCallExpr) e.getParent().getParent();
            List<GoExpression> args = parent.getArgumentList().getExpressionList();
            String ginkgoSpec = parent.getExpression().getText();

            if (GinkgoUtil.isTablePendingEntity(ginkgoSpec) && args.size() >= 2 && args.get(0) instanceof GoReferenceExpression) {
                return new Info(DISABLED_TEST_ICON, TOOLTIP_PROVIDER, ENABLE_SPEC_ACTION);
            }

            if (GinkgoUtil.isTableEntity(ginkgoSpec) && args.size() >= 2 && args.get(0) instanceof GoReferenceExpression) {
                return new Info(DISABLE_SPEC_ICON, TOOLTIP_WARNING, DISABLE_SPEC_ACTION);
            }

            if (GinkgoUtil.isGinkgoPendingFunction(ginkgoSpec)) {
                return new Info(DISABLED_TEST_ICON, TOOLTIP_PROVIDER, ENABLE_SPEC_ACTION);
            }

            if (GinkgoUtil.isGinkgoFunction(ginkgoSpec) && args.size() >= 2) {
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
        if (ginkgoSpec.equalsIgnoreCase(GinkgoSpecType.IT.specType())) {
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

    public boolean isGinkgoTestFile(PsiFile file) {
        return file instanceof GoFile && importsGinkgo((GoFile) file);
    }

    private boolean importsGinkgo(GoFile file) {
        return file.getImports().stream().anyMatch(i -> i.getPath().contains("github.com/onsi/ginkgo"));
    }
}
