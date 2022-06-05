package com.github.idea.ginkgo.util;

import com.github.idea.ginkgo.GinkgoRunConfigurationOptions;
import com.github.idea.ginkgo.scope.GinkgoScope;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import org.jdom.Element;
import org.jdom.located.LocatedElement;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

public class GinkgoSerializationUtilTest {

    @Test
    public void test_serialized_deserialize_all_test_config() {
        GinkgoRunConfigurationOptions configOptions = new GinkgoRunConfigurationOptions();
        configOptions.setGinkgoExecutable("ginkgo");
        configOptions.setWorkingDir("/workspace");
        configOptions.setEnvData(EnvironmentVariablesData.DEFAULT);
        configOptions.setGinkgoAdditionalOptions("-race");
        configOptions.setGinkgoScope(GinkgoScope.ALL);
        configOptions.setFocusTestExpression("");
        configOptions.setTestNames(Arrays.asList("ginkgo all"));
        Element element = new LocatedElement("ginkgo_config");

        GinkgoSerializationUtil.writeXml(element, configOptions);
        GinkgoRunConfigurationOptions result = GinkgoSerializationUtil.readXml(element);

        assertEquals(configOptions, result);
    }

    @Test
    public void test_serialized_deserialize_focus_test_config() {
        GinkgoRunConfigurationOptions configOptions = new GinkgoRunConfigurationOptions();
        configOptions.setGinkgoExecutable("ginkgo");
        configOptions.setWorkingDir("/workspace");
        configOptions.setEnvData(EnvironmentVariablesData.DEFAULT);
        configOptions.setGinkgoAdditionalOptions("-race");
        configOptions.setGinkgoScope(GinkgoScope.FOCUS);
        configOptions.setFocusTestExpression("books");
        configOptions.setTestNames(Arrays.asList("books"));
        Element element = new LocatedElement("ginkgo_config");

        GinkgoSerializationUtil.writeXml(element, configOptions);
        GinkgoRunConfigurationOptions result = GinkgoSerializationUtil.readXml(element);

        assertEquals(configOptions, result);
    }
}