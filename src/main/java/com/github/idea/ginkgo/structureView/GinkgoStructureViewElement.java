package com.github.idea.ginkgo.structureView;

import com.github.idea.ginkgo.GinkgoExpression;
import com.github.idea.ginkgo.icons.GinkgoIcons;
import com.goide.GoLanguage;
import com.goide.psi.GoCallExpr;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.github.idea.ginkgo.GinkgoExpression.fromGoCallExpr;
import static com.github.idea.ginkgo.util.GinkgoUtil.isGinkgoTestFile;

public class GinkgoStructureViewElement implements StructureViewTreeElement {
    public static final String GINKGO_PRESENTATION = "Ginkgo %s";
    private final NavigatablePsiElement myElement;

    public GinkgoStructureViewElement(NavigatablePsiElement element) {
        this.myElement = element;
    }

    @Override
    public Object getValue() {
        return myElement;
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        GoCallExpr goCallExpr = ObjectUtils.tryCast(myElement, GoCallExpr.class);

        //if myElement is not a goCallExpr it is the root test file
        if (goCallExpr == null) {
            return new PresentationData(String.format(GINKGO_PRESENTATION, myElement.getContainingFile().getName()), "", GinkgoIcons.INSTANCE.get(), null);
        }

        GinkgoExpression ginkgoExpression = fromGoCallExpr(goCallExpr);
        return ginkgoExpression.isValid() ? ginkgoExpression.getPresentationData() : new PresentationData();
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        if (!myElement.getLanguage().is(GoLanguage.INSTANCE)) {
            return EMPTY_ARRAY;
        }

        PsiFile file = myElement.getContainingFile();
        if (!isGinkgoTestFile(file)) {
            return EMPTY_ARRAY;
        }

        ArrayList<TreeElement> treeChildren = new ArrayList<>();
        myElement.acceptChildren(new ginkgoTestElementWalker(treeChildren));
        return treeChildren.toArray(new TreeElement[0]);
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

    private static class ginkgoTestElementWalker extends PsiRecursiveElementWalkingVisitor {
        private final ArrayList<TreeElement> treeChildren;

        public ginkgoTestElementWalker(ArrayList<TreeElement> treeChildren) {
            this.treeChildren = treeChildren;
        }

        @Override
        public void visitElement(@NotNull PsiElement element) {
            GoCallExpr goCallExpr = ObjectUtils.tryCast(element, GoCallExpr.class);
            if (goCallExpr == null) {
                super.visitElement(element);
                return;
            }

            GinkgoExpression ginkgoExpression = fromGoCallExpr(goCallExpr);
            if (!ginkgoExpression.isValid()) {
                super.visitElement(element);
                return;
            }

            treeChildren.add(new GinkgoStructureViewElement((NavigatablePsiElement) element));
        }
    }
}
