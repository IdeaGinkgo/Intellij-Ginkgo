package com.github.idea.ginkgo;

import org.junit.Test;

import static org.junit.Assert.*;

public class GinkgoSpecTest {
    @Test
    public void test_isGinkgoActiveFunction() {
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("Describe"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("DescribeTable"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("Context"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("When"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("It"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("Entry"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("Specify"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("FDescribe"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("FDescribeTable"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("FContext"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("FWhen"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("FIt"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("FEntry"));
        assertTrue(GinkgoSpec.isGinkgoActiveSpec("FSpecify"));
        assertFalse(GinkgoSpec.isGinkgoActiveSpec(null));
    }

    @Test
    public void test_isGinkgoPendingFunction() {
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("PDescribe"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("PDescribeTable"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("PContext"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("PWhen"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("PIt"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("PEntry"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("PSpecify"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("XDescribe"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("XDescribeTable"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("XContext"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("XWhen"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("XIt"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("XEntry"));
        assertTrue(GinkgoSpec.isGinkgoPendingSpec("XSpecify"));
        assertFalse(GinkgoSpec.isGinkgoPendingSpec(null));
    }

    @Test
    public void test_getSpec() {
        assertEquals(GinkgoSpec.DESCRIBE, GinkgoSpec.getSpec("Describe"));
        assertEquals(GinkgoSpec.DESCRIBE_TABLE, GinkgoSpec.getSpec("DescribeTable"));
        assertEquals(GinkgoSpec.CONTEXT, GinkgoSpec.getSpec("Context"));
        assertEquals(GinkgoSpec.WHEN, GinkgoSpec.getSpec("When"));
        assertEquals(GinkgoSpec.IT, GinkgoSpec.getSpec("It"));
        assertEquals(GinkgoSpec.ENTRY, GinkgoSpec.getSpec("Entry"));
        assertEquals(GinkgoSpec.SPECIFY, GinkgoSpec.getSpec("Specify"));
        assertEquals(GinkgoSpec.PDESCRIBE, GinkgoSpec.getSpec("PDescribe"));
        assertEquals(GinkgoSpec.PDESCRIBE_TABLE, GinkgoSpec.getSpec("PDescribeTable"));
        assertEquals(GinkgoSpec.PCONTEXT, GinkgoSpec.getSpec("PContext"));
        assertEquals(GinkgoSpec.PWHEN, GinkgoSpec.getSpec("PWhen"));
        assertEquals(GinkgoSpec.PIT, GinkgoSpec.getSpec("PIt"));
        assertEquals(GinkgoSpec.PENTRY, GinkgoSpec.getSpec("PEntry"));
        assertEquals(GinkgoSpec.PSPECIFY, GinkgoSpec.getSpec("PSpecify"));
        assertEquals(GinkgoSpec.XDESCRIBE, GinkgoSpec.getSpec("XDescribe"));
        assertEquals(GinkgoSpec.XDESCRIBE_TABLE, GinkgoSpec.getSpec("XDescribeTable"));
        assertEquals(GinkgoSpec.XCONTEXT, GinkgoSpec.getSpec("XContext"));
        assertEquals(GinkgoSpec.XWHEN, GinkgoSpec.getSpec("XWhen"));
        assertEquals(GinkgoSpec.XIT, GinkgoSpec.getSpec("XIt"));
        assertEquals(GinkgoSpec.XENTRY, GinkgoSpec.getSpec("XEntry"));
        assertEquals(GinkgoSpec.XSPECIFY, GinkgoSpec.getSpec("XSpecify"));
        assertEquals(GinkgoSpec.INVALID, GinkgoSpec.getSpec("Invalid"));
        assertEquals(GinkgoSpec.INVALID, GinkgoSpec.getSpec(null));
    }

    @Test
    public void getActiveName() {
        assertEquals("It", GinkgoSpec.IT.getActiveName());
        assertEquals("It", GinkgoSpec.PIT.getActiveName());
    }

    @Test
    public void getDisabledName() {
        assertEquals("PIt", GinkgoSpec.IT.getDisabledName());
        assertEquals("PIt", GinkgoSpec.PIT.getDisabledName());
    }
}