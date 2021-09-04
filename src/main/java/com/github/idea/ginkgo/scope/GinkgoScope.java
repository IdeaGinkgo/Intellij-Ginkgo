package com.github.idea.ginkgo.scope;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum GinkgoScope {
    All("All Tests") {
        @Override
        public GinkgoScopeView createView(Project project) {
            return new GinkgoAllTestsScopeView();
        }
    },
    FOCUS("Focus") {
        @Override
        public GinkgoScopeView createView(Project project) {
            return new GinkgoFocusScopeView();
        }
    };

    private final String label;

    GinkgoScope(String label) {
        this.label = label;
    }

    public static GinkgoScope valueOfLabel(String label) {
        return Arrays.stream(values()).filter(l -> l.label == label).findFirst().get();
    }

    public String getLabel() {
        return label;
    }

    public abstract GinkgoScopeView createView(@NotNull Project project);
}
