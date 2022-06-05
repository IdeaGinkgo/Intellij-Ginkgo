package com.github.idea.ginkgo;


import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class GinkgoTestSetupTypeTest {

    @Test
    public void test_isGinkgoTestSetupType() {
        assertTrue(GinkgoTestSetupType.isGinkgoTestSetup("BeforeSuite"));
        assertTrue(GinkgoTestSetupType.isGinkgoTestSetup("AfterSuite"));
        assertTrue(GinkgoTestSetupType.isGinkgoTestSetup("SynchronizedBeforeSuite"));
        assertTrue(GinkgoTestSetupType.isGinkgoTestSetup("SynchronizedAfterSuite"));
        assertTrue(GinkgoTestSetupType.isGinkgoTestSetup("BeforeEach"));
        assertTrue(GinkgoTestSetupType.isGinkgoTestSetup("JustBeforeEach"));
        assertTrue(GinkgoTestSetupType.isGinkgoTestSetup("AfterEach"));
        assertTrue(GinkgoTestSetupType.isGinkgoTestSetup("JustAfterEach"));
        assertTrue(GinkgoTestSetupType.isGinkgoTestSetup("BeforeAll"));
        assertTrue(GinkgoTestSetupType.isGinkgoTestSetup("AfterAll"));
        assertTrue(GinkgoTestSetupType.isGinkgoTestSetup("DeferCleanup"));
        assertFalse(GinkgoTestSetupType.isGinkgoTestSetup("lizard"));
        assertFalse(GinkgoTestSetupType.isGinkgoTestSetup("1"));
    }
}