package com.github.idea.ginkgo.util;

import com.github.idea.ginkgo.GinkgoRunConfigurationOptions;
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
    private static final String TEST_NAMES = "test-names";
    private static final String TEST_NAME = "test-name";
    private static final String WORKING_DIR = "working-dir";
    private static final String GINKGO_EXECUTABLE = "ginkgo-executable";
    private static final String GINKGO_OPTIONS = "ginkgo-options";

    private GinkgoSerializationUtil() {
    }

    public static void writeXml(Element element, GinkgoRunConfigurationOptions runSettings) {
        runSettings.getEnvData().writeExternal(element);
        writeTestNames(element, runSettings.getTestNames());
        writePath(element, WORKING_DIR, runSettings.getWorkingDir());
        writePath(element, GINKGO_EXECUTABLE, runSettings.getGinkgoExecutable());
        writeNonEmptyField(element, GINKGO_OPTIONS, runSettings.getGinkgoOptions());
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

    private static void writeTestNames(@NotNull Element element, @NotNull List testNames) {
        if (!testNames.isEmpty()) {
            Element testNamesElement = new Element(TEST_NAMES);
            JDOMExternalizerUtil.addChildrenWithValueAttribute(testNamesElement, TEST_NAME, testNames);
            element.addContent(testNamesElement);
        }
    }

    public static GinkgoRunConfigurationOptions readXml(@NotNull Element element) {

        return new GinkgoRunConfigurationOptions.RunConfigBuilder()
                .setEnvData(EnvironmentVariablesData.readExternal(element))
                .setTestNames(readTestNames(element))
                .setWorkingDir(read(element, WORKING_DIR))
                .setGinkgoExecutable(read(element, GINKGO_EXECUTABLE))
                .setGinkgoOptions(read(element, GINKGO_OPTIONS))
                .build();
    }

    private static String read(Element element, String tagName) {
        String value = JDOMExternalizerUtil.readCustomField(element, tagName);
        return StringUtils.isEmpty(value) ? "" : value;
    }

    private static List<String> readTestNames(Element element) {
        Element testNamesElement = element.getChild(TEST_NAMES);
        if (testNamesElement != null) {
            List<String> testNames = JDOMExternalizerUtil.getChildrenValueAttributes(testNamesElement, TEST_NAME);
            return testNames;
        } else {
            return new ArrayList<>();
        }
    }
}


