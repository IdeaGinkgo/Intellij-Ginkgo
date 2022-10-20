package com.github.idea.ginkgo;

import com.github.idea.ginkgo.util.GinkgoUtil;
import com.goide.GoLanguage;
import com.goide.psi.GoCallExpr;
import com.intellij.icons.AllIcons;
import com.intellij.lang.Language;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.github.idea.ginkgo.GinkgoExpression.INVALID_SPEC;
import static com.github.idea.ginkgo.GinkgoExpression.fromGoCallExpr;
import static com.github.idea.ginkgo.icons.GinkgoIcons.DISABLED_TEST_ICON;
import static com.github.idea.ginkgo.icons.GinkgoIcons.DISABLE_SPEC_ICON;


public class GinkgoBreadcrumbsProvider implements BreadcrumbsProvider {
    private static final Language[] LANGUAGES = {GoLanguage.INSTANCE};

    @Override
    public Language[] getLanguages() {
        return LANGUAGES;
    }

    @Override
    public boolean acceptElement(@NotNull PsiElement e) {
        GinkgoExpression ginkgoExpression = getGinkgoExpression(e);
        return ginkgoExpression.isValid();
    }

    @Override
    public @NotNull @NlsSafe String getElementInfo(@NotNull PsiElement e) {
        GinkgoExpression ginkgoExpression = getGinkgoExpression(e);
        if (!ginkgoExpression.isValid()) {
            return "";
        }

        return ginkgoExpression.isDynamicTableEntry() ?
                ginkgoExpression.getSpecType():
                ginkgoExpression.getSpecType() + " " + ginkgoExpression.getSpecName();
    }

    @Override
    public @Nullable Icon getElementIcon(@NotNull PsiElement e) {
        PsiFile file = e.getContainingFile();
        if (!GinkgoUtil.isGinkgoTestFile(file)) {
            return null;
        }

        GinkgoExpression ginkgoExpression = getGinkgoExpression(e);
        if (ginkgoExpression.isValid()) {
            if (ginkgoExpression.isDynamicTableEntry()) {
                return DISABLE_SPEC_ICON;
            }

            if (!ginkgoExpression.isActive()) {
                return DISABLED_TEST_ICON;
            }

            if (ginkgoExpression.isActive()) {
                return AllIcons.RunConfigurations.TestState.Run;
            }
        }

        return null;
    }

    private static GinkgoExpression getGinkgoExpression(@NotNull PsiElement e) {
        return e instanceof GoCallExpr ? fromGoCallExpr((GoCallExpr) e) : INVALID_SPEC;
    }
}
