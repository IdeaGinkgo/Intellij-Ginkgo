package com.github.taylorono.intellij.ginkgo.file;


import com.github.taylorono.intellij.ginkgo.icons.GinkgoIcons;
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
        return GinkgoIcons.INSTANCE.getGinkgo();
    }

    public GinkgoFileType() {
        super(GoLanguage.INSTANCE, true);
    }
}