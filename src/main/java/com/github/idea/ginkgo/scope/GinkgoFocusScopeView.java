package com.github.idea.ginkgo.scope;

import com.github.idea.ginkgo.GinkgoRunConfigurationOptions;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class GinkgoFocusScopeView implements GinkgoScopeView {
    private final JBTextField focusExpression;
    private final JPanel panel;

    public GinkgoFocusScopeView() {
        focusExpression = new JBTextField();
        panel = new FormBuilder()
                .setAlignLabelOnRight(false)
                .addLabeledComponent("Focus expression", focusExpression)
                .getPanel();
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void resetFrom(GinkgoRunConfigurationOptions settings) {
        focusExpression.setText(settings.getFocusTestExpression());
    }

    @Override
    public void applyTo(GinkgoRunConfigurationOptions settings) {
        settings.setFocusTestExpression(focusExpression.getText());
    }
}
