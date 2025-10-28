package com.tujulishanehub.backend.payload;

public class ProjectThemeDto {
    private String code;
    private String name;

    public ProjectThemeDto() {
    }

    public ProjectThemeDto(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
