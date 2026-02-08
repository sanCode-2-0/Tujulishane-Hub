package com.tujulishanehub.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"users", "hibernateLazyInitializer", "handler"})
public class Organization {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type")
    private OrganizationType organizationType;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "website_url")
    private String websiteUrl;
    
    @Column(name = "registration_number")
    private String registrationNumber;
    
    @Column(name = "logo_data", columnDefinition = "bytea")
    private byte[] logoData;
    
    @Column(name = "logo_content_type")
    private String logoContentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Bidirectional relationship with Users
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private Set<User> users;
    
    // Organization Type enum
    public enum OrganizationType {
        NGO,                    // Non-Governmental Organization
        GOVERNMENT_AGENCY,      // Government Agency
        PRIVATE_COMPANY,        // Private Company/Corporation
        INTERNATIONAL_ORG,      // International Organization
        FOUNDATION,             // Foundation/Trust
        COMMUNITY_GROUP,        // Community-Based Organization
        ACADEMIC_INSTITUTION,   // University/Research Institution
        HEALTHCARE_PROVIDER,    // Hospital/Clinic
        DONOR_AGENCY,          // Donor/Funding Agency
        OTHER                  // Other type
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods for approval status
    public boolean isApproved() {
        return approvalStatus == ApprovalStatus.APPROVED;
    }
    
    public boolean isPending() {
        return approvalStatus == ApprovalStatus.PENDING;
    }
    
    public boolean isSubmitted() {
        return approvalStatus == ApprovalStatus.SUBMITTED;
    }
    
    public boolean isRejected() {
        return approvalStatus == ApprovalStatus.REJECTED;
    }
    
    // Helper methods for organization type checks
    public boolean isNGO() {
        return organizationType == OrganizationType.NGO;
    }
    
    public boolean isGovernmentAgency() {
        return organizationType == OrganizationType.GOVERNMENT_AGENCY;
    }
    
    public boolean isDonorAgency() {
        return organizationType == OrganizationType.DONOR_AGENCY;
    }
    
    // Check if organization can have projects
    public boolean canCreateProjects() {
        return isApproved();
    }
    
    // Explicit getter and setter methods (in case Lombok doesn't work properly)
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public OrganizationType getOrganizationType() {
        return organizationType;
    }
    
    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }
    
    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }
    
    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}