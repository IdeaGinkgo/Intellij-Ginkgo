package com.github.idea.ginkgo;

import org.junit.Test;

import static org.junit.Assert.*;

public class GinkgoVersionTest {

    @Test
    public void greaterThan() {
        assertTrue(GinkgoVersion.of("v1.0.0").greaterThan(GinkgoVersion.of("v0.9.9")));
        assertTrue(GinkgoVersion.of("v1.1.0").greaterThan(GinkgoVersion.of("v1.0.9")));
        assertTrue(GinkgoVersion.of("v1.1.1").greaterThan(GinkgoVersion.of("v1.1.0")));
        assertFalse(GinkgoVersion.of("v0.9.9").greaterThan(GinkgoVersion.of("v1.0.0")));
        assertFalse(GinkgoVersion.of("v1.0.9").greaterThan(GinkgoVersion.of("v1.1.0")));
        assertFalse(GinkgoVersion.of("v1.1.0").greaterThan(GinkgoVersion.of("v1.1.1")));
    }
}