package com.github.idea.ginkgo.file;


import com.github.idea.ginkgo.icons.GinkgoIcons;
import com.goide.GoLanguage;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class GinkgoFileType extends LanguageFileType implements FileType {
    @NotNull
    public String getName() {
        return "Ginkgo";
    }

    @NotNull
    public String getDescription() {
        return "Ginkgo Spec";
    }

    @NotNull
    public String getDefaultExtension() {
        return "go";
    }

    @NotNull
    public Icon getIcon() {
        return GinkgoIcons.INSTANCE.get();
    }

    public GinkgoFileType() {
        super(GoLanguage.INSTANCE, true);
    }
}