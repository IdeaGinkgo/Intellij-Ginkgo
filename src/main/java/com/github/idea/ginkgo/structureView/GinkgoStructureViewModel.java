package com.github.idea.ginkgo.structureView;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.psi.PsiFile;

public class GinkgoStructureViewModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {
    public GinkgoStructureViewModel(PsiFile psiFile) {
        super(psiFile, new GinkgoStructureViewElement(psiFile));
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return false;//element.getValue() instanceof SimpleProperty;
    }
}
