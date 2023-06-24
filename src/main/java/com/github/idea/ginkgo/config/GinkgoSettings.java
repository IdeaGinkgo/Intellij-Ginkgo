package com.github.idea.ginkgo.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(name = "Ginkgo", storages = {@Storage("plugin.ginkgo.xml")}, category = SettingsCategory.PLUGINS)
@Service
public final class GinkgoSettings implements PersistentStateComponent<GinkgoSettings> {
    private boolean ginkgoStructViewEnabled;
    private boolean useGoToolsGinkgoEnabled;
    public GinkgoSettings() {
        this.ginkgoStructViewEnabled = true;
        this.useGoToolsGinkgoEnabled = false;
    }

    public static GinkgoSettings getInstance() {
        return ApplicationManager.getApplication().getService(GinkgoSettings.class);
    }

    @Override
    public @NotNull GinkgoSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull GinkgoSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public boolean isGinkgoStructViewEnabled() {
        return ginkgoStructViewEnabled;
    }

    public void setGinkgoStructViewEnabled(boolean ginkgoStructViewEnabled) {
        this.ginkgoStructViewEnabled = ginkgoStructViewEnabled;
    }

    public boolean isUseGoToolsGinkgoEnabled() {
        return useGoToolsGinkgoEnabled;
    }

    public void setUseGoToolsGinkgoEnabled(boolean goToUseGoToolsGinkgoEnabled) {
        this.useGoToolsGinkgoEnabled = goToUseGoToolsGinkgoEnabled;
    }
}
