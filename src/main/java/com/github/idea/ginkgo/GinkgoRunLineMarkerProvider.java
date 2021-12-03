package com.github.idea.ginkgo;

import com.github.idea.ginkgo.util.GinkgoUtil;
import com.goide.GoIcons;
import com.goide.GoTypes;
import com.goide.execution.testing.GoTestFinder;
import com.goide.psi.GoCallExpr;
import com.goide.psi.GoReferenceExpression;
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

import javax.swing.*;


public class GinkgoRunLineMarkerProvider extends RunLineMarkerContributor {
    private static final Function<PsiElement, String> TOOLTIP_PROVIDER = element -> "Ginkgo Test";
    private static final Function<PsiElement, String> TOOLTIP_WARNING = element -> "Unable to focus dynamic test names";
    private static final Icon disabledTestIcon = GoIcons.Helper.createIconWithShift(AllIcons.RunConfigurations.TestIgnored, AllIcons.Nodes.RunnableMark);;

    @Override
    @Nullable
    public Info getInfo(@NotNull PsiElement e) {
        PsiFile file = e.getContainingFile();
        if (!GoTestFinder.isTestFile(file) || !GoUtil.isInProject(file) || InjectedLanguageManager.getInstance(e.getProject()).isInjectedFragment(file)) {
            return null;
        }

        if (e.getNode().getElementType() == GoTypes.IDENTIFIER && e.getParent().getParent() instanceof GoCallExpr) {
            GoCallExpr parent = (GoCallExpr) e.getParent().getParent();
            String name = parent.getExpression().getText();

            if (GinkgoUtil.isTableEntity(name) && parent.getArgumentList().getExpressionList().size() >= 2) {
                if (parent.getArgumentList().getExpressionList().get(0) instanceof GoReferenceExpression) {
                    return new Info(AllIcons.RunConfigurations.TestState.Yellow2, TOOLTIP_WARNING, ActionManager.getInstance().getAction("GinkgoDisableSpec"));
                }
            }

            if (GinkgoUtil.isTablePendingEntity(name) && parent.getArgumentList().getExpressionList().size() >= 2) {
                if (parent.getArgumentList().getExpressionList().get(0) instanceof GoReferenceExpression) {
                    return new Info(disabledTestIcon, TOOLTIP_PROVIDER, ActionManager.getInstance().getAction("GinkgoEnableSpec"));
                }
            }

            if (GinkgoUtil.isGinkgoFunction(name) && parent.getArgumentList().getExpressionList().size() >= 2) {
                AnAction[] runActions = ExecutorAction.getActions(0);
                runActions = ArrayUtil.append(runActions, ActionManager.getInstance().getAction("GinkgoDisableSpec"));
                return new Info(AllIcons.RunConfigurations.TestState.Run, TOOLTIP_PROVIDER, runActions);
            }

            if (GinkgoUtil.isGinkgoPendingFunction(parent.getExpression().getText())) {
                return new Info(disabledTestIcon, TOOLTIP_PROVIDER, ActionManager.getInstance().getAction("GinkgoEnableSpec"));
            }
        }

        return null;
    }
}
