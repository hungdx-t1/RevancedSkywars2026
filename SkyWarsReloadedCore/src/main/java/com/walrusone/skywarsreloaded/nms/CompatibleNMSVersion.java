package com.walrusone.skywarsreloaded.nms;

public enum CompatibleNMSVersion {
    v1_21_R7(21, "v1_21_R7"),
    ;

    private final int featureVersion;
    private final String nmsImplVersion;

    CompatibleNMSVersion(int featureVersion, String nmsImplVersion) {
        this.featureVersion = featureVersion;
        this.nmsImplVersion = nmsImplVersion;
    }

    public String getNmsImplVersion() {
        return nmsImplVersion;
    }

    static CompatibleNMSVersion getLatestSupported(Integer currentFeatureVersion) {
        // iterate over the available NMS versions and get the latest one that matches the current feature version (not higher than the current version)
        if (currentFeatureVersion != null) {
            for (int i = CompatibleNMSVersion.values().length - 1; i >= 0; i--) {
                CompatibleNMSVersion version = CompatibleNMSVersion.values()[i];
                if (version.getFeatureVersion() <= currentFeatureVersion) {
                    return version;
                }
            }
        }

        return CompatibleNMSVersion.values()[CompatibleNMSVersion.values().length - 1];
    }

    public int getFeatureVersion() {
        return this.featureVersion;
    }
}
