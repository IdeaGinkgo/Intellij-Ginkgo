package com.github.idea.ginkgo.config;

import com.github.idea.ginkgo.GinkgoSettings;
import com.intellij.openapi.options.ConfigurableBase;
import org.jetbrains.annotations.NotNull;

public class GinkgoSettingsConfigurable extends ConfigurableBase<GinkgoSettingsUI, GinkgoSettings> {

    public GinkgoSettingsConfigurable() {
        super("ginkgo", "Ginkgo", "Ginkgo Configuration");
    }

    @Override
    protected @NotNull GinkgoSettings getSettings() {
        return GinkgoSettings.getInstance();
    }

    @Override
    protected GinkgoSettingsUI createUi() {
        return new GinkgoSettingsUI();
    }
}
