package com.github.idea.ginkgo.tree;

import com.github.idea.ginkgo.icons.GinkgoIcons;
import com.github.idea.ginkgo.structureView.GinkgoStructureViewFactory;
import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.util.treeView.AbstractTreeStructure;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.impl.ContentImpl;
import com.intellij.ui.tree.AsyncTreeModel;
import com.intellij.ui.tree.StructureTreeModel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.concurrency.Invoker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GinkgoTreeToolWindowFactory implements ToolWindowFactory {
    @Override
    public boolean isApplicable(@NotNull Project project) {
        return ToolWindowFactory.super.isApplicable(project);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JScrollPane panel = ScrollPaneFactory.createScrollPane();
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);

        Tree tree = createTestTree(content, project);
    }

    private Tree createTestTree(Content content, Project project) {
        GinkgoTestTree testTree = new GinkgoTestTree();
        StructureTreeModel<AbstractTreeStructure> treeModel = new StructureTreeModel<>(
                testTree, null, Invoker.forBackgroundPoolWithReadAction(project), project);

        Tree tree = new Tree(new AsyncTreeModel(treeModel, content));
        tree.setCellRenderer(new NodeRenderer());
        return tree;
    }

    @Override
    public @Nullable Icon getIcon() {
        return GinkgoIcons.INSTANCE.get();
    }
}
