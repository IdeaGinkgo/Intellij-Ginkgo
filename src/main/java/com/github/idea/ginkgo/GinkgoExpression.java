package com.github.idea.ginkgo;

import com.goide.GoTypes;
import com.goide.psi.GoCallExpr;
import com.goide.psi.GoStringLiteral;
import com.intellij.execution.TestStateStorage;
import com.intellij.execution.testframework.TestIconMapper;
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.github.idea.ginkgo.GinkgoRunConfigurationProducer.GINKGO;
import static com.github.idea.ginkgo.GinkgoSpec.*;
import static com.github.idea.ginkgo.util.GinkgoUtil.escapeRegexCharacters;
import static com.goide.psi.impl.manipulator.GoStringManipulator.unquote;

public class GinkgoExpression {
    // Magnitude value mapping from PoolOfTestStates.java
    public static final int ERROR_INDEX = 8;
    public static final int FAILED_INDEX = 6;
    public static final int COMPLETE_INDEX = 1;
    public static final String WHEN_REGEX = "(when )?";
    public static final GinkgoExpression INVALID_SPEC = new GinkgoExpression(INVALID, null);
    private final GinkgoSpec ginkgoSpec;
    private final GoCallExpr specDefinition;

    public static GinkgoExpression fromPsiElement(@NotNull PsiElement element) {
        boolean isGoExpression = element.getNode().getElementType() == GoTypes.IDENTIFIER && element.getParent().getParent() instanceof GoCallExpr;
        if (!isGoExpression) {
            return INVALID_SPEC;
        }

        GoCallExpr specDefinition = (GoCallExpr) element.getParent().getParent();
        return fromGoCallExpr(specDefinition);
    }

    public static GinkgoExpression fromGoCallExpr(@NotNull GoCallExpr goCallExpr) {
        if (goCallExpr.getArgumentList().getExpressionList().size() < 2) {
            return INVALID_SPEC;
        }

        String specType = goCallExpr.getExpression().getText();
        if (GinkgoSpec.getSpec(specType) == INVALID) {
            return INVALID_SPEC;
        }

        return new GinkgoExpression(GinkgoSpec.getSpec(specType), goCallExpr);
    }

    public GinkgoExpression(GinkgoSpec spec, GoCallExpr specDefinition) {
        this.ginkgoSpec = spec;
        this.specDefinition = specDefinition;
    }

    public boolean isValid() {
        return ginkgoSpec != INVALID;
    }

    public boolean isActive() {
        return ginkgoSpec.isActive();
    }

    public String getSpecType() {
        return specDefinition.getExpression().getText();
    }

    public String getFullName() {
        String testName = unquote(specDefinition.getArgumentList().getExpressionList().get(0).getText());
        return ginkgoSpec != CONTEXT ? specDefinition.getExpression().getText() + " " + testName : testName;
    }

    public PresentationData getPresentationData() {
        return new PresentationData(getFullName(), "", getSpecStateIcon(), null);
    }

    private Icon getSpecStateIcon() {
        TestStateStorage testStateStorage = TestStateStorage.getInstance(getProject());

        TestStateStorage.Record record = testStateStorage.getState(getTestURL());
        if (record != null) {
            return getTestStateIcon(record);
        }

        record = testStateStorage.getState(getTestURLV2());
        if (record != null) {
            return getTestStateIcon(record);
        }

        return AllIcons.RunConfigurations.TestState.Run;
    }

    private static Icon getTestStateIcon(TestStateStorage.Record record) {
        switch (record.magnitude) {
            case ERROR_INDEX, FAILED_INDEX -> {
                return AllIcons.RunConfigurations.TestState.Red2;
            }
            case COMPLETE_INDEX -> {
                return AllIcons.RunConfigurations.TestState.Green2;
            }
            default -> {
                return AllIcons.RunConfigurations.TestState.Run;
            }
        }
    }

    public String getSpecName() {
        String name = specDefinition.getArgumentList().getExpressionList().get(0).getText();
        return escapeRegexCharacters(name);
    }

    public String getUnescapedSpecName() {
        return specDefinition.getArgumentList().getExpressionList().get(0).getText();
    }

    public boolean isDynamicTableEntry() {
        return GinkgoSpec.isTableEntity(getSpecType()) && !(specDefinition.getArgumentList().getExpressionList().get(0) instanceof GoStringLiteral);
    }

    public Project getProject() {
        return specDefinition.getProject();
    }

    public List<String> getSpecLocation() {
        GinkgoExpression parent = getParentSpec();
        List<String> specLocations = new ArrayList<>();
        if (parent != null) {
            specLocations.addAll(parent.getSpecLocation());
        }
        specLocations.add(getSpecName());

        return specLocations;
    }

    public String getFocusExpression() {
        GinkgoExpression parent = getParentSpec();
        String name = ginkgoSpec == WHEN ? WHEN_REGEX + getSpecName() : getSpecName();
        return parent != null ? parent.getFocusExpression() + " " + name : name;
    }

    public String getTestURL() {
        GinkgoExpression parent = getParentSpec();
        if (parent == null) {
            return "";
        }

       return "gotest://" + GINKGO + "#" + parent.getFocusExpression() + "/" + getSpecName();
    }

    public String getTestURLV2() {
        GinkgoExpression parent = getParentSpec();
        if (parent == null) {
            return "";
        }

        return "gotest://" + GINKGO + "#" + parent.getFocusExpression() + " " + getSpecName();
    }

    public String getTestURLV3() {
        GinkgoExpression parent = getParentSpec();
        if (parent == null) {
            return "";
        }

        return String.format("gotest://%s#%s %s", GINKGO, parent.getFocusExpression(), getUnescapedSpecName()).
                replace("\"", "").
                replace("\\", "");
    }

    private GinkgoExpression getParentSpec() {
        GoCallExpr goExpression = getParentExpression(specDefinition);
        if (goExpression == null) {
            return null;
        }

        GinkgoExpression ginkgoExpression = fromGoCallExpr(goExpression);
        if (!ginkgoExpression.isValid()) {
            return null;
        }

        return ginkgoExpression;
    }

    private GoCallExpr getParentExpression(GoCallExpr specDefinition) {
        PsiElement location = specDefinition.getParent();
        while (location != null) {
            if (location instanceof GoCallExpr) {
                return (GoCallExpr) location;
            }
            location = location.getParent();
        }

        return null;
    }
}
