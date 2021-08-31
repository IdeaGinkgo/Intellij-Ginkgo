package com.github.idea.ginkgo;

import com.github.idea.ginkgo.scope.GinkgoScope;
import com.github.idea.ginkgo.scope.GinkgoScopeView;
import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.util.ui.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class GinkgoConfigurationEditor extends SettingsEditor<GinkgoRunConfiguration> {
    private final Project project;
    private JPanel ginkgoForm;

    private TextFieldWithBrowseButton ginkgoExecutableField = createGinkgoExecutableField();
    private TextFieldWithBrowseButton workingDirectory = createWorkingDirectoryField();
    private EnvironmentVariablesTextFieldWithBrowseButton envVars = new EnvironmentVariablesTextFieldWithBrowseButton();
    private RawCommandLineEditor ginkgoAdditionalOptions = createGinkgoOptionsField();
    private JPanel scopeViewPanel = new JPanel(new BorderLayout());

    private ButtonGroup scopeButtonGroup = new ButtonGroup();
    private Map<GinkgoScope, GinkgoScopeView> scopeViews = new HashMap<>();

    //Magic number to align subform using the length of `Ginkgo additional options` label
    private int maxLabelWidth = 138;

    public GinkgoConfigurationEditor(Project project) {
        super();
        this.project = project;
        ginkgoForm = new FormBuilder()
                .setAlignLabelOnRight(false)
                .addLabeledComponent("Ginkgo executable", ginkgoExecutableField)
                .addLabeledComponent("Working directory", workingDirectory)
                .addLabeledComponent("Ginkgo additional options", ginkgoAdditionalOptions)
                .addLabeledComponent("Environmental variables", envVars)
                .addSeparator()
                .addComponent(createScopeRadioButtonPanel(scopeButtonGroup))
                .addComponent(scopeViewPanel)
                .getPanel();
    }

    /**
     * Resets the view able editor the the values from a GinkgoRunConfiguration. config -> editor.
     *
     * @param config
     */
    @Override
    protected void resetEditorFrom(@NotNull GinkgoRunConfiguration config) {
        GinkgoRunConfigurationOptions ginkgoRunConfigurationOptions = config.getOptions();

        ginkgoExecutableField.setText(ginkgoRunConfigurationOptions.getGinkgoExecutable());
        workingDirectory.setText(ginkgoRunConfigurationOptions.getWorkingDir());
        envVars.setData(ginkgoRunConfigurationOptions.getEnvData());
        ginkgoAdditionalOptions.setText(ginkgoRunConfigurationOptions.getGinkgoAdditionalOptions());
        GinkgoScope ginkgoScope = ginkgoRunConfigurationOptions.getGinkgoScope();
        setSelectedScope(ginkgoScope);
        getScopeView(ginkgoScope).resetFrom(ginkgoRunConfigurationOptions);
    }

    /**
     * Copy the values from the editor to the configuration object. editor -> config.
     *
     * @param config
     */
    @Override
    protected void applyEditorTo(@NotNull GinkgoRunConfiguration config) {
        GinkgoRunConfigurationOptions myOptions = config.getOptions();
        GinkgoScope selectedScope = getSelectedScope(scopeButtonGroup);

        GinkgoRunConfigurationOptions ginkgoRunConfigurationOptions = new GinkgoRunConfigurationOptions(project);
        ginkgoRunConfigurationOptions.setGinkgoExecutable(ginkgoExecutableField.getText());
        ginkgoRunConfigurationOptions.setWorkingDir(workingDirectory.getText());
        ginkgoRunConfigurationOptions.setEnvData(envVars.getData());
        ginkgoRunConfigurationOptions.setGinkgoAdditionalOptions(ginkgoAdditionalOptions.getText());
        ginkgoRunConfigurationOptions.setGinkgoScope(selectedScope);
        ginkgoRunConfigurationOptions.setTestNames(myOptions.getTestNames());

        GinkgoScope ginkgoScope = selectedScope;
        getScopeView(ginkgoScope).applyTo(ginkgoRunConfigurationOptions);

        config.setOptions(ginkgoRunConfigurationOptions);
    }


    @Override
    @NotNull
    protected JComponent createEditor() {
        return ginkgoForm;
    }

    /**
     * Sets the appropriate radio button as selected and renders the correct subform.
     *
     * @param ginkgoScope
     */
    private void setSelectedScope(GinkgoScope ginkgoScope) {
        Enumeration<AbstractButton> scopes = scopeButtonGroup.getElements();
        while (scopes.hasMoreElements()) {
            AbstractButton rb = scopes.nextElement();
            if (rb.getText() == ginkgoScope.getLabel()) {
                rb.setSelected(true);
                setTestScope(ginkgoScope);
            }
        }
    }


    /**
     * @param buttonGroup
     * @return the radio button scope selection element
     */
    private JPanel createScopeRadioButtonPanel(ButtonGroup buttonGroup) {
        JPanel testScopePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, JBUI.scale(UIUtil.DEFAULT_HGAP), 0));

        for (GinkgoScope scope : GinkgoScope.values()) {
            JRadioButton radioButton = new JRadioButton(scope.getLabel());
            radioButton.addActionListener(e -> setTestScope(scope));

            buttonGroup.add(radioButton);
            testScopePanel.add(radioButton);
        }

        return testScopePanel;
    }

    /**
     * Gets the selected scope radio button.
     *
     * @param scopeButtonGroup
     * @return the scope value
     */
    private GinkgoScope getSelectedScope(ButtonGroup scopeButtonGroup) {
        Enumeration<AbstractButton> scopes = scopeButtonGroup.getElements();
        while (scopes.hasMoreElements()) {
            AbstractButton rb = scopes.nextElement();
            if (rb.isSelected()) {
                return GinkgoScope.valueOfLabel(rb.getText());
            }
        }
        return GinkgoScope.All;
    }


    /**
     * Draws the appropriate GinkgoScopeView subform.
     *
     * @param scope
     */
    private void setTestScope(GinkgoScope scope) {
        GinkgoScopeView scopeView = getScopeView(scope);
        scopeViewPanel.removeAll();
        scopeViewPanel.add(scopeView.getComponent(), BorderLayout.CENTER);
        scopeViewPanel.revalidate();
        scopeViewPanel.repaint();
    }

    /**
     * Gets the GinkgoScopeView either from the scopeViews map if it exists or creates a new one.
     *
     * @param scope
     * @return GinkgoScopeView
     */
    private GinkgoScopeView getScopeView(GinkgoScope scope) {
        GinkgoScopeView scopeView = scopeViews.get(scope);
        if (scopeView == null) {
            scopeView = scope.createView(project);
            scopeViews.put(scope, scopeView);
        }

        scopeView.getComponent().add(
                Box.createHorizontalStrut(maxLabelWidth),
                new GridBagConstraints(
                        0, GridBagConstraints.RELATIVE,
                        1, 1,
                        0, 0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.NONE,
                        JBUI.insetsRight(UIUtil.DEFAULT_HGAP),
                        0, 0)
        );

        return scopeView;
    }

    /**
     * @return dialog for ginkgo executable selection.
     */
    private TextFieldWithBrowseButton createGinkgoExecutableField() {
        TextFieldWithBrowseButton fullField = new TextFieldWithBrowseButton();
        SwingHelper.installFileCompletionAndBrowseDialog(
                project,
                fullField,
                "Select override Ginkgo executable file",
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
        );
        return fullField;
    }

    /**
     * @return dialog for suite tests path.
     */
    private TextFieldWithBrowseButton createWorkingDirectoryField() {
        TextFieldWithBrowseButton fullField = new TextFieldWithBrowseButton();
        SwingHelper.installFileCompletionAndBrowseDialog(
                project,
                fullField,
                "Select working directory",
                FileChooserDescriptorFactory.createSingleFolderDescriptor()
        );
        return fullField;
    }

    /**
     * @return dialog for additional ginkgo options.
     */
    private RawCommandLineEditor createGinkgoOptionsField() {
        RawCommandLineEditor editor = new RawCommandLineEditor();
        JTextField field = editor.getTextField();
        if (field instanceof ExpandableTextField) {
            field.putClientProperty("monospaced", false);
        }

        if (field instanceof ComponentWithEmptyText) {
            ((ComponentWithEmptyText) field).getEmptyText().setText("CLI options, e.g. --fail-fast=true");
        }

        return editor;
    }
}
