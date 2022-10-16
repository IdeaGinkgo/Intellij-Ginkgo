package com.github.idea.ginkgo;

public class GinkgoSpec {
    private final boolean active;
    private final String name;
    private final String alternateName;

    public GinkgoSpec(boolean active, String name, String alternateName) {
        this.active = active;
        this.name = name;
        this.alternateName = alternateName;
    }

    public String getName() {
        return name;
    }

    public String getActiveName() {
        return active ? name : alternateName;
    }

    public String getDisabledName() {
        return active ? alternateName : name;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isInactive() {
        return !active;
    }

    public boolean withName(String name) {
        return this.name.equals(trimPackageName(name));
    }

    private String trimPackageName(String specType) {
        String[] split = specType.split("\\.");
        return split[split.length - 1];
    }
}
