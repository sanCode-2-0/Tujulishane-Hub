/**
 * Authentication utilities for Tujulishane Hub frontend
 * Handles JWT token management, API calls with authentication, and user session
 */

// Allow switching between dev and prod base URLs
function getBaseUrl() {
  // 1. Check for ?env=dev or ?env=prod in URL
  const params = new URLSearchParams(window.location.search);
  const env = params.get("env") || localStorage.getItem("apiEnv");
  if (env === "dev") {
    return "https://tujulishane-hub-backend-52b7e709d99f.herokuapp.com";
  }
  // 2. Production: Use Heroku backend
  if (
    window.location.hostname !== "localhost" &&
    window.location.hostname !== "127.0.0.1"
  ) {
    return "https://tujulishane-hub-backend-52b7e709d99f.herokuapp.com";
  }
  // 3. Development: Use local backend
  return "https://tujulishane-hub-backend-52b7e709d99f.herokuapp.com";
}

const AUTH_CONFIG = {
  BASE_URL: getBaseUrl(),
  TOKEN_KEY: "accessToken",
  USER_KEY: "currentUser",
};

class AuthManager {
  constructor() {
    this.baseUrl = AUTH_CONFIG.BASE_URL;
    this.tokenKey = AUTH_CONFIG.TOKEN_KEY;
    this.userKey = AUTH_CONFIG.USER_KEY;
  }

  /**
   * Get stored JWT token
   * @returns {string|null} JWT token or null if not found
   */
  getToken() {
    const token = localStorage.getItem(this.tokenKey);
    console.log("[auth.js] getToken:", token);
    return token;
  }

  /**
   * Store JWT token
   * @param {string} token - JWT token to store
   */
  setToken(token) {
    console.log("[auth.js] setToken: storing token", token);
    localStorage.setItem(this.tokenKey, token);
    const stored = localStorage.getItem(this.tokenKey);
    console.log("[auth.js] setToken: token in localStorage now =", stored);
  }

  /**
   * Remove JWT token from storage
   */
  removeToken() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
  }

  /**
   * Check if user is authenticated
   * @returns {boolean} True if token exists
   */
  isAuthenticated() {
    const token = this.getToken();
    console.log("[auth.js] isAuthenticated: token =", token);
    if (!token) return false;
    // Basic check for token format (should be JWT with 3 parts)
    const parts = token.split(".");
    const valid = parts.length === 3;
    console.log("[auth.js] isAuthenticated: valid =", valid);
    return valid;
  }

  /**
   * Get current user profile from API
   * @returns {Promise<Object>} User profile data
   */
  async getCurrentUser() {
    console.log("[auth.js] getCurrentUser: called");
    try {
      const response = await this.apiCall("/api/auth/profile", "GET");
      console.log("[auth.js] getCurrentUser: response.ok =", response.ok);
      if (response.ok) {
        const data = await response.json();
        console.log("[auth.js] getCurrentUser: received data", data);
        localStorage.setItem(this.userKey, JSON.stringify(data.data));
        const storedUser = localStorage.getItem(this.userKey);
        console.log(
          "[auth.js] getCurrentUser: user in localStorage now =",
          storedUser
        );
        return data.data;
      }
      throw new Error("Failed to get user profile");
    } catch (error) {
      console.error("Error getting current user:", error);
      throw error;
    }
  }

  /**
   * Get cached user data from localStorage
   * @returns {Object|null} User data or null if not cached
   */
  getCachedUser() {
    const userData = localStorage.getItem(this.userKey);
    return userData ? JSON.parse(userData) : null;
  }

  /**
   * Make authenticated API call
   * @param {string} endpoint - API endpoint (e.g., '/api/projects')
   * @param {string} method - HTTP method ('GET', 'POST', 'PUT', 'DELETE')
   * @param {Object} data - Request body data (for POST/PUT)
   * @param {Object} headers - Additional headers
   * @returns {Promise<Response>} Fetch response
   */
  async apiCall(endpoint, method = "GET", data = null, headers = {}) {
    const url = `${this.baseUrl}${endpoint}`;
    const token = this.getToken();

    const config = {
      method,
      headers: {
        "Content-Type": "application/json",
        ...headers,
      },
    };

    // Add authorization header if token exists
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // Add body for POST/PUT requests
    if (data && (method === "POST" || method === "PUT")) {
      config.body = JSON.stringify(data);
    }

    try {
      const response = await fetch(url, config);
      // If unauthorized, redirect to login
      if (response.status === 401) {
        this.logout();
        // window.location.href = "/frontend/index.html";
        return response;
      }
      return response;
    } catch (error) {
      console.error("API call error:", error);
      throw error;
    }
  }

  /**
   * Logout user and clear session data
   */
  logout() {
    this.removeToken();
    // Redirect to home page
    window.location.href = "/frontend/index.html";
  }

  /**
   * Check if user has required role
   * @param {string} requiredRole - Required role ('SUPER_ADMIN', 'PARTNER')
   * @returns {boolean} True if user has required role
   */
  hasRole(requiredRole) {
    const user = this.getCachedUser();
    return user && user.role === requiredRole;
  }

  /**
   * Check if user is approved
   * @returns {boolean} True if user is approved
   */
  isApproved() {
    const user = this.getCachedUser();
    return user && user.approvalStatus === "APPROVED";
  }

  /**
   * Redirect to login if not authenticated
   */
  requireAuth() {
    if (!this.isAuthenticated()) {
      console.error("[auth.js] requireAuth: Not authenticated!");
      // Show error message on page instead of redirecting
      let msg = document.getElementById("authErrorMsg");
      if (!msg) {
        msg = document.createElement("div");
        msg.id = "authErrorMsg";
        msg.style =
          "color: red; font-weight: bold; margin: 2em; text-align: center;";
        msg.textContent =
          "You must be logged in to access this page. (See console for details)";
        document.body.prepend(msg);
      }
      return false;
    }
    return true;
  }

  /**
   * Initialize authentication on page load
   * Checks if user is authenticated and loads user data
   */
  async init() {
    if (this.isAuthenticated()) {
      try {
        const user = await this.getCurrentUser();
        console.log("[auth.js] init: user loaded", user);
      } catch (error) {
        console.error("Failed to load user data:", error);
        // If token is invalid, clear it
        this.removeToken();
      }
    } else {
      console.log("[auth.js] init: not authenticated, skipping getCurrentUser");
    }
  }
}

// Create global instance
const authManager = new AuthManager();

// Auto-initialize on DOM load
document.addEventListener("DOMContentLoaded", async () => {
  await authManager.init();
});

// Export for use in other scripts
window.authManager = authManager;
