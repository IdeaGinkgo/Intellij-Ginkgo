package com.github.idea.ginkgo;

import com.goide.GoTypes;
import com.goide.psi.GoCallExpr;
import com.goide.psi.GoStringLiteral;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import static com.github.idea.ginkgo.GinkgoRunConfigurationProducer.GINKGO;
import static com.github.idea.ginkgo.GinkgoSpec.INVALID;
import static com.github.idea.ginkgo.GinkgoSpec.WHEN;
import static com.github.idea.ginkgo.util.GinkgoUtil.escapeRegexCharacters;

class GinkgoExpression {
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

    public String getSpecName() {
        String name = specDefinition.getArgumentList().getExpressionList().get(0).getText();
        return escapeRegexCharacters(name);
    }

    public boolean isDynamicTableEntry() {
        return GinkgoSpec.isTableEntity(getSpecType()) && !(specDefinition.getArgumentList().getExpressionList().get(0) instanceof GoStringLiteral);
    }

    public Project getProject() {
        return specDefinition.getProject();
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
