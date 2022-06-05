package com.github.idea.ginkgo;

import java.util.EnumSet;

public enum GinkgoTestSetupType {
    BEFORE_SUITE("BeforeSuite"),
    AFTER_SUITE("AfterSuite"),
    SYNCHRONIZED_BEFORE_SUITE("SynchronizedBeforeSuite"),
    SYNCHRONIZED_AFTER_SUITE("SynchronizedAfterSuite"),
    BEFORE_EACH("BeforeEach"),
    JUST_BEFORE_EACH("JustBeforeEach"),
    AFTER_EACH("AfterEach"),
    JUST_AFTER_EACH("JustAfterEach"),
    BEFORE_ALL("BeforeAll"),
    AFTER_ALL("AfterAll"),
    DEFER_CLEANUP("DeferCleanup");

    private final String testSetupType;

    GinkgoTestSetupType(String testSetupType) {
        this.testSetupType = testSetupType;
    }

    public String testSetupType() {
        return testSetupType;
    }

    public static boolean isGinkgoTestSetup(String spec) {
        return EnumSet.allOf(GinkgoTestSetupType.class).stream().anyMatch(e -> e.testSetupType.equals(spec));
    }
}
