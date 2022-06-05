package com.github.idea.ginkgo;


import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class GinkgoSpecTypeTest {

    @Test
    public void test_isGinkgoFunction() {
        assertTrue(GinkgoSpecType.isGinkgoSpec("Describe"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("DescribeTable"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("Context"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("When"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("It"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("Entry"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("Specify"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("FDescribe"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("FDescribeTable"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("FContext"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("FWhen"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("FIt"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("FEntry"));
        assertTrue(GinkgoSpecType.isGinkgoSpec("FSpecify"));
        assertFalse(GinkgoSpecType.isGinkgoSpec("1"));
        assertFalse(GinkgoSpecType.isGinkgoSpec("Lizard"));
    }
}