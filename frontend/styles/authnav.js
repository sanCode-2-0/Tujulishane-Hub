// Populate user dropdown with data from authManager
document.addEventListener("DOMContentLoaded", function () {
  // Wait briefly for authManager to initialize
  setTimeout(() => {
    const user = authManager.getCachedUser();
    console.log("[DEBUG] Loading user data for dropdown:", user);

    if (user) {
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
      console.log("[DEBUG] DOMContentLoaded - User role:", user.role);

      // ADMIN and SUPER_ADMIN (use same nav)
      if (user.role === "ADMIN" || user.role === "SUPER_ADMIN") {
        const adminNavItems = document.querySelectorAll("#adminNav");
        adminNavItems.forEach((nav) => nav.classList.remove("hidden"));
        console.log("[DEBUG] Showing adminNav for ADMIN or SUPER_ADMIN");
        if (typeof loadPendingRequestsCount === "function") {
          loadPendingRequestsCount();
        }
      }

      // SUPER_ADMIN_REVIEWER
      else if (user.role === "SUPER_ADMIN_REVIEWER") {
        const adminNavItems = document.querySelectorAll("#adminNav");
        adminNavItems.forEach((nav) => nav.classList.remove("hidden"));
        console.log("[DEBUG] Showing adminNav for SUPER_ADMIN_REVIEWER");
        if (typeof loadPendingRequestsCount === "function") {
          loadPendingRequestsCount();
        }

        // Optional reviewer-only customization
        const approvalButtons = document.querySelectorAll(".approval-only");
        approvalButtons.forEach((btn) => (btn.style.display = "none"));
      }

      // SUPER_ADMIN_APPROVER
      else if (user.role === "SUPER_ADMIN_APPROVER") {
        const adminNavItems = document.querySelectorAll("#adminNav");
        adminNavItems.forEach((nav) => nav.classList.remove("hidden"));
        console.log("[DEBUG] Showing adminNav for SUPER_ADMIN_APPROVER");
        if (typeof loadPendingRequestsCount === "function") {
          loadPendingRequestsCount();
        }

        // Optional approver-only customization
        const approvalPanel = document.getElementById("approval-panel");
        if (approvalPanel) approvalPanel.style.display = "block";
      }

      // DONOR
      else if (user.role === "DONOR") {
        const donorNavItems = document.querySelectorAll("#donorNav");
        donorNavItems.forEach((nav) => nav.classList.remove("hidden"));
        console.log("[DEBUG] Showing donorNav for DONOR");
      }

      // --------------------------
      // Common admin-type elements
      // --------------------------
      if (
        user.role === "ADMIN" ||
        user.role === "SUPER_ADMIN" ||
        user.role === "SUPER_ADMIN_REVIEWER" ||
        user.role === "SUPER_ADMIN_APPROVER"
      ) {
        const adminCollabItem = document.getElementById("admin-collab-item");
        if (adminCollabItem) {
          adminCollabItem.style.display = "block";
          console.log("[DEBUG] adminCollabItem set to block");
        }

        const adminCollabMobile = document.getElementById(
          "admin-collab-mobile"
        );
        if (adminCollabMobile) {
          adminCollabMobile.style.display = "block";
          console.log("[DEBUG] adminCollabMobile set to block");
        }
      }
    } else {
      console.warn("[DEBUG] No user data found in cache");
    }
  }, 500);
});
