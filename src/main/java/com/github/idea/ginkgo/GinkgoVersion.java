package com.github.idea.ginkgo;

import static java.lang.Integer.parseInt;

public class GinkgoVersion {
    public static GinkgoVersion DEFAULT_VERSION = new GinkgoVersion(0,0,0);
    private final int major;
    private final int minor;
    private final int patch;

    public static GinkgoVersion of(String version) {
        String cleanVersion = version.replace("v", "");
        String[] elements = cleanVersion.split("\\.");
        try {
            return new GinkgoVersion(parseInt(elements[0]), parseInt(elements[1]), parseInt(elements[2]));
        } catch (Exception e) {
            return DEFAULT_VERSION;
        }
    }

    private GinkgoVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public String toString() {
        return major + "." + minor + "." + patch;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public boolean greaterThan(GinkgoVersion version) {
        if (major > version.major) {
            return true;
        }
        if (major == version.major && minor > version.minor) {
            return true;
        }
        return major == version.major && minor == version.minor && patch > version.patch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GinkgoVersion that = (GinkgoVersion) o;

        if (getMajor() != that.getMajor()) return false;
        if (getMinor() != that.getMinor()) return false;
        return getPatch() == that.getPatch();
    }

    @Override
    public int hashCode() {
        int result = getMajor();
        result = 31 * result + getMinor();
        result = 31 * result + getPatch();
        return result;
    }
}
