package com.github.idea.ginkgo.actions;

import com.github.idea.ginkgo.GinkgoSpec;
import com.github.idea.ginkgo.GinkgoSpecs;
import com.goide.psi.GoLiteral;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.execution.Location;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class DisableSpec extends AnAction {

    public DisableSpec() {
        super("Disable test", "Disable test", AllIcons.RunConfigurations.TestIgnored);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Location<?> location = e.getData(Location.DATA_KEY);
        assert location != null;
        PsiElement psiElement = location.getPsiElement();
        GinkgoSpec spec = GinkgoSpecs.getSpec(psiElement.getText());
        PsiElement newElement = GoElementFactory.createIdentifierFromText(psiElement.getProject(), spec.getDisabledName());

        ApplicationManager.getApplication().runWriteAction(() ->
            CommandProcessor.getInstance().runUndoTransparentAction(() ->
                psiElement.replace(newElement))
        );
    }
}
