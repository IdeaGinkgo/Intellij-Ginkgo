package com.github.idea.ginkgo;

import com.github.idea.ginkgo.util.GinkgoUtil;
import com.goide.GoIcons;
import com.goide.GoTypes;
import com.goide.execution.testing.GoTestFinder;
import com.goide.psi.GoCallExpr;
import com.goide.util.GoUtil;
import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class GinkgoRunLineMarkerProvider extends RunLineMarkerContributor {
    private static final Function<PsiElement, String> TOOLTIP_PROVIDER = element -> "Ginkgo Test";

    @Override
    @Nullable
    public Info getInfo(@NotNull PsiElement e) {
        PsiFile file = e.getContainingFile();
        if (!GoTestFinder.isTestFile(file) || !GoUtil.isInProject(file) || InjectedLanguageManager.getInstance(e.getProject()).isInjectedFragment(file)) {
            return null;
        }

        if (e.getNode().getElementType() == GoTypes.IDENTIFIER && e.getParent().getParent() instanceof GoCallExpr) {
            GoCallExpr parent = (GoCallExpr) e.getParent().getParent();
            if (GinkgoUtil.isGinkgoFunction(parent.getExpression().getText())) {
                AnAction[] runActions = ExecutorAction.getActions(0);
                runActions = ArrayUtil.append(runActions, ActionManager.getInstance().getAction("GinkgoDisableSpec"));
                return new Info(AllIcons.RunConfigurations.TestState.Run, TOOLTIP_PROVIDER, runActions);
            }

            if (GinkgoUtil.isGinkgoPendingFunction(parent.getExpression().getText())) {
                return new Info(GoIcons.Helper.createIconWithShift(AllIcons.RunConfigurations.TestIgnored, AllIcons.Nodes.RunnableMark), TOOLTIP_PROVIDER, ActionManager.getInstance().getAction("GinkgoEnableSpec"));
            }
        }

        return null;
    }
}
