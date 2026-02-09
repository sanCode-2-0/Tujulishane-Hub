// Populate user dropdown with data from authManager
// Listens for "navLoaded" event (dispatched by loadNav.js after nav HTML is injected)

function populateUserNav() {
  const user = authManager.getCachedUser();
  console.log("[authnav] Loading user data for dropdown:", user);

  if (!user) {
    console.warn("[authnav] No user data found in cache");
    return;
  }

  // --------------------------
  // Populate user info
  // --------------------------
  document.querySelectorAll("#userDisplayName").forEach(el => el.textContent = user.name || user.email || "User");
  document.querySelectorAll("#userNameDisplay").forEach(el => el.textContent = user.name || "User");
  document.querySelectorAll("#userEmailDisplay").forEach(el => el.textContent = user.email || "");
  document.querySelectorAll("#userRoleDisplay").forEach(el => el.textContent = user.role || "USER");
  document.querySelectorAll("#userStatusDisplay").forEach(el => el.textContent = user.approvalStatus || "PENDING");

  // --------------------------
  // Role-based navigation logic
  // --------------------------
  console.log("[authnav] User role:", user.role);

  // ADMIN and SUPER_ADMIN (use same nav)
  if (user.role === "ADMIN" || user.role === "SUPER_ADMIN") {
    document.querySelectorAll("#adminNav").forEach(nav => nav.classList.remove("hidden"));
    if (typeof loadPendingRequestsCount === "function") loadPendingRequestsCount();
  }

  // SUPER_ADMIN_REVIEWER
  else if (user.role === "SUPER_ADMIN_REVIEWER") {
    document.querySelectorAll("#adminNav").forEach(nav => nav.classList.remove("hidden"));
    if (typeof loadPendingRequestsCount === "function") loadPendingRequestsCount();
    document.querySelectorAll(".approval-only").forEach(btn => btn.style.display = "none");
  }

  // SUPER_ADMIN_APPROVER
  else if (user.role === "SUPER_ADMIN_APPROVER") {
    document.querySelectorAll("#adminNav").forEach(nav => nav.classList.remove("hidden"));
    if (typeof loadPendingRequestsCount === "function") loadPendingRequestsCount();
    const approvalPanel = document.getElementById("approval-panel");
    if (approvalPanel) approvalPanel.style.display = "block";
  }

  // DONOR
  else if (user.role === "DONOR") {
    document.querySelectorAll("#donorNav").forEach(nav => nav.classList.remove("hidden"));
  }

  // --------------------------
  // Common admin-type elements
  // --------------------------
  if (["ADMIN", "SUPER_ADMIN", "SUPER_ADMIN_REVIEWER", "SUPER_ADMIN_APPROVER"].includes(user.role)) {
    const adminCollabItem = document.getElementById("admin-collab-item");
    if (adminCollabItem) adminCollabItem.style.display = "block";

    const adminCollabMobile = document.getElementById("admin-collab-mobile");
    if (adminCollabMobile) adminCollabMobile.style.display = "block";
  }
}

// Run when nav is loaded (fired by loadNav.js)
window.addEventListener("navLoaded", populateUserNav);

// Also run on DOMContentLoaded as fallback (for pages with inline nav)
document.addEventListener("DOMContentLoaded", () => {
  // Only run if nav-placeholder already has content (inline nav or nav already loaded)
  const placeholder = document.getElementById("nav-placeholder");
  if (placeholder && placeholder.children.length > 0) {
    populateUserNav();
  }
});
