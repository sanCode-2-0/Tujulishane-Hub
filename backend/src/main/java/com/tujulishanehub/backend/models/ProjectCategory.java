package com.tujulishanehub.backend.models;

/**
 * Enum for project categories in the Tujulishane Hub system
 */
public enum ProjectCategory {
    IMPLEMENTING("Implementing Project", "IMPLEMENTING"),
    RESEARCH("Research Project", "RESEARCH"),
    PRIORITY("Priority Project", "PRIORITY");

    private final String displayName;
    private final String code;

    ProjectCategory(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public static ProjectCategory fromCode(String code) {
        for (ProjectCategory category : values()) {
            if (category.code.equalsIgnoreCase(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown project category code: " + code);
    }

    public static ProjectCategory fromDisplayName(String displayName) {
        for (ProjectCategory category : values()) {
            if (category.displayName.equalsIgnoreCase(displayName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown project category: " + displayName);
    }
}