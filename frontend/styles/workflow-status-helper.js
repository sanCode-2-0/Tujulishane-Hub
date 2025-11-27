/**
 * Workflow Status Badge Helper
 * Simple helper functions for displaying workflow status badges
 * Include this in any page that needs to display workflow status
 */

// Get workflow status badge classes and text
function getWorkflowStatusBadge(status) {
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

// Create workflow status badge HTML
function createWorkflowStatusBadge(status) {
  const badge = getWorkflowStatusBadge(status);
  return `<span class="px-3 py-1 ${badge.class} rounded-full text-xs font-medium inline-flex items-center">
        <i class="fas ${badge.icon} mr-1"></i>
        ${badge.text}
    </span>`;
}

// Get thematic area display name
function getThematicAreaName(code) {
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

// Create thematic area badge
function createThematicBadge(code) {
  const name = getThematicAreaName(code);
  return `<span class="px-2 py-1 bg-blue-100 text-blue-800 rounded text-xs">${name}</span>`;
}

// Format workflow progress percentage
function getWorkflowProgress(status) {
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

// Create progress bar HTML
function createWorkflowProgressBar(status) {
  const progress = getWorkflowProgress(status);
  const colorClass =
    progress === 100
      ? "bg-green-500"
      : progress === 0
      ? "bg-red-500"
      : "bg-blue-500";

  return `
        <div class="w-full bg-gray-200 rounded-full h-2 mb-2">
            <div class="${colorClass} h-2 rounded-full transition-all duration-300" style="width: ${progress}%"></div>
        </div>
        <p class="text-xs text-gray-600">${progress}% Complete</p>
    `;
}

// Check if user has review permissions
function hasReviewPermission(userRole) {
  return ["SUPER_ADMIN", "SUPER_ADMIN_REVIEWER"].includes(userRole);
}

// Check if user has final approval permissions
function hasFinalApprovalPermission(userRole) {
  return ["SUPER_ADMIN", "SUPER_ADMIN_APPROVER"].includes(userRole);
}

// Check if user can manage reviewers
function canManageReviewers(userRole) {
  return ["SUPER_ADMIN", "SUPER_ADMIN_APPROVER"].includes(userRole);
}

// Display reviewer comments section
function createReviewerCommentsSection(project) {
  if (!project.reviewerComments && !project.reviewedBy) {
    return "";
  }

  return `
        <div class="bg-green-50 border-l-4 border-green-500 p-3 mb-2">
            <p class="text-sm text-green-800 mb-1"><strong>Reviewed by:</strong> Reviewer #${
              project.reviewedBy || "N/A"
            }</p>
            ${
              project.reviewedAt
                ? `<p class="text-sm text-green-800 mb-1"><strong>Reviewed on:</strong> ${new Date(
                    project.reviewedAt
                  ).toLocaleDateString()}</p>`
                : ""
            }
            ${
              project.reviewerComments
                ? `
                <p class="text-sm text-green-800 mb-1"><strong>Comments:</strong></p>
                <p class="text-sm text-green-700 italic">${project.reviewerComments}</p>
            `
                : ""
            }
        </div>
    `;
}

// Helper to add workflow status to project cards
function enhanceProjectCardWithWorkflow(projectElement, project) {
  // Add workflow badge
  const statusBadge = createWorkflowStatusBadge(project.approvalWorkflowStatus);
  const badgeContainer = projectElement.querySelector(".workflow-status-badge");
  if (badgeContainer) {
    badgeContainer.innerHTML = statusBadge;
  }

  // Add progress bar if container exists
  const progressContainer = projectElement.querySelector(".workflow-progress");
  if (progressContainer) {
    progressContainer.innerHTML = createWorkflowProgressBar(
      project.approvalWorkflowStatus
    );
  }

  // Add reviewer comments if container exists
  const commentsContainer = projectElement.querySelector(".reviewer-comments");
  if (commentsContainer && project.reviewerComments) {
    commentsContainer.innerHTML = createReviewerCommentsSection(project);
  }
}
