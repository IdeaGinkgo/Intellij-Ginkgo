package com.github.idea.ginkgo.execution.testing.creator;

import com.github.idea.ginkgo.icons.GinkgoIcons;
import com.goide.execution.testing.creator.GoTestCreator;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Properties;

public class GinkgoFileTestCreator extends GoTestCreator {

    private static final Logger LOG = Logger.getInstance(GinkgoFileTestCreator.class);

    @Override
    public @NlsSafe @Nullable String getPresentableText() {
        return "Ginkgo Test";
    }

    @Nullable
    public Icon getIcon(boolean unused) {
        return GinkgoIcons.INSTANCE.get();
    }

    @Override
    public void createTest(Project project, Editor editor, PsiFile file) {
        PsiDirectory directory = file.getContainingDirectory();
        if (directory != null) {
            String testFileName = FileUtil.getNameWithoutExtension(file.getName()) + "_test.go";
            PsiFile testFile = directory.findFile(testFileName);
            if (testFile == null) {
                Properties properties = new Properties(FileTemplateManager.getInstance(project).getDefaultProperties());
                FileTemplate template = FileTemplateManager.getInstance(project).getInternalTemplate("Go File");

                try {
                    FileTemplateUtil.createFromTemplate(template, testFileName, properties, directory);
                } catch (Exception exception) {
                    LOG.warn("Failed to create a new file from a template", exception);
                }
            }

            if (testFile != null) {
                FileEditorManager.getInstance(project).openFile((testFile).getVirtualFile(), true);
            }
        }
    }
}
