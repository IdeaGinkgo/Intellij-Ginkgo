package com.github.idea.ginkgo.util;

import com.github.idea.ginkgo.GinkgoPendingSpecType;
import com.github.idea.ginkgo.GinkgoRunConfigurationProducer;
import com.github.idea.ginkgo.GinkgoSpecType;
import com.github.idea.ginkgo.GinkgoTestSetupType;
import com.goide.psi.GoCallExpr;
import com.intellij.execution.TestStateStorage;
import com.intellij.execution.testframework.TestIconMapper;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class GinkgoUtil {

    private GinkgoUtil() {
        //Util class should not be instantiated.
    }

    @NotNull
    public static  List<String> getSpecNames(@Nullable PsiElement location, boolean appendWhen) {
        Deque<String> specTree = new ArrayDeque<>();
        while (location != null) {
            GoCallExpr call = ObjectUtils.tryCast(location, GoCallExpr.class);
            if (call != null) {
                StringBuilder nodeNameBuilder = new StringBuilder();

                //Special case append when for When blocks
                if (appendWhen && call.getExpression().getText().equalsIgnoreCase(GinkgoSpecType.WHEN.specType())) {
                    nodeNameBuilder.append(GinkgoRunConfigurationProducer.WHEN);
                }

                nodeNameBuilder.append(call.getArgumentList().getExpressionList().get(0).getText().replace("\"", ""));
                specTree.push(nodeNameBuilder.toString());
            }
            location = location.getParent();
        }

        return specTree.isEmpty() ? Arrays.asList(GinkgoRunConfigurationProducer.GINKGO) : new ArrayList<>(specTree);
    }

    @Nullable
    public static TestStateStorage.Record getTestStateRecord(@Nullable PsiElement element) {
        List<String> specNames = GinkgoUtil.getSpecNames(element, false);
        if (specNames.isEmpty()) {
            return null;
        }
        String specName = specNames.remove(specNames.size() - 1);
        String specContext = String.join(" ", specNames);
        String testUrl = "gotest://" + GinkgoRunConfigurationProducer.GINKGO + "#" + specContext + "/" + specName;

        return TestStateStorage.getInstance(element.getProject()).getState(testUrl);
    }

    @Nullable
    public static Icon getIcon(@Nullable PsiElement element) {
        GoCallExpr goCallExpr = ObjectUtils.tryCast(element, GoCallExpr.class);
        if (goCallExpr == null) {
            return null;
        }

        String methodName = goCallExpr.getExpression().getText();
        boolean isPending = GinkgoUtil.isGinkgoPendingFunction(methodName) || GinkgoUtil.isTablePendingEntity(methodName);
        Icon icon = isPending ? AllIcons.RunConfigurations.TestIgnored : AllIcons.RunConfigurations.TestState.Run;

        TestStateStorage.Record record = getTestStateRecord(element);
        if (record != null) {
            icon = TestIconMapper.getIcon(TestIconMapper.getMagnitude(record.magnitude));
        }
        return icon;
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
