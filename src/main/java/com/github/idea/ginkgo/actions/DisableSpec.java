package com.github.idea.ginkgo.actions;

import com.github.idea.ginkgo.GinkgoSpecType;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.execution.Location;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import static com.github.idea.ginkgo.util.GinkgoUtil.getGinkgoSpecType;

public class DisableSpec extends AnAction {

    public DisableSpec() {
        super("Disable test", "Disable test", AllIcons.RunConfigurations.TestIgnored);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Location<?> location = e.getData(Location.DATA_KEY);
        PsiElement psiElement = location.getPsiElement();
        GinkgoSpecType ginkgoSpec = getGinkgoSpecType(psiElement.getText());
        PsiElement newElement = GoElementFactory.createIdentifierFromText(e.getProject(), ginkgoSpec.getPendingSpecType().specType());

        ApplicationManager.getApplication().runWriteAction(() ->
            CommandProcessor.getInstance().runUndoTransparentAction(() ->
                psiElement.replace(newElement))
        );
    }

    @Override
    public boolean isDumbAware() {
        return super.isDumbAware();
    }
}
