package com.github.idea.ginkgo.scope;

import com.github.idea.ginkgo.GinkgoRunConfigurationOptions;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class GinkgoFocusScopeView implements GinkgoScopeView {
    private final JBTextField packageName;
    private final JBTextField focusExpression;
    private final JPanel panel;

    public GinkgoFocusScopeView() {
        packageName = new JBTextField();
        focusExpression = new JBTextField();
        panel = new FormBuilder()
                .setAlignLabelOnRight(false)
                .addLabeledComponent("Package", packageName)
                .addLabeledComponent("Focus expression", focusExpression)
                .getPanel();
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public void resetFrom(GinkgoRunConfigurationOptions settings) {
        packageName.setText(settings.getPackageName());
        focusExpression.setText(settings.getFocusTestExpression());
    }

    @Override
    public void applyTo(GinkgoRunConfigurationOptions settings) {
        settings.setPackageName(packageName.getText());
        settings.setFocusTestExpression(focusExpression.getText());
    }
}
