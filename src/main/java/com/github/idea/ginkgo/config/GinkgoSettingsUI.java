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
    private final JBCheckBox useGoToolsGinkgo = new JBCheckBox("Use Go Tools Ginkgo Version (Requires Ginkgo V2)");

    public GinkgoSettingsUI() {
        FormBuilder builder = FormBuilder.createFormBuilder()
                .addComponent(enableStructViewCheckBox)
                .addComponent(useGoToolsGinkgo);
        ginkgoConfigPanel.add(builder.getPanel(), "North");
    }

    @Override
    public void reset(@NotNull GinkgoSettings settings) {
        enableStructViewCheckBox.setSelected(settings.isGinkgoStructViewEnabled());
        useGoToolsGinkgo.setSelected(settings.isUseGoToolsGinkgoEnabled());
    }

    @Override
    public boolean isModified(@NotNull GinkgoSettings settings) {
        return this.enableStructViewCheckBox.isSelected() != settings.isGinkgoStructViewEnabled()
                || this.useGoToolsGinkgo.isSelected() != settings.isUseGoToolsGinkgoEnabled();
    }

    @Override
    public void apply(@NotNull GinkgoSettings settings) {
        settings.setGinkgoStructViewEnabled(this.enableStructViewCheckBox.isSelected());
        settings.setUseGoToolsGinkgoEnabled(this.useGoToolsGinkgo.isSelected());
    }

    @Override
    public @NotNull JComponent getComponent() {
        return ginkgoConfigPanel;
    }
}
