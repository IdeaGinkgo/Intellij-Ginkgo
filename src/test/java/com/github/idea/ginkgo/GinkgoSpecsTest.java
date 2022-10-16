package com.github.idea.ginkgo;

import org.junit.Test;

import static com.github.idea.ginkgo.GinkgoSpecs.*;
import static org.junit.Assert.*;

public class GinkgoSpecsTest {
    @Test
    public void test_isGinkgoActiveFunction() {
        assertTrue(isGinkgoActiveSpec("Describe"));
        assertTrue(isGinkgoActiveSpec("DescribeTable"));
        assertTrue(isGinkgoActiveSpec("Context"));
        assertTrue(isGinkgoActiveSpec("When"));
        assertTrue(isGinkgoActiveSpec("It"));
        assertTrue(isGinkgoActiveSpec("Entry"));
        assertTrue(isGinkgoActiveSpec("Specify"));
        assertTrue(isGinkgoActiveSpec("FDescribe"));
        assertTrue(isGinkgoActiveSpec("FDescribeTable"));
        assertTrue(isGinkgoActiveSpec("FContext"));
        assertTrue(isGinkgoActiveSpec("FWhen"));
        assertTrue(isGinkgoActiveSpec("FIt"));
        assertTrue(isGinkgoActiveSpec("FEntry"));
        assertTrue(isGinkgoActiveSpec("FSpecify"));
    }

    @Test
    public void test_isGinkgoPendingFunction() {
        assertTrue(isGinkgoPendingSpec("PDescribe"));
        assertTrue(isGinkgoPendingSpec("PDescribeTable"));
        assertTrue(isGinkgoPendingSpec("PContext"));
        assertTrue(isGinkgoPendingSpec("PWhen"));
        assertTrue(isGinkgoPendingSpec("PIt"));
        assertTrue(isGinkgoPendingSpec("PEntry"));
        assertTrue(isGinkgoPendingSpec("PSpecify"));
        assertTrue(isGinkgoPendingSpec("XDescribe"));
        assertTrue(isGinkgoPendingSpec("XDescribeTable"));
        assertTrue(isGinkgoPendingSpec("XContext"));
        assertTrue(isGinkgoPendingSpec("XWhen"));
        assertTrue(isGinkgoPendingSpec("XIt"));
        assertTrue(isGinkgoPendingSpec("XEntry"));
        assertTrue(isGinkgoPendingSpec("XSpecify"));
    }

    @Test
    public void test_getSpec() {
        assertEquals(DESCRIBE, getSpec("Describe"));
        assertEquals(DESCRIBE_TABLE, getSpec("DescribeTable"));
        assertEquals(CONTEXT, getSpec("Context"));
        assertEquals(WHEN, getSpec("When"));
        assertEquals(IT, getSpec("It"));
        assertEquals(ENTRY, getSpec("Entry"));
        assertEquals(SPECIFY, getSpec("Specify"));
        assertEquals(PDESCRIBE, getSpec("PDescribe"));
        assertEquals(PDESCRIBE_TABLE, getSpec("PDescribeTable"));
        assertEquals(PCONTEXT, getSpec("PContext"));
        assertEquals(PWHEN, getSpec("PWhen"));
        assertEquals(PIT, getSpec("PIt"));
        assertEquals(PENTRY, getSpec("PEntry"));
        assertEquals(PSPECIFY, getSpec("PSpecify"));
        assertEquals(XDESCRIBE, getSpec("XDescribe"));
        assertEquals(XDESCRIBE_TABLE, getSpec("XDescribeTable"));
        assertEquals(XCONTEXT, getSpec("XContext"));
        assertEquals(XWHEN, getSpec("XWhen"));
        assertEquals(XIT, getSpec("XIt"));
        assertEquals(XENTRY, getSpec("XEntry"));
        assertEquals(XSPECIFY, getSpec("XSpecify"));
    }
}