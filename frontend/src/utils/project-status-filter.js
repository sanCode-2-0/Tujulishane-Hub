/**
 * Project Status Filter Utility
 * 
 * Filters projects based on user role and approval workflow status.
 * Only APPROVED projects are visible to non-admin users.
 * MoH admins/reviewers can see all project statuses.
 * 
 * Usage:
 *   const filteredProjects = filterProjectsByApprovalStatus(projects, currentUser);
 */

/**
 * Approval workflow status values (must match backend enum)
 */
const ApprovalWorkflowStatus = {
  PENDING_REVIEW: 'PENDING_REVIEW',
  UNDER_REVIEW: 'UNDER_REVIEW',
  REVIEWED: 'REVIEWED',
  PENDING_FINAL_APPROVAL: 'PENDING_FINAL_APPROVAL',
  APPROVED: 'APPROVED',
  REJECTED_BY_REVIEWER: 'REJECTED_BY_REVIEWER',
  REJECTED_BY_APPROVER: 'REJECTED_BY_APPROVER'
};

/**
 * Check if a user is an admin (MoH admin/reviewer/approver)
 * @param {Object} user - User object with role property
 * @returns {boolean} True if user is admin
 */
function isAdminUser(user) {
  if (!user || !user.role) return false;
  
  const adminRoles = [
    'SUPER_ADMIN',
    'SUPER_ADMIN_REVIEWER',
    'SUPER_ADMIN_APPROVER',
    'ADMIN'
  ];
  
  return adminRoles.includes(user.role);
}

/**
 * Filter projects based on approval workflow status and user role
 * Non-admin users only see APPROVED projects
 * Admin users see all projects
 * 
 * @param {Array} projects - Array of project objects
 * @param {Object} user - Current user object with role property
 * @returns {Array} Filtered array of projects
 */
function filterProjectsByApprovalStatus(projects, user) {
  if (!Array.isArray(projects)) {
    console.warn('[filterProjectsByApprovalStatus] Invalid projects array:', projects);
    return [];
  }
  
  // Admin users see all projects
  if (isAdminUser(user)) {
    console.log('[filterProjectsByApprovalStatus] Admin user - showing all projects:', projects.length);
    return projects;
  }
  
  // Non-admin users only see approved projects
  const filtered = projects.filter(project => {
    // Check approval workflow status (new system)
    if (project.approvalWorkflowStatus === ApprovalWorkflowStatus.APPROVED) {
      return true;
    }
    
    // Fallback: If approval workflow status is not set, don't show the project
    // (unless it's an old project with legacy approval status)
    if (!project.approvalWorkflowStatus && project.approvalStatus === 'APPROVED') {
      return true;
    }
    
    return false;
  });
  
  console.log(
    `[filterProjectsByApprovalStatus] Filtered ${projects.length} projects to ${filtered.length} approved projects for non-admin user`
  );
  
  return filtered;
}

/**
 * Get display-friendly status text for approval workflow status
 * @param {string} status - ApprovalWorkflowStatus value
 * @returns {string} Human-readable status text
 */
function getApprovalStatusDisplayText(status) {
  const statusMap = {
    [ApprovalWorkflowStatus.PENDING_REVIEW]: 'Pending Review',
    [ApprovalWorkflowStatus.UNDER_REVIEW]: 'Under Review',
    [ApprovalWorkflowStatus.REVIEWED]: 'Reviewed',
    [ApprovalWorkflowStatus.PENDING_FINAL_APPROVAL]: 'Pending Final Approval',
    [ApprovalWorkflowStatus.APPROVED]: 'Approved',
    [ApprovalWorkflowStatus.REJECTED_BY_REVIEWER]: 'Rejected by Reviewer',
    [ApprovalWorkflowStatus.REJECTED_BY_APPROVER]: 'Rejected by Approver'
  };
  
  return statusMap[status] || status;
}

/**
 * Check if a project is visible to the current user
 * @param {Object} project - Project object
 * @param {Object} user - Current user object
 * @returns {boolean} True if project should be visible
 */
function isProjectVisible(project, user) {
  if (isAdminUser(user)) {
    return true;
  }
  
  return project.approvalWorkflowStatus === ApprovalWorkflowStatus.APPROVED ||
         (!project.approvalWorkflowStatus && project.approvalStatus === 'APPROVED');
}

// Export for module systems if available
if (typeof module !== 'undefined' && module.exports) {
  module.exports = {
    ApprovalWorkflowStatus,
    isAdminUser,
    filterProjectsByApprovalStatus,
    getApprovalStatusDisplayText,
    isProjectVisible
  };
}
