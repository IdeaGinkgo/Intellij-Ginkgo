package com.github.idea.ginkgo;

import com.goide.execution.testing.frameworks.gotest.GotestEventsConverter;
import com.intellij.execution.Executor;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import org.jetbrains.annotations.NotNull;

public class GinkgoConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {

    GinkgoConsoleProperties(@NotNull GinkgoRunConfiguration configuration, @NotNull String testFrameworkName, @NotNull Executor executor) {
        super(configuration, testFrameworkName, executor);
        this.setPrintTestingStartedTime(false);
    }

    @NotNull
    public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
        return new GotestEventsConverter(testFrameworkName, consoleProperties);
    }
}
