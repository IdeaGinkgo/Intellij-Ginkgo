package com.github.idea.ginkgo;

import com.intellij.filePrediction.features.history.FileHistoryManagerWrapper;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.ex.FileEditorWithProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GinkgoInitializer implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        project.getMessageBus().connect().subscribe(FileHistoryManagerWrapper.EditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void fileOpenedSync(@NotNull FileEditorManager source, @NotNull VirtualFile file, @NotNull List<FileEditorWithProvider> editorsWithProviders) {
                System.out.println("fileOpenedSync" + file.getCanonicalFile());
            }

            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                System.out.println("fileOpened" + file.getCanonicalFile());
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                System.out.println("fileOpened" + file.getCanonicalFile());
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                System.out.println("selectionChanged" + event.getNewFile().getCanonicalPath());
            }
        });
    }
}
