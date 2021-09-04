package com.github.idea.ginkgo.scope;

import com.github.idea.ginkgo.GinkgoRunConfigurationOptions;

import javax.swing.*;

public interface GinkgoScopeView {
    JComponent getComponent();

    void resetFrom(GinkgoRunConfigurationOptions settings);

    void applyTo(GinkgoRunConfigurationOptions settings);
}
