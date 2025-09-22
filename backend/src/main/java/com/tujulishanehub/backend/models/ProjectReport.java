package com.tujulishanehub.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "project_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "outcomes_achieved", columnDefinition = "TEXT")
    private String outcomesAchieved;
    
    @Column(name = "challenges_faced", columnDefinition = "TEXT")
    private String challengesFaced;
    
    @Column(name = "lessons_learned", columnDefinition = "TEXT")
    private String lessonsLearned;
    
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;
    
    @Column(name = "beneficiaries_reached")
    private Integer beneficiariesReached;
    
    @Column(name = "budget_utilized", precision = 15, scale = 2)
    private java.math.BigDecimal budgetUtilized;
    
    @Column(name = "budget_variance", precision = 15, scale = 2)
    private java.math.BigDecimal budgetVariance;
    
    @Column(name = "completion_percentage")
    private Integer completionPercentage;
    
    // File attachments as JSON array of file paths/URLs
    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments; // JSON array of file paths
    
    // Photos/images as JSON array of image paths/URLs
    @Column(name = "images", columnDefinition = "TEXT")
    private String images; // JSON array of image paths
    
    @Enumerated(EnumType.STRING)
    @Column(name = "report_status")
    private ReportStatus reportStatus = ReportStatus.DRAFT;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private ReportType reportType = ReportType.COMPLETION;
    
    @Column(name = "submitted_by")
    private Long submittedBy;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "reviewed_by")
    private Long reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Report Status enum
    public enum ReportStatus {
        DRAFT,          // Report is being written
        SUBMITTED,      // Report submitted for review
        UNDER_REVIEW,   // Report is being reviewed by admin
        APPROVED,       // Report approved but not yet published
        PUBLISHED,      // Report is published and visible to public
        REJECTED,       // Report was rejected and needs revision
        ARCHIVED        // Report has been archived
    }
    
    // Report Type enum
    public enum ReportType {
        COMPLETION,     // Final project completion report
        INTERIM,        // Interim/progress report
        FINANCIAL,      // Financial report
        IMPACT,         // Impact assessment report
        EVALUATION      // Project evaluation report
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
    
    // Helper methods for report status
    public boolean isDraft() {
        return reportStatus == ReportStatus.DRAFT;
    }
    
    public boolean isSubmitted() {
        return reportStatus == ReportStatus.SUBMITTED;
    }
    
    public boolean isUnderReview() {
        return reportStatus == ReportStatus.UNDER_REVIEW;
    }
    
    public boolean isApproved() {
        return reportStatus == ReportStatus.APPROVED;
    }
    
    public boolean isPublished() {
        return reportStatus == ReportStatus.PUBLISHED;
    }
    
    public boolean isRejected() {
        return reportStatus == ReportStatus.REJECTED;
    }
    
    // Helper methods for report type
    public boolean isCompletionReport() {
        return reportType == ReportType.COMPLETION;
    }
    
    public boolean isInterimReport() {
        return reportType == ReportType.INTERIM;
    }
    
    public boolean isFinancialReport() {
        return reportType == ReportType.FINANCIAL;
    }
    
    // Explicit getter and setter methods (in case Lombok doesn't work properly)
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public ReportStatus getReportStatus() {
        return reportStatus;
    }
    
    public void setReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
    }
    
    public ReportType getReportType() {
        return reportType;
    }
    
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
    
    public String getOutcomesAchieved() {
        return outcomesAchieved;
    }
    
    public void setOutcomesAchieved(String outcomesAchieved) {
        this.outcomesAchieved = outcomesAchieved;
    }
    
    public String getChallengesFaced() {
        return challengesFaced;
    }
    
    public void setChallengesFaced(String challengesFaced) {
        this.challengesFaced = challengesFaced;
    }
    
    public String getLessonsLearned() {
        return lessonsLearned;
    }
    
    public void setLessonsLearned(String lessonsLearned) {
        this.lessonsLearned = lessonsLearned;
    }
    
    public Integer getBeneficiariesReached() {
        return beneficiariesReached;
    }
    
    public void setBeneficiariesReached(Integer beneficiariesReached) {
        this.beneficiariesReached = beneficiariesReached;
    }
    
    public java.math.BigDecimal getBudgetUtilized() {
        return budgetUtilized;
    }
    
    public void setBudgetUtilized(java.math.BigDecimal budgetUtilized) {
        this.budgetUtilized = budgetUtilized;
    }
}