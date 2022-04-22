package com.github.idea.ginkgo.structureView;

import com.github.idea.ginkgo.GinkgoRunConfigurationProducer;
import com.github.idea.ginkgo.util.GinkgoUtil;
import com.goide.GoLanguage;
import com.goide.execution.testing.GoTestFinder;
import com.goide.psi.GoCallExpr;
import com.goide.psi.GoExpression;
import com.goide.psi.GoStringLiteral;
import com.goide.util.GoUtil;
import com.intellij.execution.TestStateStorage;
import com.intellij.execution.testframework.TestIconMapper;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GinkgoStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private final NavigatablePsiElement myElement;

    public GinkgoStructureViewElement(NavigatablePsiElement element) {
        this.myElement = element;
    }

    @Override
    public Object getValue() {
        return myElement;
    }

    @Override
    public void navigate(boolean requestFocus) {
        myElement.navigate(requestFocus);
    }


    @Override
    public boolean canNavigate() {
        return myElement.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return myElement.canNavigateToSource();
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        String name = myElement.getName();
        return name != null ? name : "";
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        GoCallExpr goCallExpr = ObjectUtils.tryCast(myElement, GoCallExpr.class);
        if (goCallExpr == null) {
            ItemPresentation presentation = myElement.getPresentation();
            return presentation != null ? presentation : new PresentationData();
        } else {
            String methodName = goCallExpr.getExpression().getText();
            String presentableName = methodName;
            GoStringLiteral firstStringArg = ObjectUtils.tryCast(goCallExpr.getArgumentList().getExpressionList().get(0), GoStringLiteral.class);
            if (firstStringArg != null) {
                presentableName += " " + firstStringArg.getDecodedText();
            }

            List<String> specNames = GinkgoUtil.getSpecNames(myElement, false);
            String specName = specNames.remove(specNames.size() - 1);
            String specContext = String.join(" ", specNames);
            String testUrl = "gotest://" + GinkgoRunConfigurationProducer.GINKGO + "#" + specContext + "/" + specName;

            boolean isPending = GinkgoUtil.isGinkgoPendingFunction(methodName) || GinkgoUtil.isTablePendingEntity(methodName);
            Icon icon = isPending ? AllIcons.RunConfigurations.TestIgnored : AllIcons.RunConfigurations.TestState.Run;
            TestStateStorage.Record record = TestStateStorage.getInstance(myElement.getProject()).getState(testUrl);
            if (record != null) {
                icon = TestIconMapper.getIcon(TestIconMapper.getMagnitude(record.magnitude));
            }

            return new PresentationData(presentableName, "", icon, null);
        }
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        if (!myElement.getLanguage().is(GoLanguage.INSTANCE)) {
            return EMPTY_ARRAY;
        }

        PsiFile file = myElement.getContainingFile();
        if (!GoTestFinder.isTestFile(file) || !GoUtil.isInProject(file) || InjectedLanguageManager.getInstance(myElement.getProject()).isInjectedFragment(file)) {
            return EMPTY_ARRAY;
        }

        ArrayList<TreeElement> treeChildren = new ArrayList<>();

        myElement.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                GoCallExpr goCallExpr = ObjectUtils.tryCast(element, GoCallExpr.class);
                if (goCallExpr == null) {
                    super.visitElement(element);
                } else {
                    List<GoExpression> args = goCallExpr.getArgumentList().getExpressionList();
                    String name = goCallExpr.getExpression().getText();

                    if ((GinkgoUtil.isTableEntity(name) && args.size() >= 2) ||
                            (GinkgoUtil.isTablePendingEntity(name) && args.size() >= 2) ||
                            (GinkgoUtil.isGinkgoFunction(name) && args.size() >= 2) ||
                            (GinkgoUtil.isGinkgoPendingFunction(name))) {
                        treeChildren.add(new GinkgoStructureViewElement((NavigatablePsiElement) element));
                    } else {
                        super.visitElement(element);
                    }
                }
            }
        });

        return treeChildren.toArray(new TreeElement[0]);
    }
}
