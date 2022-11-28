package com.github.idea.ginkgo.config;

import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;


public class GinkgoSettingsUI implements ConfigurableUi<GinkgoSettings> {
    private final JPanel ginkgoConfigPanel = new JPanel(new BorderLayout());
    private final JBCheckBox enableStructViewCheckBox = new JBCheckBox("Enable Struct View");

    public GinkgoSettingsUI() {
        FormBuilder builder = FormBuilder.createFormBuilder().addComponent(enableStructViewCheckBox);
        ginkgoConfigPanel.add(builder.getPanel(), "North");
    }

    @Override
    public void reset(@NotNull GinkgoSettings settings) {
        enableStructViewCheckBox.setSelected(settings.isGinkgoStructViewEnabled());
    }

    @Override
    public boolean isModified(@NotNull GinkgoSettings settings) {
        return this.enableStructViewCheckBox.isSelected() != settings.isGinkgoStructViewEnabled();
    }

    @Override
    public void apply(@NotNull GinkgoSettings settings) {
        settings.setGinkgoStructViewEnabled(this.enableStructViewCheckBox.isSelected());
    }

    @Override
    public @NotNull JComponent getComponent() {
        return ginkgoConfigPanel;
    }
}
