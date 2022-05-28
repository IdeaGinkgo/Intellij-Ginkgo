package com.github.idea.ginkgo.util;

import com.github.idea.ginkgo.GinkgoPendingSpecType;
import com.github.idea.ginkgo.GinkgoRunConfigurationProducer;
import com.github.idea.ginkgo.GinkgoSpecType;
import com.github.idea.ginkgo.GinkgoTestSetupType;
import com.goide.psi.GoCallExpr;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GinkgoUtil {

    private GinkgoUtil() {
        //Util class should not be instantiated.
    }
    public static List<String> getSpecNames(@Nullable PsiElement location, boolean appendWhen) {
        Deque<String> specTree = new ArrayDeque<>();
        while (location != null && location.getParent() != null) {
            location = location.getParent();
            if (location.getParent() instanceof GoCallExpr) {
                GoCallExpr ginkgoSpecFunction = (GoCallExpr) location.getParent();
                StringBuilder nodeNameBuilder = new StringBuilder();

                //Special case append when for When blocks
                if (appendWhen && ginkgoSpecFunction.getExpression().getText().equalsIgnoreCase(GinkgoSpecType.WHEN.specType())) {
                    nodeNameBuilder.append(GinkgoRunConfigurationProducer.WHEN);
                }

                String specName = escapeRegexCharacters(ginkgoSpecFunction.getArgumentList().getExpressionList().get(0).getText());
                nodeNameBuilder.append(specName);
                specTree.push(nodeNameBuilder.toString());
            }
        }

        return specTree.isEmpty() ? Collections.singletonList(GinkgoRunConfigurationProducer.GINKGO) : new ArrayList<>(specTree);
    }

    private static String escapeRegexCharacters(String specName) {
        return specName.replace("\"", "").replace("(", "\\(").replace(")", "\\)");
    }

    public static boolean isGinkgoTestSetup(String name) {
        return Arrays.stream(GinkgoTestSetupType.class.getEnumConstants()).anyMatch(e -> e.testSetupType().equals(name));
    }

    public static boolean isGinkgoFunction(String name) {
        return Arrays.stream(GinkgoSpecType.class.getEnumConstants()).anyMatch(e -> e.specType().equals(name));
    }

    public static boolean isGinkgoPendingFunction(String name) {
        return Arrays.stream(GinkgoPendingSpecType.class.getEnumConstants()).anyMatch(e -> e.specType().equals(name));
    }

    public static boolean isTableEntity(String name) {
        return Arrays.asList(GinkgoSpecType.ENTRY, GinkgoSpecType.FENTRY).stream().anyMatch(e -> e.specType().equals(name));
    }

    public static boolean isTablePendingEntity(String name) {
        return Arrays.asList(GinkgoPendingSpecType.PENTRY, GinkgoPendingSpecType.XENTRY).stream().anyMatch(e -> e.specType().equals(name));
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
