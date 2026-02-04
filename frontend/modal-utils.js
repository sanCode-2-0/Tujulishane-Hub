/**
 * Modal Utilities - Reusable modal functions to replace alert() and confirm()
 *
 * This file provides modern, accessible modal dialogs that replace
 * the browser's default alert() and confirm() functions.
 */

// Initialize modals on DOM load
document.addEventListener("DOMContentLoaded", function () {
  // Create modal containers if they don't exist
  if (!document.getElementById("globalConfirmModal")) {
    createConfirmModal();
  }
  if (!document.getElementById("globalAlertModal")) {
    createAlertModal();
  }
});

/**
 * Create the global confirm modal
 */
function createConfirmModal() {
  const modalHTML = `
    <div
      id="globalConfirmModal"
      class="fixed left-0 top-0 backdrop-blur-md z-[9999] hidden h-full w-full overflow-y-auto overflow-x-hidden outline-none"
      tabindex="-1"
      role="dialog"
      aria-labelledby="globalConfirmModalTitle"
      aria-modal="true"
    >
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="relative w-full max-w-md bg-white rounded-lg shadow-xl">
          <!-- Modal Header -->
          <div class="flex items-center justify-between p-5 border-b border-gray-200">
            <h3 id="globalConfirmModalTitle" class="text-lg font-semibold text-gray-900">
              Confirm Action
            </h3>
            <button
              type="button"
              id="globalConfirmModalCloseBtn"
              class="text-gray-400 hover:text-gray-600 transition"
              aria-label="Close"
            >
              <i class="fas fa-times text-xl"></i>
            </button>
          </div>

          <!-- Modal Body -->
          <div class="p-6">
            <p id="globalConfirmModalMessage" class="text-gray-700 leading-relaxed"></p>
          </div>

          <!-- Modal Footer -->
          <div class="flex items-center justify-end gap-3 p-5 border-t border-gray-200">
            <button
              type="button"
              id="globalConfirmModalCancelBtn"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition"
            >
              Cancel
            </button>
            <button
              type="button"
              id="globalConfirmModalConfirmBtn"
              class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition"
            >
              Confirm
            </button>
          </div>
        </div>
      </div>
    </div>
  `;

  document.body.insertAdjacentHTML("beforeend", modalHTML);
}

/**
 * Create the global alert modal
 */
function createAlertModal() {
  const modalHTML = `
    <div
      id="globalAlertModal"
      class="fixed left-0 top-0 backdrop-blur-md z-[9999] hidden h-full w-full overflow-y-auto overflow-x-hidden outline-none"
      tabindex="-1"
      role="dialog"
      aria-labelledby="globalAlertModalTitle"
      aria-modal="true"
    >
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="relative w-full max-w-md bg-white rounded-lg shadow-xl">
          <!-- Modal Header -->
          <div class="flex items-center justify-between p-5 border-b border-gray-200">
            <h3 id="globalAlertModalTitle" class="text-lg font-semibold text-gray-900">
              <i id="globalAlertModalIcon" class="mr-2"></i>
              <span id="globalAlertModalTitleText">Notice</span>
            </h3>
            <button
              type="button"
              id="globalAlertModalCloseBtn"
              class="text-gray-400 hover:text-gray-600 transition"
              aria-label="Close"
            >
              <i class="fas fa-times text-xl"></i>
            </button>
          </div>

          <!-- Modal Body -->
          <div class="p-6">
            <p id="globalAlertModalMessage" class="text-gray-700 leading-relaxed whitespace-pre-wrap"></p>
          </div>

          <!-- Modal Footer -->
          <div class="flex items-center justify-end gap-3 p-5 border-t border-gray-200">
            <button
              type="button"
              id="globalAlertModalOkBtn"
              class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition"
            >
              OK
            </button>
          </div>
        </div>
      </div>
    </div>
  `;

  document.body.insertAdjacentHTML("beforeend", modalHTML);
}

/**
 * Show a confirmation dialog (replaces window.confirm())
 * @param {string} message - The message to display
 * @param {string} title - Optional title (default: "Confirm Action")
 * @param {string} confirmText - Optional confirm button text (default: "Confirm")
 * @param {string} cancelText - Optional cancel button text (default: "Cancel")
 * @returns {Promise<boolean>} - Resolves to true if confirmed, false if cancelled
 */
window.showConfirm = function (
  message,
  title = "Confirm Action",
  confirmText = "Confirm",
  cancelText = "Cancel"
) {
  return new Promise((resolve) => {
    const modal = document.getElementById("globalConfirmModal");
    const titleElement = document.getElementById("globalConfirmModalTitle");
    const messageElement = document.getElementById("globalConfirmModalMessage");
    const confirmBtn = document.getElementById("globalConfirmModalConfirmBtn");
    const cancelBtn = document.getElementById("globalConfirmModalCancelBtn");
    const closeBtn = document.getElementById("globalConfirmModalCloseBtn");

    // Set content
    titleElement.textContent = title;
    messageElement.textContent = message;
    confirmBtn.textContent = confirmText;
    cancelBtn.textContent = cancelText;

    // Show modal
    modal.classList.remove("hidden");

    // Handle confirm
    const handleConfirm = () => {
      cleanup();
      resolve(true);
    };

    // Handle cancel
    const handleCancel = () => {
      cleanup();
      resolve(false);
    };

    // Handle escape key
    const handleEscape = (e) => {
      if (e.key === "Escape") {
        handleCancel();
      }
    };

    // Cleanup function
    const cleanup = () => {
      modal.classList.add("hidden");
      confirmBtn.removeEventListener("click", handleConfirm);
      cancelBtn.removeEventListener("click", handleCancel);
      closeBtn.removeEventListener("click", handleCancel);
      document.removeEventListener("keydown", handleEscape);
    };

    // Add event listeners
    confirmBtn.addEventListener("click", handleConfirm);
    cancelBtn.addEventListener("click", handleCancel);
    closeBtn.addEventListener("click", handleCancel);
    document.addEventListener("keydown", handleEscape);

    // Close on backdrop click
    modal.addEventListener(
      "click",
      function (e) {
        if (e.target === modal) {
          handleCancel();
        }
      },
      { once: true }
    );
  });
};

/**
 * Show an alert dialog (replaces window.alert())
 * @param {string} message - The message to display
 * @param {string} title - Optional title (default: "Notice")
 * @param {string} type - Optional type: 'info', 'success', 'warning', 'error' (default: 'info')
 * @returns {Promise<void>} - Resolves when the user clicks OK
 */
window.showAlert = function (message, title = "Notice", type = "info") {
  return new Promise((resolve) => {
    const modal = document.getElementById("globalAlertModal");
    const titleTextElement = document.getElementById(
      "globalAlertModalTitleText"
    );
    const iconElement = document.getElementById("globalAlertModalIcon");
    const messageElement = document.getElementById("globalAlertModalMessage");
    const okBtn = document.getElementById("globalAlertModalOkBtn");
    const closeBtn = document.getElementById("globalAlertModalCloseBtn");

    // Set content
    titleTextElement.textContent = title;
    messageElement.innerHTML = message; // Changed from textContent to innerHTML to support HTML content

    // Set icon based on type
    iconElement.className = "";
    switch (type) {
      case "success":
        iconElement.className = "fas fa-check-circle text-green-600 mr-2";
        okBtn.className =
          "px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-lg hover:bg-green-700 transition";
        break;
      case "warning":
        iconElement.className =
          "fas fa-exclamation-triangle text-yellow-600 mr-2";
        okBtn.className =
          "px-4 py-2 text-sm font-medium text-white bg-yellow-600 rounded-lg hover:bg-yellow-700 transition";
        break;
      case "error":
        iconElement.className = "fas fa-exclamation-circle text-red-600 mr-2";
        okBtn.className =
          "px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-lg hover:bg-red-700 transition";
        break;
      default: // info
        iconElement.className = "fas fa-info-circle text-blue-600 mr-2";
        okBtn.className =
          "px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition";
    }

    // Show modal
    modal.classList.remove("hidden");

    // Handle OK
    const handleOk = () => {
      cleanup();
      resolve();
    };

    // Handle escape key
    const handleEscape = (e) => {
      if (e.key === "Escape") {
        handleOk();
      }
    };

    // Cleanup function
    const cleanup = () => {
      modal.classList.add("hidden");
      okBtn.removeEventListener("click", handleOk);
      closeBtn.removeEventListener("click", handleOk);
      document.removeEventListener("keydown", handleEscape);
    };

    // Add event listeners
    okBtn.addEventListener("click", handleOk);
    closeBtn.addEventListener("click", handleOk);
    document.addEventListener("keydown", handleEscape);

    // Close on backdrop click
    modal.addEventListener(
      "click",
      function (e) {
        if (e.target === modal) {
          handleOk();
        }
      },
      { once: true }
    );
  });
};

// Export for module usage if needed
if (typeof module !== "undefined" && module.exports) {
  module.exports = { showConfirm, showAlert };
}
