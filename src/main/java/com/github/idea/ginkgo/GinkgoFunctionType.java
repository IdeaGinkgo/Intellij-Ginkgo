package com.github.idea.ginkgo;

public enum GinkgoFunctionType {
    DESCRIBE("Describe"),
    CONTEXT("Context"),
    IT("It"),
    SPECIFY("Specify");

    private final String specType;

    GinkgoFunctionType(String specType) {
        this.specType = specType;
    }
}
