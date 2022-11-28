package com.github.idea.ginkgo;

import com.goide.vgo.project.VgoDependency;
import com.goide.vgo.project.VgoModulesRegistry;
import com.intellij.execution.Executor;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class GinkgoConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {

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
                .orElse(GinkgoVersion.DEFAULT_VERSION);

        return importedVersion.greaterThan(version) || importedVersion.equals(version);
    }
}
