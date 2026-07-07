package com.walrusone.skywarsreloaded.api.enums.cages;

public enum CageType {
    CUBE,
    DOME,
    PYRAMID,
    SPHERE,
    STANDARD;

    public static CageType getNext(CageType type) {
        return switch (type) {
            case CUBE -> CageType.DOME;
            case DOME -> CageType.PYRAMID;
            case PYRAMID -> CageType.SPHERE;
            case STANDARD -> CageType.CUBE;
            default -> CageType.STANDARD;
        };
    }

    public static CageType matchType(String string) {
        for (CageType type : CageType.values()) {
            if (type.toString().equalsIgnoreCase(string)) {
                return type;
            }
        }
        return CageType.STANDARD;
    }
}