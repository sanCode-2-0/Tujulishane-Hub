package com.tujulishanehub.backend.payload;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.models.ProjectTheme;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private Boolean emailVerified;
    private String status;
    private User.Role role;
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String organizationName;
    private ProjectTheme thematicArea;
    private List<String> thematicAreas;
    private String phone;
    private String linkedinUrl;
    private String websiteUrl;
    private String bio;

    public UserProfileDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.emailVerified = user.getEmailVerified();
        this.status = user.getStatus();
        this.role = user.getRole();
        this.approvalStatus = user.getApprovalStatus();
        this.createdAt = user.getCreatedAt();
        this.lastLogin = user.getLastLogin();
        this.thematicArea = user.getThematicArea();
        try { this.thematicAreas = user.getThematicAreasAsStrings(); } catch (Exception e) { this.thematicAreas = java.util.Collections.emptyList(); }
        this.phone = user.getPhone();
        this.linkedinUrl = user.getLinkedinUrl();
        this.websiteUrl = user.getWebsiteUrl();
        this.bio = user.getBio();
        try { if (user.getOrganization() != null) this.organizationName = user.getOrganization().getName(); } catch (Exception ignored) {}
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Boolean getEmailVerified() { return emailVerified; }
    public String getStatus() { return status; }
    public User.Role getRole() { return role; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public String getOrganizationName() { return organizationName; }
    public ProjectTheme getThematicArea() { return thematicArea; }
    public List<String> getThematicAreas() { return thematicAreas; }
    public String getPhone() { return phone; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public String getWebsiteUrl() { return websiteUrl; }
    public String getBio() { return bio; }
}