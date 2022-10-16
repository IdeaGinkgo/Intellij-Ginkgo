package com.github.idea.ginkgo.util;

import com.github.idea.ginkgo.GinkgoRunConfigurationProducer;
import com.github.idea.ginkgo.GinkgoTestSetupType;
import com.goide.psi.GoCallExpr;
import com.goide.psi.GoExpression;
import com.goide.psi.GoFile;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.github.idea.ginkgo.GinkgoRunConfigurationProducer.WHEN_REGEX;
import static com.github.idea.ginkgo.GinkgoSpecs.WHEN;
import static com.github.idea.ginkgo.GinkgoSpecs.getSpec;

public class GinkgoUtil {
    public static final Logger LOG = Logger.getInstance(GinkgoUtil.class);
    public static final String GINKGO_IMPORT = "github.com/onsi/ginkgo";

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
                if (appendWhen && getSpec(ginkgoSpecFunction.getExpression().getText()) == WHEN) {
                    nodeNameBuilder.append(WHEN_REGEX);
                }

                String specName = escapeRegexCharacters(getSpecDescription(ginkgoSpecFunction).orElse(""));
                nodeNameBuilder.append(specName);
                specTree.push(nodeNameBuilder.toString());
            }
        }

        return specTree.isEmpty() ? Collections.singletonList(GinkgoRunConfigurationProducer.GINKGO) : new ArrayList<>(specTree);
    }

    private static Optional<String> getSpecDescription(GoCallExpr ginkgoSpecFunction) {
        List<GoExpression> expressionList = ginkgoSpecFunction.getArgumentList().getExpressionList();
        if (expressionList.isEmpty()) {
            LOG.error("Could not get spec description for function: %s", ginkgoSpecFunction.getText());
            return Optional.empty();
        }

        return Optional.of(expressionList.get(0).getText());
    }

    public static String escapeRegexCharacters(String specName) {
        return specName.replace("\"", "").replace("(", "\\(").replace(")", "\\)");
    }

    public static boolean isGinkgoTestSetup(String name) {
        return Arrays.stream(GinkgoTestSetupType.class.getEnumConstants()).anyMatch(e -> e.testSetupType().equals(name));
    }

    public static boolean isGinkgoTestFile(PsiFile file) {
        return file instanceof GoFile && importsGinkgo((GoFile) file);
    }

    private static boolean importsGinkgo(GoFile file) {
        return file.getImports().stream().anyMatch(goImportSpec -> goImportSpec.getPath().contains(GINKGO_IMPORT));
    }
}
