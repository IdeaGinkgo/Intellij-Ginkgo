package com.github.idea.ginkgo.util;

import com.github.idea.ginkgo.GinkgoFunctionType;

import java.util.Arrays;

public class GinkgoUtil {
    public static boolean isGinkgoFunction(String name) {
        return Arrays.stream(GinkgoFunctionType.class.getEnumConstants()).anyMatch(e -> e.specType().equals(name));
    }
}
