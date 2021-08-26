package com.github.idea.ginkgo;

import com.goide.execution.GoBuildingRunner;
import com.goide.execution.testing.frameworks.gotest.GotestEventsConverter;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GinkgoTestEventsConverter extends GotestEventsConverter {
    private static final Pattern SUITE_START = Pattern.compile("Running Suite: (.*)");
    private static final Pattern SUCCESS = Pattern.compile("^(SUCCESS!)$");
    private static final Pattern FAIL = Pattern.compile("^(FAIL!)$");
    private Stack<String> suites = new Stack();

    public GinkgoTestEventsConverter(@NotNull String defaultImportPath, @NotNull TestConsoleProperties consoleProperties) {
        super(defaultImportPath, consoleProperties);
    }

    @Override
    protected int processLine(@NotNull String line, int start, @NotNull Key<?> outputType, @NotNull ServiceMessageVisitor visitor) throws ParseException {
        Matcher matcher;
        if ((matcher = SUITE_START.matcher(line)).find(start)) {
            String suiteName = matcher.group(1);
            suites.push(suiteName);
            startTest(suiteName, outputType, visitor);
            processOutput(line, outputType, visitor);
            return line.length();
        }

        if (SUCCESS.matcher(line).find(start)) {
            processOutput(line, outputType, visitor);
            finishTest(suites.pop(), TestResult.PASSED, visitor);
            return line.length();
        }

        if (FAIL.matcher(line).find(start)) {
            processOutput(line, outputType, visitor);
            finishTest(suites.pop(), TestResult.FAILED, visitor);
            return line.length();
        }

        return super.processLine(line, start, outputType, visitor);
    }
}
