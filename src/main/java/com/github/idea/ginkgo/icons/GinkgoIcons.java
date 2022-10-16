package com.github.idea.ginkgo.icons;

import com.goide.GoIcons;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Supplier;

public final class GinkgoIcons implements Supplier<Icon> {
    @NotNull
    public static final GinkgoIcons INSTANCE = new GinkgoIcons();
    public static final Icon DISABLED_TEST_ICON = GoIcons.Helper.createIconWithShift(AllIcons.RunConfigurations.TestIgnored, AllIcons.Nodes.RunnableMark);
    public static final Icon DISABLE_SPEC_ICON = AllIcons.RunConfigurations.TestState.Yellow2;

    private Icon ginkgo;

    @NotNull
    private final Icon load(String path) {
        return IconLoader.getIcon(path, GinkgoIcons.class);
    }

    @Override
    public Icon get() {
        if (ginkgo == null) {
            ginkgo = this.load("/icons/ginkgo.png");
        }
        return ginkgo;
    }
}
