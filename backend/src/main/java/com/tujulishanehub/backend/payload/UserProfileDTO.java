package com.tujulishanehub.backend.payload;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.models.ProjectTheme;

import java.time.LocalDateTime;

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
    private String organizationName; // Just the name, not the full object
    private ProjectTheme thematicArea; // Thematic area for SUPER_ADMIN_REVIEWER

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
        this.thematicArea = user.getThematicArea(); // Include thematic area
        if (user.getOrganization() != null) {
            this.organizationName = user.getOrganization().getName();
        }
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
}