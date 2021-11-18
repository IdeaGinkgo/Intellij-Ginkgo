package com.github.idea.ginkgo.util;

import com.github.idea.ginkgo.GinkgoPendingSpecType;
import com.github.idea.ginkgo.GinkgoSpecType;
import com.goide.psi.GoCallExpr;

import java.util.Arrays;

public class GinkgoUtil {

    private GinkgoUtil() {
        //Util class should not be instantiated.
    }

    public static boolean isGinkgoSuite(GoCallExpr function) {
        return function.getExpression().getText().equals("RunSpecs");
    }

    public static boolean isGinkgoFunction(GoCallExpr function) {
        String name = function.getExpression().getText();
        return Arrays.stream(GinkgoSpecType.class.getEnumConstants()).anyMatch(e -> e.specType().equals(name));
    }

    public static boolean isGinkgoPendingFunction(GoCallExpr function) {
        String name = function.getExpression().getText();
        return Arrays.stream(GinkgoPendingSpecType.class.getEnumConstants()).anyMatch(e -> e.specType().equals(name));
    }

    public static GinkgoPendingSpecType getGinkgoPendingSpecType(String name) {
        return Arrays.stream(GinkgoPendingSpecType.class.getEnumConstants())
                .filter(e -> e.specType().equals(name))
                .findFirst()
                .orElseThrow(() -> new EnumConstantNotPresentException(GinkgoPendingSpecType.class, name));
    }

    public static GinkgoSpecType getGinkgoSpecType(String name) {
        return Arrays.stream(GinkgoSpecType.class.getEnumConstants())
                .filter(e -> e.specType().equals(name))
                .findFirst()
                .orElseThrow(() -> new EnumConstantNotPresentException(GinkgoPendingSpecType.class, name));
    }
}
