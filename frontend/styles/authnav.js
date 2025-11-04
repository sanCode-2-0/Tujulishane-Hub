// Populate user dropdown with data from authManager
document.addEventListener("DOMContentLoaded", function () {
  // Wait a bit for authManager to initialize
  setTimeout(() => {
    const user = authManager.getCachedUser();
    console.log("[DEBUG] Loading user data for dropdown:", user);

    if (user) {
      // Update user display name in button
      const userDisplayName = document.getElementById("userDisplayName");
      if (userDisplayName) {
        userDisplayName.textContent = user.name || user.email || "User";
      }

      // Update user name in dropdown
      const userNameDisplay = document.getElementById("userNameDisplay");
      if (userNameDisplay) {
        userNameDisplay.textContent = user.name || "User";
      }

      // Update email
      const userEmailDisplay = document.getElementById("userEmailDisplay");
      if (userEmailDisplay) {
        userEmailDisplay.textContent = user.email || "";
      }

      // Update role
      const userRoleDisplay = document.getElementById("userRoleDisplay");
      if (userRoleDisplay) {
        userRoleDisplay.textContent = user.role || "USER";
      }

      // Update status
      const userStatusDisplay = document.getElementById("userStatusDisplay");
      if (userStatusDisplay) {
        userStatusDisplay.textContent = user.approvalStatus || "PENDING";
      }

      // Show admin nav if user is admin
      console.log("[DEBUG] DOMContentLoaded - User role:", user.role);
      if (
        user.role === "ADMIN" ||
        user.role === "SUPER_ADMIN" ||
        user.role === "SUPER_ADMIN_REVIEWER" ||
        user.role === "SUPER_ADMIN_APPROVER"
      ) {
        console.log(
          "[DEBUG] DOMContentLoaded - User is admin, showing admin elements"
        );

        const adminNavItems = document.querySelectorAll("#adminNav");
        adminNavItems.forEach((nav) => nav.classList.remove("hidden"));

        // Show admin collaboration menu item
        const adminCollabItem = document.getElementById("admin-collab-item");
        if (adminCollabItem) {
          adminCollabItem.style.display = "block";
          console.log(
            "[DEBUG] DOMContentLoaded - Set adminCollabItem display to block"
          );
        }

        const adminCollabMobile = document.getElementById(
          "admin-collab-mobile"
        );
        if (adminCollabMobile) {
          adminCollabMobile.style.display = "block";
          console.log(
            "[DEBUG] DOMContentLoaded - Set adminCollabMobile display to block"
          );
        }

        // Load pending requests count
        loadPendingRequestsCount();
      } else if (user.role === "DONOR") {
        const donorNavItems = document.querySelectorAll("#donorNav");
        donorNavItems.forEach((nav) => nav.classList.remove("hidden"));
      }
    } else {
      console.warn("[DEBUG] No user data found in cache");
    }
  }, 500);
});
