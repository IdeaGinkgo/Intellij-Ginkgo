package com.github.idea.ginkgo.util;

import com.github.idea.ginkgo.GinkgoRunConfigurationOptions;
import com.github.idea.ginkgo.scope.GinkgoScope;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GinkgoSerializationUtil {
    public static final GinkgoSerializationUtil INSTANCE = new GinkgoSerializationUtil();
    private static final String WORKING_DIR = "working-dir";
    private static final String GINKGO_EXECUTABLE = "ginkgo-executable";
    private static final String GINKGO_ADDITIONAL_OPTIONS = "ginkgo-additional-options";
    private static final String GINKGO_SCOPE = "ginkgo-scope";
    private static final String FOCUS_EXPRESSION = "focus-expression";
    private static final String TEST_NAMES = "test-names";
    private static final String TEST_NAME = "test-name";

    private GinkgoSerializationUtil() {
    }

    public static void writeXml(Element element, GinkgoRunConfigurationOptions runSettings) {
        runSettings.getEnvData().writeExternal(element);
        writePath(element, GINKGO_EXECUTABLE, runSettings.getGinkgoExecutable());
        writePath(element, WORKING_DIR, runSettings.getWorkingDir());
        runSettings.getEnvData().writeExternal(element);
        writeNonEmptyField(element, GINKGO_ADDITIONAL_OPTIONS, runSettings.getGinkgoAdditionalOptions());
        write(element, GINKGO_SCOPE, runSettings.getGinkgoScope().getLabel());
        write(element, FOCUS_EXPRESSION, runSettings.getFocusTestExpression());
        writeTestNames(element, runSettings.getTestNames());
    }

    private static void write(Element element, String tagName, String value) {
        JDOMExternalizerUtil.writeCustomField(element, tagName, value);
    }

    private static void writePath(Element element, String tagName, String value) {
        write(element, tagName, FileUtil.toSystemIndependentName(value));
    }

    private static void writeNonEmptyField(@NotNull Element element, @NotNull String tagName, @Nullable String value) {
        if (StringUtil.isNotEmpty(value)) {
            write(element, tagName, value);
        }
    }

    private static void writeTestNames(@NotNull Element element, @NotNull List<String> testNames) {
        if (!testNames.isEmpty()) {
            Element testNamesElement = new Element(TEST_NAMES);
            JDOMExternalizerUtil.addChildrenWithValueAttribute(testNamesElement, TEST_NAME, testNames);
            element.addContent(testNamesElement);
        }
    }

    public static GinkgoRunConfigurationOptions readXml(@NotNull Element element) {
        GinkgoRunConfigurationOptions ginkgoRunConfigurationOptions = new GinkgoRunConfigurationOptions();
        ginkgoRunConfigurationOptions.setGinkgoExecutable(read(element, GINKGO_EXECUTABLE));
        ginkgoRunConfigurationOptions.setWorkingDir(read(element, WORKING_DIR));
        ginkgoRunConfigurationOptions.setEnvData(EnvironmentVariablesData.readExternal(element));
        ginkgoRunConfigurationOptions.setGinkgoAdditionalOptions(read(element, GINKGO_ADDITIONAL_OPTIONS));
        ginkgoRunConfigurationOptions.setGinkgoScope(readScope(element));
        ginkgoRunConfigurationOptions.setFocusTestExpression(read(element, FOCUS_EXPRESSION));
        ginkgoRunConfigurationOptions.setTestNames(readTestNames(element));

        return ginkgoRunConfigurationOptions;
    }

    private static String read(Element element, String tagName) {
        String value = JDOMExternalizerUtil.readCustomField(element, tagName);
        return StringUtils.isEmpty(value) ? "" : value;
    }

    private static GinkgoScope readScope(Element element) {
        try {
            return GinkgoScope.valueOf(read(element, GINKGO_SCOPE));
        } catch (Exception e) {
            return GinkgoScope.ALL;
        }
    }

    private static List<String> readTestNames(Element element) {
        Element testNamesElement = element.getChild(TEST_NAMES);
        if (testNamesElement != null) {
            return JDOMExternalizerUtil.getChildrenValueAttributes(testNamesElement, TEST_NAME);
        } else {
            return new ArrayList<>();
        }
    }
}


