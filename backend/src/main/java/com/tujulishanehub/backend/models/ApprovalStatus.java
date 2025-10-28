package com.tujulishanehub.backend.models;

/**
 * Enum representing the approval status of entities (Projects, Users, etc.)
 */
public enum ApprovalStatus {
    PENDING,    // Waiting for MOH approval
    SUBMITTED,  // Submitted for approval
    APPROVED,   // Approved by MOH
    REJECTED    // Rejected by MOH
}
