/**
 * Two-Tier Approval Workflow Utility Functions
 * Provides helper functions for managing the approval workflow status
 */

// Workflow status badge styling
export function getWorkflowStatusBadge(status) {
  const badges = {
    PENDING_REVIEW: {
      class: "bg-yellow-100 text-yellow-800",
      icon: "fa-clock",
      text: "Pending Review",
    },
    UNDER_REVIEW: {
      class: "bg-blue-100 text-blue-800",
      icon: "fa-eye",
      text: "Under Review",
    },
    REVIEWED: {
      class: "bg-purple-100 text-purple-800",
      icon: "fa-check-circle",
      text: "Reviewed",
    },
    PENDING_FINAL_APPROVAL: {
      class: "bg-indigo-100 text-indigo-800",
      icon: "fa-hourglass-half",
      text: "Awaiting Final Approval",
    },
    APPROVED: {
      class: "bg-green-100 text-green-800",
      icon: "fa-check-double",
      text: "Approved",
    },
    REJECTED_BY_REVIEWER: {
      class: "bg-red-100 text-red-800",
      icon: "fa-times-circle",
      text: "Rejected by Reviewer",
    },
    REJECTED_BY_APPROVER: {
      class: "bg-red-200 text-red-900",
      icon: "fa-ban",
      text: "Rejected by Approver",
    },
    PENDING: {
      class: "bg-gray-100 text-gray-800",
      icon: "fa-clock",
      text: "Pending",
    },
  };

  return (
    badges[status] || {
      class: "bg-gray-100 text-gray-800",
      icon: "fa-question",
      text: status || "Unknown",
    }
  );
}

// Format workflow status for display
export function formatWorkflowStatus(status) {
  const badge = getWorkflowStatusBadge(status);
  return badge.text;
}

// Get thematic area display name
export function getThematicAreaName(code) {
  const areas = {
    GBV: "Gender-Based Violence",
    AYPSRH: "Adolescent and Young People Sexual and Reproductive Health",
    MNH: "Maternal and Newborn Health",
    FP: "Family Planning",
    CH: "Child Health",
    AH: "Adolescent Health",
    ADV_SBC: "Advocacy and SBC",
    MERL: "MERL - Monitoring, Evaluation, Research and Learning",
  };
  return areas[code] || code;
}

// Thematic areas list
export const THEMATIC_AREAS = [
  { code: "GBV", name: "Gender-Based Violence" },
  { code: "AYPSRH", name: "Adolescent and Young People SRH" },
  { code: "MNH", name: "Maternal and Newborn Health" },
  { code: "FP", name: "Family Planning" },
  { code: "CH", name: "Child Health" },
  { code: "AH", name: "Adolescent Health" },
  { code: "ADV_SBC", name: "Advocacy and SBC" },
  { code: "MERL", name: "MERL - Monitoring, Evaluation, Research and Learning" },
];

// Check if user can review a project
export function canReviewProject(userRole, userThematicArea, projectThemes) {
  if (userRole === "SUPER_ADMIN") return true;
  if (userRole !== "SUPER_ADMIN_REVIEWER") return false;
  if (!userThematicArea) return false;

  // Check if user's thematic area matches any of the project themes
  return projectThemes.some((theme) => theme.code === userThematicArea);
}

// Check if user can do final approval
export function canFinalApprove(userRole) {
  return userRole === "SUPER_ADMIN" || userRole === "SUPER_ADMIN_APPROVER";
}

// Check if user can manage reviewers
export function canManageReviewers(userRole) {
  return userRole === "SUPER_ADMIN" || userRole === "SUPER_ADMIN_APPROVER";
}

// Get workflow status badge HTML
export function getWorkflowStatusBadgeHTML(status) {
  const badge = getWorkflowStatusBadge(status);
  return `<span class="px-3 py-1 ${badge.class} rounded-full text-xs font-medium inline-flex items-center">
        <i class="fas ${badge.icon} mr-1"></i>
        ${badge.text}
    </span>`;
}

// Validate review comments
export function validateReviewComments(comments) {
  if (!comments || comments.trim().length === 0) {
    return { valid: false, message: "Review comments are required" };
  }
  if (comments.trim().length < 10) {
    return {
      valid: false,
      message: "Please provide more detailed comments (at least 10 characters)",
    };
  }
  return { valid: true };
}

// Validate rejection reason
export function validateRejectionReason(reason) {
  if (!reason || reason.trim().length === 0) {
    return { valid: false, message: "Rejection reason is required" };
  }
  if (reason.trim().length < 20) {
    return {
      valid: false,
      message:
        "Please provide a detailed rejection reason (at least 20 characters)",
    };
  }
  return { valid: true };
}

// Format date for display
export function formatDate(dateString) {
  if (!dateString) return "N/A";
  const date = new Date(dateString);
  return date.toLocaleDateString("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

// Get workflow progress percentage
export function getWorkflowProgress(status) {
  const progress = {
    PENDING_REVIEW: 20,
    UNDER_REVIEW: 40,
    REVIEWED: 60,
    PENDING_FINAL_APPROVAL: 80,
    APPROVED: 100,
    REJECTED_BY_REVIEWER: 0,
    REJECTED_BY_APPROVER: 0,
  };
  return progress[status] || 0;
}

// Generate workflow timeline
export function generateWorkflowTimeline(project) {
  const timeline = [];

  // Submission
  if (project.createdAt) {
    timeline.push({
      status: "Submitted",
      date: project.createdAt,
      user: project.partner,
      icon: "fa-paper-plane",
      color: "blue",
    });
  }

  // Review
  if (project.reviewedAt) {
    timeline.push({
      status: "Reviewed",
      date: project.reviewedAt,
      user: `Reviewer #${project.reviewedBy}`,
      comments: project.reviewerComments,
      icon: "fa-check-circle",
      color: "purple",
    });
  }

  // Approval/Rejection
  if (project.approvalWorkflowStatus === "APPROVED") {
    timeline.push({
      status: "Finally Approved",
      date: project.updatedAt,
      icon: "fa-check-double",
      color: "green",
    });
  } else if (project.approvalWorkflowStatus === "REJECTED_BY_APPROVER") {
    timeline.push({
      status: "Rejected",
      date: project.updatedAt,
      icon: "fa-times-circle",
      color: "red",
    });
  }

  return timeline;
}

// API Helper for workflow operations
export class ApprovalWorkflowAPI {
  constructor(baseURL) {
    this.baseURL = baseURL;
  }

  getAuthHeaders() {
    const token = localStorage.getItem("accessToken");
    return {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    };
  }

  async reviewProject(projectId, approved, comments) {
    const response = await fetch(
      `${this.baseURL}/api/projects/admin/review/${projectId}`,
      {
        method: "POST",
        headers: this.getAuthHeaders(),
        body: JSON.stringify({ approved, comments }),
      }
    );

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Failed to review project");
    }

    return await response.json();
  }

  async finalApproveProject(projectId, comments = "") {
    const response = await fetch(
      `${this.baseURL}/api/projects/admin/final-approve/${projectId}`,
      {
        method: "POST",
        headers: this.getAuthHeaders(),
        body: JSON.stringify({ comments }),
      }
    );

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Failed to approve project");
    }

    return await response.json();
  }

  async finalRejectProject(projectId, reason) {
    const response = await fetch(
      `${this.baseURL}/api/projects/admin/final-reject/${projectId}`,
      {
        method: "POST",
        headers: this.getAuthHeaders(),
        body: JSON.stringify({ reason }),
      }
    );

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Failed to reject project");
    }

    return await response.json();
  }

  async getProjectsForReview() {
    const response = await fetch(
      `${this.baseURL}/api/projects/admin/projects-for-review`,
      {
        headers: this.getAuthHeaders(),
      }
    );

    if (!response.ok) {
      throw new Error("Failed to load projects for review");
    }

    const data = await response.json();
    return data.data || [];
  }

  async getProjectsAwaitingFinalApproval() {
    const response = await fetch(
      `${this.baseURL}/api/projects/admin/projects-awaiting-final-approval`,
      {
        headers: this.getAuthHeaders(),
      }
    );

    if (!response.ok) {
      throw new Error("Failed to load projects for approval");
    }

    const data = await response.json();
    return data.data || [];
  }

  async assignThematicArea(userId, thematicArea) {
    const response = await fetch(
      `${this.baseURL}/api/auth/admin/assign-thematic-area/${userId}`,
      {
        method: "POST",
        headers: this.getAuthHeaders(),
        body: JSON.stringify({ thematicArea }),
      }
    );

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Failed to assign thematic area");
    }

    return await response.json();
  }

  async updateUserRoleWithTheme(userId, role, thematicArea = null) {
    const payload = { role };
    if (thematicArea) {
      payload.thematicArea = thematicArea;
    }

    const response = await fetch(
      `${this.baseURL}/api/auth/admin/update-role-with-theme/${userId}`,
      {
        method: "POST",
        headers: this.getAuthHeaders(),
        body: JSON.stringify(payload),
      }
    );

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || "Failed to update user role");
    }

    return await response.json();
  }

  async getAllReviewers() {
    const response = await fetch(`${this.baseURL}/api/auth/admin/reviewers`, {
      headers: this.getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error("Failed to load reviewers");
    }

    const data = await response.json();
    return data.data || [];
  }

  async getReviewersByTheme(themeCode) {
    const response = await fetch(
      `${this.baseURL}/api/auth/admin/reviewers/by-theme/${themeCode}`,
      {
        headers: this.getAuthHeaders(),
      }
    );

    if (!response.ok) {
      throw new Error("Failed to load reviewers for theme");
    }

    const data = await response.json();
    return data.data || [];
  }
}

// Export all functions as default object
export default {
  getWorkflowStatusBadge,
  formatWorkflowStatus,
  getThematicAreaName,
  THEMATIC_AREAS,
  canReviewProject,
  canFinalApprove,
  canManageReviewers,
  getWorkflowStatusBadgeHTML,
  validateReviewComments,
  validateRejectionReason,
  formatDate,
  getWorkflowProgress,
  generateWorkflowTimeline,
  ApprovalWorkflowAPI,
};
