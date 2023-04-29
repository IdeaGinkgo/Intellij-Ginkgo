package com.github.idea.ginkgo;

import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.openapi.util.Key;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor;
import jetbrains.buildServer.messages.serviceMessages.TestStdErr;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.regex.Matcher;

public class GinkgoTestEventsConverterV2 extends GinkgoTestEventsConverter {
    public GinkgoTestEventsConverterV2(@NotNull String defaultImportPath, @NotNull TestConsoleProperties consoleProperties) {
        super(defaultImportPath, consoleProperties);
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
            return line.length();
        }

        if(START_PENDING_BLOCK.matcher(line).find(start)) {
            inPendingBlock=true;
            return line.length();
        }

        if(inPendingBlock && StringUtils.isNotBlank(line)) {
            return processPendingSpecBlock(line, outputType, visitor);
        }

        if(START_SKIP_BLOCK.matcher(line).find(start)) {
            inSkipBlock=true;
            return line.length();
        }

        if(inSkipBlock && StringUtils.isNotBlank(line)) {
            return processSkipSpecBlock(line);
        }

        if (inSuiteBlock && StringUtils.isNotBlank(line)) {
            if (line.startsWith(SPEC_SEPARATOR) && specCompleted) {
                processOutput(line, outputType, visitor);
                specContext = null;
                specCompleted = false;
                return line.length();
            }

            if (line.startsWith(SPEC_SEPARATOR)) {
                return line.length();
            }

            if (specContext == null) {
                specContext = line.trim();
                startTest(specContext, outputType, visitor);
                processOutput(line, outputType, visitor);
                return line.length();
            }

            if (isFailure(line) || isPanic(line)) {
                processOutput(line, outputType, visitor);
                finishTest(specContext, TestResult.FAILED, visitor);
                specCompleted = true;
                return line.length();
            }

            if (isSuccess(line)) {
                processOutput(line, outputType, visitor);
                finishTest(specContext, TestResult.PASSED, visitor);
                specCompleted = true;
                return line.length();
            }
        }

        processOutput(line, outputType, visitor);
        return line.length();
    }
}
