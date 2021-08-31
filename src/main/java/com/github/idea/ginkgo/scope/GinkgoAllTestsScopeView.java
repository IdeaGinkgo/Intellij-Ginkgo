package com.github.idea.ginkgo.scope;

import com.github.idea.ginkgo.GinkgoRunConfigurationOptions;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class GinkgoAllTestsScopeView implements GinkgoScopeView {
    @Override
    public JComponent getComponent() {
        return new FormBuilder().setAlignLabelOnRight(false).getPanel();
    }

    @Override
    public void resetFrom(GinkgoRunConfigurationOptions settings) {
    }

    @Override
    public void applyTo(GinkgoRunConfigurationOptions settings) {
    }
}
