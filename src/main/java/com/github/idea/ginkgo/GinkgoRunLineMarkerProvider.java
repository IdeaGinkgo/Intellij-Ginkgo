package com.github.idea.ginkgo;

import com.github.idea.ginkgo.util.GinkgoUtil;
import com.goide.GoTypes;
import com.goide.execution.testing.GoTestFinder;
import com.goide.psi.GoCallExpr;
import com.goide.util.GoUtil;
import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class GinkgoRunLineMarkerProvider extends RunLineMarkerContributor {
    private static final Function<PsiElement, String> TOOLTIP_PROVIDER = (element) -> "Ginkgo Test";

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
                return new Info(AllIcons.RunConfigurations.TestState.Run, TOOLTIP_PROVIDER, ExecutorAction.getActions(0));
            }
        }

        return null;
    }
}
