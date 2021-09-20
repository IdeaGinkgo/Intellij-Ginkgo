package com.github.idea.ginkgo;

import com.github.idea.ginkgo.util.GinkgoUtil;

public enum GinkgoPendingSpecType {
    PDESCRIBE("PDescribe"),
    PCONTEXT("PContext"),
    PWHEN("PWhen"),
    PIT("PIt"),
    PSPECIFY("PSpecify"),
    XDESCRIBE("XDescribe"),
    XCONTEXT("XContext"),
    XWHEN("XWhen"),
    XIT("XIt"),
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
