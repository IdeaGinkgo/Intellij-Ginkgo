package com.github.idea.ginkgo;

import com.github.idea.ginkgo.util.GinkgoUtil;

import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.icons.AllIcons;
import com.intellij.lang.Language;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.goide.GoLanguage;
import com.goide.GoIcons;
import com.goide.editor.GoBreadcrumbsProvider;
import com.goide.execution.testing.GoTestFinder;
import com.goide.psi.*;
import com.goide.util.GoUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class GinkgoBreadcrumbsProvider implements BreadcrumbsProvider {
    private static final Language[] LANGUAGES = {GoLanguage.INSTANCE};
    private static final Icon disabledTestIcon = GoIcons.Helper.createIconWithShift(AllIcons.RunConfigurations.TestIgnored, AllIcons.Nodes.RunnableMark);

    public static @Nullable BreadcrumbsProvider getGoBreadcrumbProvider() {
        BreadcrumbsProvider[] providers = BreadcrumbsProvider.EP_NAME.getExtensions();
        for (BreadcrumbsProvider provider : providers) {
            if (provider instanceof GoBreadcrumbsProvider) {
                return provider;
            }
        }
        return null;
    }

    @Override
    public Language[] getLanguages() {
        return LANGUAGES;
    }

    @Override
    public boolean acceptElement(@NotNull PsiElement e) {
        GoCallExpr call = extractGinkgoMethodCall(e);
        if (call != null && isGinkgoTestCall(call)) {
            return true;
        }
        if (call != null && isGinkgoTestSetupCall(call)) {
            return true;
        }

        // fallthrough to go breadcrumbs
        // https://youtrack.jetbrains.com/issue/IJSDK-1383
        return Objects.requireNonNull(getGoBreadcrumbProvider()).acceptElement(e);
    }

    @Override
    public @NotNull @NlsSafe String getElementInfo(@NotNull PsiElement e) {
        GoCallExpr call = extractGinkgoMethodCall(e);
        if (call != null && isGinkgoTestCall(call)) {
            String methodName = extractMethodName(call);
            GoStringLiteral firstArg = (GoStringLiteral) call.getArgumentList().getExpressionList().get(0);
            return methodName + " " + firstArg.getDecodedText();
        }
        if (call != null && isGinkgoTestSetupCall(call)) {
            return Objects.requireNonNull(extractMethodName(call));
        }

        // fallthrough to go breadcrumbs
        // https://youtrack.jetbrains.com/issue/IJSDK-1383
        return Objects.requireNonNull(getGoBreadcrumbProvider()).getElementInfo(e);
    }

    @Override
    public @Nullable Icon getElementIcon(@NotNull PsiElement e) {
        GoCallExpr call = extractGinkgoMethodCall(e);
        if (call == null) {
            return BreadcrumbsProvider.super.getElementIcon(e);
        }

        String name = extractMethodName(call);
        List<GoExpression> args = call.getArgumentList().getExpressionList();
        if (GinkgoUtil.isTableEntity(name) && args.size() >= 2) {
            if (args.get(0) instanceof GoReferenceExpression) {
                return AllIcons.RunConfigurations.TestState.Yellow2;
            }
        }

        if (GinkgoUtil.isTablePendingEntity(name) && args.size() >= 2) {
            if (args.get(0) instanceof GoReferenceExpression) {
                return disabledTestIcon;
            }
        }

        if (GinkgoUtil.isGinkgoFunction(name) && args.size() >= 2) {
            return AllIcons.RunConfigurations.TestState.Run;
        }

        if (GinkgoUtil.isGinkgoPendingFunction(name)) {
            return disabledTestIcon;
        }

        return BreadcrumbsProvider.super.getElementIcon(e);
    }

    @Override
    public @NotNull List<? extends Action> getContextActions(@NotNull PsiElement e) {
        GoCallExpr call = extractGinkgoMethodCall(e);
        if (call == null) {
            return BreadcrumbsProvider.super.getContextActions(e);
        }

        String name = extractMethodName(call);
        List<GoExpression> args = call.getArgumentList().getExpressionList();
        if (GinkgoUtil.isTableEntity(name) && args.size() >= 2) {
            if (args.get(0) instanceof GoReferenceExpression) {
                return convertIdeaActionToSwingAction(e, new AnAction[]{ActionManager.getInstance().getAction("GinkgoDisableSpec")});
            }
        }

        if (GinkgoUtil.isTablePendingEntity(name) && args.size() >= 2) {
            if (args.get(0) instanceof GoReferenceExpression) {
                return convertIdeaActionToSwingAction(e, new AnAction[]{ActionManager.getInstance().getAction("GinkgoEnableSpec")});
            }
        }

        if (GinkgoUtil.isGinkgoFunction(name) && args.size() >= 2) {
            AnAction[] runActions = ExecutorAction.getActions(0);
            ArrayUtil.append(runActions, ActionManager.getInstance().getAction("GinkgoDisableSpec"));
            return convertIdeaActionToSwingAction(e, runActions);
        }

        if (GinkgoUtil.isGinkgoPendingFunction(name)) {
            return convertIdeaActionToSwingAction(e, new AnAction[]{ActionManager.getInstance().getAction("GinkgoEnableSpec")});
        }
        return BreadcrumbsProvider.super.getContextActions(e);
    }

    private @Nullable GoCallExpr extractGinkgoMethodCall(@NotNull PsiElement e) {
        PsiFile file = e.getContainingFile();
        if (!GoTestFinder.isTestFile(file) || !GoUtil.isInProject(file) || InjectedLanguageManager.getInstance(e.getProject()).isInjectedFragment(file)) {
            return null;
        }

        // we expect the element to be an anonymous function literal
        // with the parent function call expression being the call to Ginkgo
        if (!(e instanceof GoFunctionLit) || !(e.getParent() instanceof GoArgumentList) || !(e.getParent().getParent() instanceof GoCallExpr)) {
            return null;
        }
        return (GoCallExpr) e.getParent().getParent();
    }

    private @Nullable String extractMethodName(@NotNull GoCallExpr call) {
        if (!(call.getExpression() instanceof GoReferenceExpression)) {
            return null;
        }
        GoReferenceExpression callRefExpr = (GoReferenceExpression) call.getExpression();
        return callRefExpr.getIdentifier().getText();
    }

    private boolean isGinkgoTestCall(@NotNull GoCallExpr call) {
        String methodName = extractMethodName(call);
        if (methodName == null) {
            return false;
        }

        List<GoExpression> args = call.getArgumentList().getExpressionList();
        if (args.size() < 1 || !(args.get(0) instanceof GoStringLiteral)) {
            return false;
        }

        return GinkgoUtil.isTableEntity(methodName) ||
            GinkgoUtil.isTablePendingEntity(methodName) ||
            GinkgoUtil.isGinkgoFunction(methodName) ||
            GinkgoUtil.isGinkgoPendingFunction(methodName);
    }

    private boolean isGinkgoTestSetupCall(@NotNull GoCallExpr call) {
        String methodName = extractMethodName(call);
        if (methodName == null) {
            return false;
        }

        return GinkgoUtil.isGinkgoTestSetup(methodName);
    }

    private List<? extends Action> convertIdeaActionToSwingAction(PsiElement element, AnAction[] anActions) {
        DataContext dataContext = dataId -> {
            if (CommonDataKeys.PROJECT.is(dataId)) {
                return element.getProject();
            } else if (CommonDataKeys.VIRTUAL_FILE.is(dataId)) {
                return element.getContainingFile().getVirtualFile();
            } else if (LangDataKeys.MODULE.is(dataId)) {
                return ModuleUtilCore.findModuleForPsiElement(element);
            } else if (Location.DATA_KEY.is(dataId)) {
                return PsiLocation.fromPsiElement(element);
            }
            return null;
        };

        return Arrays.stream(anActions).
                filter(anAction -> !(anAction instanceof SplitButtonAction)).
                flatMap(anAction -> {
                    AnActionEvent actionEvent = AnActionEvent.createFromDataContext(ActionPlaces.TOOLBAR, anAction.getTemplatePresentation(), dataContext);

                    try {
                        if (anAction instanceof ExecutorAction) {
                            ExecutorAction executorAction = (ExecutorAction) anAction;
                            executorAction.update(actionEvent);
                            if (!executorAction.canBePerformed(dataContext) || !executorAction.getTemplatePresentation().isVisible()) {
                                return Stream.empty();
                            }
                        }
                    } catch(IndexNotReadyException ignored) {
                        return Stream.empty();
                    }

                    AbstractAction abstractAction = new AbstractAction(anAction.getTemplateText(), anAction.getTemplatePresentation().getIcon()) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            UIUtil.invokeLaterIfNeeded(() -> {
                                anAction.actionPerformed(actionEvent);
                            });
                        }
                    };
                    abstractAction.setEnabled(anAction.getTemplatePresentation().isEnabled());
                    return Stream.of(abstractAction);
                }).
                collect(Collectors.toList());
    }
}
