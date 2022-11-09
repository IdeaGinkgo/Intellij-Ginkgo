package com.github.idea.ginkgo;

import com.goide.execution.testing.frameworks.gotest.GotestEventsConverter;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.openapi.util.Key;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import jetbrains.buildServer.messages.serviceMessages.TestStdErr;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GinkgoTestEventsConverter extends GotestEventsConverter {
    private static final Pattern SUITE_START = Pattern.compile("Running Suite: (.*)");
    private static final Pattern SUCCESS = Pattern.compile("^(SUCCESS!)");
    private static final Pattern FAIL = Pattern.compile("^(FAIL!)");
    private static final Pattern START_SUITE_BLOCK = Pattern.compile("Will run [0-9]* of [0-9]* specs");
    private static final Pattern END_SUITE_BLOCK = Pattern.compile("Ran [0-9]* of [0-9]* Specs in [0-9]*\\.?[0-9]* seconds");
    private static final Pattern START_PENDING_BLOCK = Pattern.compile("P \\[PENDING\\]");
    private static final Pattern START_BEFORE_SUITE_BLOCK = Pattern.compile("\\[BeforeSuite\\]");
    private static final Pattern FILE_LOCATION_OUTPUT = Pattern.compile(".*_test.go:[0-9]*");
    private static final Pattern LOG_OUTPUT = Pattern.compile("\\d{4}[\\/-]\\d{2}[\\/-]\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}(.*)");
    private static final String SPEC_SEPARATOR = "------------------------------";
    public static final String SUCCESS_PREFIX_1 = "+";
    public static final String SUCCESS_PREFIX_2 = "•";
    public static final String FAILURE_PREFIX_1 = "+ Failure";
    public static final String FAILURE_PREFIX_2 = "• Failure";
    public static final String FAILURE_PREFIX_3 = "+ [FAILED]";
    public static final String FAILURE_PREFIX_4 = "• [FAILED]";

    public static final String PANIC_PREFIX_1 = "+! Panic";
    public static final String PANIC_PREFIX_2 = "•! Panic";
    public static final String PANIC_PREFIX_3 = "+! [PANICKED]";
    public static final String PANIC_PREFIX_4 = "•! [PANICKED]";
    private Stack<String> suites = new Stack<>();
    private boolean inSuiteBlock;
    private String specContext;
    private String specName;
    private String tempLine;
    private StringBuilder line = new StringBuilder();
    private StringBuilder pendingTestOutputBuffer = new StringBuilder();
    private List<String> pendingSpecNames = new ArrayList<>();
    private boolean inPendingBlock;
    private boolean inBeforeSuite;
    private boolean specCompleted = true;
    private boolean ginkgoCLIException;

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
        if (text.equalsIgnoreCase(SUCCESS_PREFIX_1) || text.equalsIgnoreCase(SUCCESS_PREFIX_2)) {
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
        if (line.startsWith("flag provided but not defined:") || line.startsWith("=== RUN")) {
            startTest("Ginkgo CLI Incompatible", outputType, visitor);
            visitor.visitTestStdErr(new TestStdErr("Ginkgo CLI Incompatible",
                    "An error occurred with ginkgo CLI this usually is a V1/V2 compatibility issue. \n" +
                                "Please make sure the ginkgo CLI version matches the version used by your project. \n" +
                                "You can install the appropriate CLI by running 'go install github.com/onsi/ginkgo/ginkgo@v1' or " +
                                "'go install github.com/onsi/ginkgo/v2/ginkgo@v2' \n"));
            finishTest("Ginkgo CLI Incompatible", TestResult.FAILED, visitor);
            ginkgoCLIException = true;
            return line.length();
        }

        if (ginkgoCLIException) {
            return line.length();
        }

        if (LOG_OUTPUT.matcher(line).find()) {
            processOutput(line, outputType, visitor);
            return line.length();
        }

        if ((matcher = SUITE_START.matcher(line)).find(start)) {
            String suiteName = cleanSuiteName(matcher.group(1));
            suites.push(suiteName);
            startTest(suiteName, outputType, visitor);
            processOutput(line, outputType, visitor);
            return line.length();
        }

        if(START_BEFORE_SUITE_BLOCK.matcher(line).find(start)) {
            inBeforeSuite=true;
            return line.length();
        }

        if(inBeforeSuite && StringUtils.isNotBlank(line)) {
            return processBeforeSuiteBlock(line, outputType, visitor);
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

        if(START_PENDING_BLOCK.matcher(line).find(start)) {
            inPendingBlock=true;
            return line.length();
        }

        if(inPendingBlock && StringUtils.isNotBlank(line)) {
            return processPendingSpecBlock(line, outputType, visitor);
        }

        if (inSuiteBlock && StringUtils.isNotBlank(line)) {
            if (line.startsWith(SPEC_SEPARATOR) && specCompleted) {
                processOutput(line, outputType, visitor);
                specContext = null;
                specCompleted = false;
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

            if (isFailure(line) || isPanic(line)) {
                processOutput(line, outputType, visitor);
                finishTest(specContext+"/"+specName, TestResult.FAILED, visitor);
                specCompleted = true;
                return line.length();
            }

            if (line.startsWith(SUCCESS_PREFIX_1) || line.startsWith(SUCCESS_PREFIX_2)) {
                processOutput(line, outputType, visitor);
                finishTest(specContext+"/"+specName, TestResult.PASSED, visitor);
                specCompleted = true;
                return line.length();
            }
        }

        processOutput(line, outputType, visitor);
        return line.length();
    }

    private static boolean isFailure(@NotNull String line) {
        return line.startsWith(FAILURE_PREFIX_1) || line.startsWith(FAILURE_PREFIX_2)
                || line.startsWith(FAILURE_PREFIX_3) || line.startsWith(FAILURE_PREFIX_4);
    }

    private static boolean isPanic(@NotNull String line) {
        return line.startsWith(PANIC_PREFIX_1) || line.startsWith(PANIC_PREFIX_2)
                || line.startsWith(PANIC_PREFIX_3) || line.startsWith(PANIC_PREFIX_4);
    }

    private int processBeforeSuiteBlock(@NotNull String line, @NotNull Key<?> outputType, @NotNull ServiceMessageVisitor visitor) {
        if (line.startsWith(SPEC_SEPARATOR)) {
            inBeforeSuite = false;
            return line.length();
        }

        return line.length();
    }

    private String cleanSuiteName(String group) {
        int locationDataStart = group.lastIndexOf("-");
        if (locationDataStart == -1) {
            return group;
        }

        return group.substring(0, locationDataStart).trim();
    }


    /**
     * Processes pending spec output. Buffers the output so it can be grouped under the appropriate test name while
     * building the spec name until a line separator is reached.
     *
     * @param line
     * @param outputType
     * @param visitor
     * @return
     * @throws ParseException
     */
    private int processPendingSpecBlock(@NotNull String line, @NotNull Key<?> outputType, @NotNull ServiceMessageVisitor visitor) throws ParseException {
        if (line.startsWith(SPEC_SEPARATOR)) {
            String pendingSpecName = String.join(" ", pendingSpecNames);

            startTest(pendingSpecName, outputType, visitor);
            processOutput(pendingTestOutputBuffer.toString(), outputType, visitor);
            finishTest(pendingSpecName, TestResult.SKIPPED, visitor);

            //Complete pending suite block and reset state.
            inPendingBlock = false;
            pendingTestOutputBuffer.delete(0, pendingTestOutputBuffer.length());
            pendingSpecNames.clear();
            return line.length();
        }

        addPendingSpecName(line);
        pendingTestOutputBuffer.append(line);
        return line.length();
    }


    /**
     * Add only spec names to pendingSpecList ignoring file location hint output.
     *
     * @param line
     */
    private void addPendingSpecName(String line) {
        if (!FILE_LOCATION_OUTPUT.matcher(line).find()) {
            pendingSpecNames.add(line.trim());
        }
    }
}
