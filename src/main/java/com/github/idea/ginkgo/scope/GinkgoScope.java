package com.github.idea.ginkgo.scope;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum GinkgoScope {
    ALL("All Tests") {
        @Override
        public GinkgoScopeView createView(@NotNull Project project) {
            return new GinkgoAllTestsScopeView();
        }
    },
    FOCUS("Focus") {
        @Override
        public GinkgoScopeView createView(@NotNull Project project) {
            return new GinkgoFocusScopeView();
        }
    };

    private final String label;

    GinkgoScope(String label) {
        this.label = label;
    }

    public static GinkgoScope valueOfLabel(String label) {
        return Arrays
                .stream(values())
                .filter(l -> l.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new EnumConstantNotPresentException(GinkgoScope.class, label));
    }

    public String getLabel() {
        return label;
    }

    public abstract GinkgoScopeView createView(@NotNull Project project);
}
