package com.github.idea.ginkgo.icons;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Supplier;

public final class GinkgoIcons implements Supplier<Icon> {
    @NotNull
    public static final GinkgoIcons INSTANCE = new GinkgoIcons();

    @NotNull
    private static Icon Ginkgo;

    @NotNull
    private final Icon load(String path) {
        return IconLoader.getIcon(path, GinkgoIcons.class);
    }

    @Override
    public Icon get() {
        if (Ginkgo == null) {
            Ginkgo = this.load("/icons/ginkgo.png");
        }
        return Ginkgo;
    }
}
