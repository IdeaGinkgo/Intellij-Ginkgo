package com.github.idea.ginkgo;

import com.goide.execution.testing.frameworks.gotest.GotestEventsConverter;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.openapi.util.Key;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GinkgoTestEventsConverter extends GotestEventsConverter {
    private static final Pattern SUITE_START = Pattern.compile("Running Suite: (.*)");
    private static final Pattern SUCCESS = Pattern.compile("^(SUCCESS!)");
    private static final Pattern FAIL = Pattern.compile("^(FAIL!)");
    private static final Pattern START_SUITE_BLOCK = Pattern.compile("Will run [0-9]* of [0-9]* specs");
    private static final Pattern END_SUITE_BLOCK = Pattern.compile("Ran [0-9]* of [0-9]* Specs in [0-9]*\\.?[0-9]* seconds");
    private static final String SPEC_SEPARATOR = "------------------------------";
    private Stack<String> suites = new Stack();
    private boolean inSuiteBlock;
    private String specContext;
    private String specName;
    private String tempLine;
    private StringBuffer line = new StringBuffer();

    public GinkgoTestEventsConverter(@NotNull String defaultImportPath, @NotNull TestConsoleProperties consoleProperties) {
        super(defaultImportPath, consoleProperties);
    }

    @Override
    public void process(String text, Key outputType) {
        // ignore skipped test indicators
        if (text.equalsIgnoreCase("S")) {
            return;
        }

        // immediately flush completed test indicator
        if (text.equalsIgnoreCase("+")) {
            super.process(text, outputType);
            return;
        }

        if (text.endsWith("\n")) {
            super.process(line.append(text).toString(), outputType);
            line.delete(0, line.length());
            return;
        }

        line.append(text);
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

        if (START_SUITE_BLOCK.matcher(line).find(start)) {
            processOutput(line, outputType, visitor);
            inSuiteBlock=true;
            return line.length();
        }

        if (END_SUITE_BLOCK.matcher(line).find(start)) {
            processOutput(line, outputType, visitor);
            inSuiteBlock=false;
            specContext = null;
            specName = null;
            tempLine = null;
            return line.length();
        }

        if (inSuiteBlock && StringUtils.isNotBlank(line)) {
            if (line.startsWith(SPEC_SEPARATOR)) {
                processOutput(line, outputType, visitor);
                specContext = null;
                specName = null;
                tempLine = null;
                return line.length();
            }

            if (specContext == null) {
                specContext = line.trim();
                tempLine = line;
                return line.length();
            }

            if (specName == null) {
                specName = line.trim();
                startTest(specContext+"/"+specName, outputType, visitor);
                processOutput(tempLine, outputType, visitor);
                processOutput(line, outputType, visitor);
                return line.length();
            }

            if (line.startsWith("+ Failure")) {
                processOutput(line, outputType, visitor);
                finishTest(specContext+"/"+specName, TestResult.FAILED, visitor);
                return line.length();
            }

            if (line.startsWith("+")) {
                processOutput(line, outputType, visitor);
                finishTest(specContext+"/"+specName, TestResult.PASSED, visitor);
                return line.length();
            }
        }

        processOutput(line, outputType, visitor);
        return line.length();
    }

    @Override
    protected void processOutput(@NotNull String text, Key<?> outputType, ServiceMessageVisitor visitor) throws ParseException {
        super.processOutput(text, outputType, visitor);
    }
}
