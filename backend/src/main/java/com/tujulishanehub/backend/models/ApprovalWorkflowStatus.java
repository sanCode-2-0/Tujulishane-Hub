package com.tujulishanehub.backend.models;

/**
 * Enum representing the two-tier approval workflow status for projects
 * This supports the specialized SUPER_ADMIN approval process with thematic reviewers
 */
public enum ApprovalWorkflowStatus {
    PENDING_REVIEW,             // Project submitted, awaiting review by thematic area reviewer
    UNDER_REVIEW,               // Project is currently being reviewed by thematic reviewer
    REVIEWED,                   // Thematic reviewer approved, awaiting final approval
    PENDING_FINAL_APPROVAL,     // Ready for final approval by SUPER_ADMIN_APPROVER
    APPROVED,                   // Final approval granted by SUPER_ADMIN_APPROVER
    REJECTED_BY_REVIEWER,       // Rejected by thematic area reviewer
    REJECTED_BY_APPROVER;       // Rejected by final approver (SUPER_ADMIN_APPROVER)
    
    /**
     * Check if this status indicates the project is approved
     */
    public boolean isApproved() {
        return this == APPROVED;
    }
    
    /**
     * Check if this status indicates the project is rejected
     */
    public boolean isRejected() {
        return this == REJECTED_BY_REVIEWER || this == REJECTED_BY_APPROVER;
    }
    
    /**
     * Check if this status indicates the project is pending some action
     */
    public boolean isPending() {
        return this == PENDING_REVIEW || this == UNDER_REVIEW || 
               this == REVIEWED || this == PENDING_FINAL_APPROVAL;
    }
    
    /**
     * Check if project is awaiting reviewer action
     */
    public boolean awaitingReviewer() {
        return this == PENDING_REVIEW || this == UNDER_REVIEW;
    }
    
    /**
     * Check if project is awaiting final approver action
     */
    public boolean awaitingApprover() {
        return this == REVIEWED || this == PENDING_FINAL_APPROVAL;
    }
}
