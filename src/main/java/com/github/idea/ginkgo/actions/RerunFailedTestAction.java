package com.github.idea.ginkgo.actions;

import com.github.idea.ginkgo.GinkgoConfigurationType;
import com.github.idea.ginkgo.GinkgoConsoleProperties;
import com.github.idea.ginkgo.GinkgoRunConfiguration;
import com.github.idea.ginkgo.GinkgoRunConfigurationOptions;
import com.github.idea.ginkgo.scope.GinkgoScope;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.idea.ginkgo.util.GinkgoUtil.escapeRegexCharacters;

public class RerunFailedTestAction extends AbstractRerunFailedTestsAction {
    public RerunFailedTestAction(@NotNull SMTRunnerConsoleView consoleView, GinkgoConsoleProperties ginkgoConsoleProperties) {
        super(consoleView);
        this.init(ginkgoConsoleProperties);
        setModelProvider(() -> consoleView.getResultsViewer().getFailedTestCount() > 0 ? consoleView.getResultsViewer() : null);
    }

    @Override
    protected @Nullable MyRunProfile getRunProfile(@NotNull ExecutionEnvironment environment) {
        Project project = myConsoleProperties.getProject();

        List<AbstractTestProxy> failedTests = getFailedTests(project);
        String focusExpression = failedTests.stream()
                .map(RerunFailedTestAction::focusExpression)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("|"));

        GinkgoRunConfiguration runConfiguration = (GinkgoRunConfiguration) GinkgoConfigurationType
                .getInstance()
                .createTemplateConfiguration(project);
        GinkgoRunConfigurationOptions options = runConfiguration.getOptions();
        options.setGinkgoScope(GinkgoScope.FOCUS);
        options.setFocusTestExpression(focusExpression);
        options.setRerun(true);
        return new MyRunProfile(runConfiguration) {
            @Override
            public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
                return runConfiguration.getState(executor, environment);
            }
        };
    }

    private static String focusExpression(AbstractTestProxy abstractTestProxy) {
        String name = abstractTestProxy.getName();
        if (name.startsWith("[root]")) {
            return null;
        }

        if (name.startsWith("Summarizing")) {
            return null;
        }

        if (name.contains(" - ")) {
            return null;
        }

        return escapeRegexCharacters(name.replace("/", " ") + "$");
    }
}
