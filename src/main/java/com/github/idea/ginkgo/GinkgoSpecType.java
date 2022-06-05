package com.github.idea.ginkgo;

import com.github.idea.ginkgo.util.GinkgoUtil;

import java.util.EnumSet;

public enum GinkgoSpecType {
    DESCRIBE("Describe"),
    DESCRIBE_TABLE("DescribeTable"),
    CONTEXT("Context"),
    WHEN("When"),
    IT("It"),
    ENTRY("Entry"),
    SPECIFY("Specify"),
    FDESCRIBE("FDescribe"),
    FDESCRIBE_TABLE("FDescribeTable"),
    FCONTEXT("FContext"),
    FWHEN("FWhen"),
    FIT("FIt"),
    FENTRY("FEntry"),
    FSPECIFY("FSpecify");

    private final String specType;

    GinkgoSpecType(String specType) {
        this.specType = specType;
    }

    public String specType() {
        return specType;
    }

    public GinkgoPendingSpecType getPendingSpecType() {
        return GinkgoUtil.getGinkgoPendingSpecType("P"+specType);
    }

    public static boolean isGinkgoSpec(String spec) {
        return EnumSet.allOf(GinkgoSpecType.class).stream().anyMatch(e -> e.specType().equals(spec));
    }
}
