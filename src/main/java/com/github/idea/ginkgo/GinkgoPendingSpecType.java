package com.github.idea.ginkgo;

import com.github.idea.ginkgo.util.GinkgoUtil;

public enum GinkgoPendingSpecType {
    PDESCRIBE("PDescribe"),
    PDESCRIBE_TABLE("PDescribeTable"),
    PCONTEXT("PContext"),
    PWHEN("PWhen"),
    PIT("PIt"),
    PENTRY("PEntry"),
    PSPECIFY("PSpecify"),
    XDESCRIBE("XDescribe"),
    XDESCRIBE_TABLE("XDescribeTable"),
    XCONTEXT("XContext"),
    XWHEN("XWhen"),
    XIT("XIt"),
    XENTRY("XEntry"),
    XSPECIFY("XSpecify");

    private final String specType;

    GinkgoPendingSpecType(String specType) {
        this.specType = specType;
    }

    public String specType() {
        return specType;
    }

    public GinkgoSpecType activeSpecType() {
        return GinkgoUtil.getGinkgoSpecType(specType.substring(1));
    }
}
