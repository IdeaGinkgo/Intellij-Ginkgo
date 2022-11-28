package com.github.idea.ginkgo.structureView;

import com.github.idea.ginkgo.config.GinkgoSettings;
import com.goide.tree.GoStructureViewFactory;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.idea.ginkgo.util.GinkgoUtil.isGinkgoTestFile;

public class GinkgoStructureViewFactory implements PsiStructureViewFactory {
    @Override
    public @Nullable StructureViewBuilder getStructureViewBuilder(@NotNull PsiFile psiFile) {
        GinkgoSettings settings = GinkgoSettings.getInstance();
        if (isGinkgoTestFile(psiFile) && settings.isGinkgoStructViewEnabled()) {
            return new TreeBasedStructureViewBuilder() {
                public @NotNull StructureViewModel createStructureViewModel(@Nullable Editor editor) {
                    return new GinkgoStructureViewModel(psiFile);
                }
            };
        }

        //we should fall back to GoStructViewFactory
        return new GoStructureViewFactory().getStructureViewBuilder(psiFile);
    }
}
