package com.tujulishanehub.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String partner;
    
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "project_theme")
    private ProjectTheme projectTheme;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "project_category")
    private ProjectCategory projectCategory;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "activity_type")
    private String activityType;
    
    private String county;
    
    @Column(name = "sub_county")
    private String subCounty;
    
    @Column(name = "maps_address", columnDefinition = "TEXT")
    private String mapsAddress;
    
    @Column(name = "contact_person_name")
    private String contactPersonName;
    
    @Column(name = "contact_person_role")
    private String contactPersonRole;
    
    @Column(name = "contact_person_email")
    private String contactPersonEmail;
    
    @Column(columnDefinition = "TEXT")
    private String objectives;
    
    @Column(precision = 15, scale = 2)
    private java.math.BigDecimal budget;
    
    // New latitude and longitude fields extracted from maps_address
    private Double latitude;
    
    private Double longitude;
    
    // Additional metadata fields
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    private String status; // e.g., "active", "pending", "stalled", "completed", "abandoned"
    
    @Column(name = "completion_percentage")
    private Integer completionPercentage = 0;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "has_reports")
    private Boolean hasReports = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    // Collaboration tracking fields
    @Column(name = "last_modified_by")
    private String lastModifiedBy; // Email of the user who last modified
    
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;
    
    // Bidirectional relationship with ProjectReports
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ProjectReport> reports = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastModifiedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        lastModifiedAt = LocalDateTime.now();
    }
    
    // Helper method to check if coordinates are available
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
    
    // Helper method to get formatted coordinate string
    public String getCoordinatesString() {
        if (hasCoordinates()) {
            return String.format("%.6f,%.6f", latitude, longitude);
        }
        return null;
    }

    // Explicit getter and setter methods for important fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMapsAddress() {
        return mapsAddress;
    }

    public void setMapsAddress(String mapsAddress) {
        this.mapsAddress = mapsAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public ProjectTheme getProjectTheme() {
        return projectTheme;
    }
    
    public void setProjectTheme(ProjectTheme projectTheme) {
        this.projectTheme = projectTheme;
    }
    
    public ProjectCategory getProjectCategory() {
        return projectCategory;
    }
    
    public void setProjectCategory(ProjectCategory projectCategory) {
        this.projectCategory = projectCategory;
    }
    
    // Helper methods for project completion
    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status);
    }
    
    public boolean canCreateReports() {
        return completionPercentage != null && completionPercentage >= 50;
    }
    
    public void markAsCompleted() {
        this.status = "completed";
        this.completionPercentage = 100;
        this.completedAt = LocalDateTime.now();
    }
    
    public Integer getCompletionPercentage() {
        return completionPercentage != null ? completionPercentage : 0;
    }
    
    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
        if (completionPercentage != null && completionPercentage >= 100) {
            this.status = "completed";
            if (this.completedAt == null) {
                this.completedAt = LocalDateTime.now();
            }
        }
    }
    
    public Boolean getHasReports() {
        return hasReports != null ? hasReports : false;
    }
    
    public void setHasReports(Boolean hasReports) {
        this.hasReports = hasReports;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public Set<ProjectReport> getReports() {
        return reports;
    }
    
    public void setReports(Set<ProjectReport> reports) {
        this.reports = reports;
        this.hasReports = reports != null && !reports.isEmpty();
    }
}