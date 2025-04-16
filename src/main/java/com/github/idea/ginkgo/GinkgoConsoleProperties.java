package com.github.idea.ginkgo;

import com.goide.vgo.project.VgoDependency;
import com.goide.vgo.project.VgoModulesRegistry;
import com.github.idea.ginkgo.actions.RerunFailedTestAction;
import com.intellij.execution.Executor;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class GinkgoConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {
    public static final Logger LOG = Logger.getInstance(GinkgoConsoleProperties.class);

    public GinkgoConsoleProperties(@NotNull GinkgoRunConfiguration configuration, @NotNull String testFrameworkName, @NotNull Executor executor) {
        super(configuration, testFrameworkName, executor);
        this.setPrintTestingStartedTime(false);
    }

    @NotNull
    public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
        if (ginkgoAtOrAboveVersion(GinkgoVersion.of("2.5.0"))) {
            return new GinkgoTestEventsConverterV2(testFrameworkName, consoleProperties);
        }

        return new GinkgoTestEventsConverter(testFrameworkName, consoleProperties);
    }

    @Nullable
    @Override
    public AbstractRerunFailedTestsAction createRerunFailedTestsAction(ConsoleView consoleView) {
        if (!(consoleView instanceof SMTRunnerConsoleView)) {
            return null;
        }

        return new RerunFailedTestAction((SMTRunnerConsoleView) consoleView, this);
    }

    private boolean ginkgoAtOrAboveVersion(GinkgoVersion version) {
        List<VgoDependency> dependencies = VgoModulesRegistry.getInstance(getProject())
        .getModules()
        .stream().flatMap(vgoModule -> vgoModule.getDependencies().stream())
        .collect(Collectors.toList());

        GinkgoVersion importedVersion = dependencies.stream()
                .filter(vgoDependency -> vgoDependency.getImportPath().startsWith("github.com/onsi/ginkgo"))
                .findFirst()
                .map(VgoDependency::getVersion)
                .map(GinkgoVersion::of)
                .orElseGet(() -> {
                    LOG.warn("No github.com/onsi/ginkgo version found in go.mod defaulting to 0.0.0");
                    return GinkgoVersion.DEFAULT_VERSION;
                });

        LOG.info("Ginkgo version: " + importedVersion.toString());
        return importedVersion.greaterThan(version) || importedVersion.equals(version);
    }
}
