package com.github.idea.ginkgo;

import com.github.idea.ginkgo.util.GinkgoUtil;

public enum GinkgoSpecType {
    DESCRIBE("Describe"),
    CONTEXT("Context"),
    WHEN("When"),
    IT("It"),
    SPECIFY("Specify"),
    FDESCRIBE("FDescribe"),
    FCONTEXT("FContext"),
    FWHEN("FWhen"),
    FIT("FIt"),
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
}
