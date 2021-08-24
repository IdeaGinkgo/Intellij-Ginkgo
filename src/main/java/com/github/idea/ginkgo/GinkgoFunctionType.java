package com.github.idea.ginkgo;

public enum GinkgoFunctionType {
    DESCRIBE("Describe"),
    CONTEXT("Context"),
    WHEN("When"),
    IT("It"),
    SPECIFY("Specify");

    private final String specType;

    GinkgoFunctionType(String specType) {
        this.specType = specType;
    }
}
