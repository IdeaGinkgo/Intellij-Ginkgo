package com.github.idea.ginkgo;

public enum GinkgoFunctionType {
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

    GinkgoFunctionType(String specType) {
        this.specType = specType;
    }

    public String specType() {
        return specType;
    }
}
