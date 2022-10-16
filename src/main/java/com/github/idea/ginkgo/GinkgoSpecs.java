package com.github.idea.ginkgo;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class GinkgoSpecs {
    public static final GinkgoSpec INVALID = new GinkgoSpec(false, null, null);
    //Standard
    public static final GinkgoSpec DESCRIBE = new GinkgoSpec(true, "Describe", "PDescribe");
    public static final GinkgoSpec DESCRIBE_TABLE = new GinkgoSpec(true, "DescribeTable", "PDescribeTable");
    public static final GinkgoSpec CONTEXT = new GinkgoSpec(true, "Context", "PContext");
    public static final GinkgoSpec WHEN = new GinkgoSpec(true, "When", "PWhen");
    public static final GinkgoSpec IT = new GinkgoSpec(true, "It", "PIt");
    public static final GinkgoSpec ENTRY = new GinkgoSpec(true, "Entry", "PEntry");
    public static final GinkgoSpec SPECIFY = new GinkgoSpec(true, "Specify", "PSpecify");

    //Focus
    public static final GinkgoSpec FDESCRIBE = new GinkgoSpec(true, "FDescribe", "PDescribe");
    public static final GinkgoSpec FDESCRIBE_TABLE = new GinkgoSpec(true, "FDescribeTable", "PDescribeTable");
    public static final GinkgoSpec FCONTEXT = new GinkgoSpec(true, "FContext", "PContext");
    public static final GinkgoSpec FWHEN = new GinkgoSpec(true, "FWhen", "PWhen");
    public static final GinkgoSpec FIT = new GinkgoSpec(true, "FIt", "PIt");
    public static final GinkgoSpec FENTRY = new GinkgoSpec(true, "FEntry", "PEntry");
    public static final GinkgoSpec FSPECIFY = new GinkgoSpec(true, "FSpecify", "PSpecify");

    //Pending
    public static final GinkgoSpec PDESCRIBE = new GinkgoSpec(false, "PDescribe", "Describe");
    public static final GinkgoSpec PDESCRIBE_TABLE = new GinkgoSpec(false, "PDescribeTable", "DescribeTable");
    public static final GinkgoSpec PCONTEXT = new GinkgoSpec(false, "PContext", "Context");
    public static final GinkgoSpec PWHEN = new GinkgoSpec(false, "PWhen", "When");
    public static final GinkgoSpec PIT = new GinkgoSpec(false, "PIt", "It");
    public static final GinkgoSpec PENTRY = new GinkgoSpec(false, "PEntry", "Entry");
    public static final GinkgoSpec PSPECIFY = new GinkgoSpec(false, "PSpecify", "Specify");

    //Pending
    public static final GinkgoSpec XDESCRIBE = new GinkgoSpec(false, "XDescribe", "Describe");
    public static final GinkgoSpec XDESCRIBE_TABLE = new GinkgoSpec(false, "XDescribeTable", "DescribeTable");
    public static final GinkgoSpec XCONTEXT = new GinkgoSpec(false, "XContext", "Context");
    public static final GinkgoSpec XWHEN = new GinkgoSpec(false, "XWhen", "When");
    public static final GinkgoSpec XIT = new GinkgoSpec(false, "XIt", "It");
    public static final GinkgoSpec XENTRY = new GinkgoSpec(false, "XEntry", "Entry");
    public static final GinkgoSpec XSPECIFY = new GinkgoSpec(false, "XSpecify", "Specify");

    public static final List<GinkgoSpec> GINKGO_SPECS = Arrays.asList(
            DESCRIBE, DESCRIBE_TABLE, CONTEXT, WHEN, IT, ENTRY, SPECIFY,
            FDESCRIBE, FDESCRIBE_TABLE, FCONTEXT, FWHEN, FIT, FENTRY, FSPECIFY,
            PDESCRIBE, PDESCRIBE_TABLE, PCONTEXT, PWHEN, PIT, PENTRY, PSPECIFY,
            XDESCRIBE, XDESCRIBE_TABLE, XCONTEXT, XWHEN, XIT, XENTRY, XSPECIFY);

    public static GinkgoSpec getSpec(String specType) {
        if (specType == null) {
            return INVALID;
        }
        return GINKGO_SPECS.stream()
                .filter(ginkgoSpec -> ginkgoSpec.withName(specType))
                .findFirst()
                .orElse(INVALID);
    }

    public static boolean isGinkgoActiveSpec(String specType) {
        if (specType == null) {
            return false;
        }
        return GINKGO_SPECS.stream()
                .filter(GinkgoSpec::isActive)
                .anyMatch(ginkgoSpec -> ginkgoSpec.withName(specType));
    }

    public static boolean isGinkgoPendingSpec(String specType) {
        if (specType == null) {
            return false;
        }
        return GINKGO_SPECS.stream()
                .filter(GinkgoSpec::isInactive)
                .anyMatch(ginkgoSpec -> ginkgoSpec.withName(specType));
    }

    public static boolean isTableEntity(String specType) {
        return Stream.of(ENTRY, FENTRY)
                .anyMatch(ginkgoSpec -> ginkgoSpec.withName(specType));
    }

    public static boolean isTablePendingEntity(String specType) {
        return Stream.of(PENTRY, XENTRY)
                .anyMatch(ginkgoSpec -> ginkgoSpec.withName(specType));
    }
}
