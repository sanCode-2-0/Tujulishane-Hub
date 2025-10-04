package com.tujulishanehub.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "collaboration_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CollaborationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "project", "createdBy"})
    private Announcement announcement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "otp", "otpExpiry"})
    private User requestingUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_organization_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Organization requestingOrganization;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "proposed_contribution", columnDefinition = "TEXT")
    private String proposedContribution;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CollaborationRequestStatus status = CollaborationRequestStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "otp", "otpExpiry"})
    private User reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}