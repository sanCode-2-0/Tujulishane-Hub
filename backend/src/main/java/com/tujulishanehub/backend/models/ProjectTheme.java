package com.tujulishanehub.backend.models;

/**
 * Enum for allowed project themes in the Tujulishane Hub system
 */
public enum ProjectTheme {
    GBV("Gender Based Violence", "GBV"),
    AYPSRH("Adolescent and Young People Sexual and Reproductive Health", "AYPSRH"),
    MNH("Maternity and Newborn Health", "MNH"),
    FP("Family Planning", "FP"),
    CH("Child Health", "CH"),
    AH("Adolescent Health", "AH"),
    ADV_SBC("Advocacy and SBC", "ADV_SBC"),
    MONITORING_EVALUATION("Monitoring and Evaluation", "MONITORING_EVALUATION"),
    RESEARCH_LEARNING("Research and Learning", "RESEARCH_LEARNING");
    
    private final String displayName;
    private final String code;
    
    ProjectTheme(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public static ProjectTheme fromCode(String code) {
        for (ProjectTheme theme : values()) {
            if (theme.code.equalsIgnoreCase(code)) {
                return theme;
            }
        }
        throw new IllegalArgumentException("Unknown project theme code: " + code);
    }
    
    public static ProjectTheme fromDisplayName(String displayName) {
        for (ProjectTheme theme : values()) {
            if (theme.displayName.equalsIgnoreCase(displayName)) {
                return theme;
            }
        }
        throw new IllegalArgumentException("Unknown project theme: " + displayName);
    }
}