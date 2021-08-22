package com.github.idea.ginkgo;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.util.ui.ComponentWithEmptyText;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.SwingHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GinkgoConfigurationEditor extends SettingsEditor<GinkgoRunConfiguration> {
    private final Project project;
    private TextFieldWithBrowseButton ginkgoExecutableField = createGinkgoExecutableField();
    private TextFieldWithBrowseButton workingDirectory = createWorkingDirectoryField();
    private RawCommandLineEditor ginkgoOptions = createGinkgoOptionsField();


    public GinkgoConfigurationEditor(Project project) {
        super();
        this.project = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull GinkgoRunConfiguration config) {
        GinkgoRunConfigurationOptions ginkgoRunConfigurationOptions = config.getOptions();
        ginkgoExecutableField.setText(ginkgoRunConfigurationOptions.getGinkgoExecutable());
        workingDirectory.setText(ginkgoRunConfigurationOptions.getWorkingDir());
        ginkgoOptions.setText(ginkgoRunConfigurationOptions.getGinkgoOptions());
    }

    @Override
    protected void applyEditorTo(@NotNull GinkgoRunConfiguration config) {
        GinkgoRunConfigurationOptions myOptions = config.getOptions();
        GinkgoRunConfigurationOptions ginkgoRunConfigurationOptions = new GinkgoRunConfigurationOptions.RunConfigBuilder()
                .setEnvData(myOptions.getEnvData())
                .setTestNames(myOptions.getTestNames())
                .setWorkingDir(workingDirectory.getText())
                .setGinkgoExecutable(ginkgoExecutableField.getText())
                .setGinkgoOptions(ginkgoOptions.getText())
                .build();
        config.setOptions(ginkgoRunConfigurationOptions);
    }

    @Override
    @NotNull
    protected JComponent createEditor() {
        return new FormBuilder()
                .setAlignLabelOnRight(false)
                .addLabeledComponent("Ginkgo executable", ginkgoExecutableField)
                .addLabeledComponent("Working directory", workingDirectory)
                .addLabeledComponent("Ginkgo options", ginkgoOptions)
                .getPanel();
    }

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
