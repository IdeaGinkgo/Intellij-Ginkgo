package com.github.idea.ginkgo;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GinkgoTestSetupTypeTest {

    @Test
    void isGinkgoTestSetupType() {
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