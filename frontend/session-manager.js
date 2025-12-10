/**
 * Session Manager for Tujulishane Hub
 * Handles session timeout, warnings, auto-logout, and activity tracking
 * 
 * Features:
 * - Session expiration warnings
 * - Auto-logout on session expiration
 * - Activity tracking to update session
 * - Remember Me functionality
 * - Session state persistence
 */

class SessionManager {
  constructor() {
    this.warningShown = false;
    this.checkInterval = null;
    this.activityTimeout = null;
    this.warningModal = null;
    
    // Bind methods
    this.checkSession = this.checkSession.bind(this);
    this.updateActivity = this.updateActivity.bind(this);
    this.handleSessionExpired = this.handleSessionExpired.bind(this);
    this.showWarning = this.showWarning.bind(this);
    this.dismissWarning = this.dismissWarning.bind(this);
    
    console.log('[session-manager.js] SessionManager initialized');
  }

  /**
   * Initialize session manager
   * Sets up session tracking and activity monitoring
   */
  init() {
    // Only initialize if user is logged in
    if (!this.isLoggedIn()) {
      console.log('[session-manager.js] User not logged in, skipping initialization');
      return;
    }

    console.log('[session-manager.js] Initializing session manager');
    
    // Set initial login time if not set
    if (!localStorage.getItem(SESSION_CONFIG.KEYS.LOGIN_TIME)) {
      this.setLoginTime();
    }

    // Set initial activity time
    this.updateActivity();

    // Start session checking
    this.startSessionCheck();

    // Set up activity listeners
    this.setupActivityListeners();

    console.log('[session-manager.js] Session manager started');
    this.logSessionInfo();
  }

  /**
   * Check if user is logged in
   */
  isLoggedIn() {
    return !!localStorage.getItem(SESSION_CONFIG.KEYS.TOKEN);
  }

  /**
   * Set login time when user logs in
   */
  setLoginTime() {
    const now = Date.now();
    localStorage.setItem(SESSION_CONFIG.KEYS.LOGIN_TIME, now.toString());
    localStorage.setItem(SESSION_CONFIG.KEYS.SESSION_TIMEOUT, getSessionTimeout().toString());
    console.log('[session-manager.js] Login time set:', new Date(now).toLocaleString());
  }

  /**
   * Update last activity timestamp
   */
  updateActivity() {
    const now = Date.now();
    localStorage.setItem(SESSION_CONFIG.KEYS.LAST_ACTIVITY, now.toString());
    console.log('[session-manager.js] Activity updated:', new Date(now).toLocaleString());
  }

  /**
   * Set up activity listeners to track user interaction
   */
  setupActivityListeners() {
    const events = ['mousedown', 'keydown', 'scroll', 'touchstart', 'click'];
    
    events.forEach(event => {
      document.addEventListener(event, () => {
        if (this.activityTimeout) {
          clearTimeout(this.activityTimeout);
        }
        
        // Debounce activity updates (update at most once per 30 seconds)
        this.activityTimeout = setTimeout(() => {
          this.updateActivity();
          // If warning is shown and user is active, dismiss it
          if (this.warningShown) {
            this.dismissWarning();
          }
        }, 30000);
      });
    });

    console.log('[session-manager.js] Activity listeners set up');
  }

  /**
   * Start checking session status periodically
   */
  startSessionCheck() {
    // Clear any existing interval
    if (this.checkInterval) {
      clearInterval(this.checkInterval);
    }

    // Check session immediately
    this.checkSession();

    // Set up periodic check
    this.checkInterval = setInterval(
      this.checkSession,
      SESSION_CONFIG.ACTIVITY_CHECK_INTERVAL
    );

    console.log('[session-manager.js] Session check started');
  }

  /**
   * Stop session checking
   */
  stopSessionCheck() {
    if (this.checkInterval) {
      clearInterval(this.checkInterval);
      this.checkInterval = null;
      console.log('[session-manager.js] Session check stopped');
    }
  }

  /**
   * Check current session status
   */
  checkSession() {
    if (!this.isLoggedIn()) {
      this.stopSessionCheck();
      return;
    }

    const timeRemaining = getTimeRemaining();
    const warningTime = SESSION_CONFIG.WARNING_BEFORE_EXPIRY;

    // Session expired
    if (timeRemaining <= 0) {
      console.log('[session-manager.js] Session expired');
      this.handleSessionExpired();
      return;
    }

    // Show warning if close to expiration
    if (timeRemaining <= warningTime && !this.warningShown) {
      console.log('[session-manager.js] Session expiring soon, showing warning');
      this.showWarning(timeRemaining);
      return;
    }

    // Update warning if already shown
    if (this.warningShown) {
      this.updateWarning(timeRemaining);
    }
  }

  /**
   * Show session expiration warning
   */
  showWarning(timeRemaining) {
    this.warningShown = true;

    // Create warning modal if it doesn't exist
    if (!this.warningModal) {
      this.warningModal = this.createWarningModal();
      document.body.appendChild(this.warningModal);
    }

    // Update time and show
    const timeText = formatTimeRemaining(timeRemaining);
    this.warningModal.querySelector('#session-time-remaining').textContent = timeText;
    this.warningModal.style.display = 'flex';

    console.log('[session-manager.js] Warning shown, time remaining:', timeText);
  }

  /**
   * Update warning modal with current time remaining
   */
  updateWarning(timeRemaining) {
    if (this.warningModal) {
      const timeText = formatTimeRemaining(timeRemaining);
      this.warningModal.querySelector('#session-time-remaining').textContent = timeText;
    }
  }

  /**
   * Dismiss session warning
   */
  dismissWarning() {
    this.warningShown = false;
    if (this.warningModal) {
      this.warningModal.style.display = 'none';
      console.log('[session-manager.js] Warning dismissed');
    }
  }

  /**
   * Create warning modal HTML
   */
  createWarningModal() {
    const modal = document.createElement('div');
    modal.id = 'session-warning-modal';
    modal.style.cssText = `
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.7);
      display: none;
      align-items: center;
      justify-content: center;
      z-index: 10000;
    `;

    modal.innerHTML = `
      <div style="
        background: white;
        padding: 2rem;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0,0,0,0.3);
        max-width: 500px;
        margin: 1rem;
      ">
        <div style="display: flex; align-items: center; margin-bottom: 1rem;">
          <svg style="width: 48px; height: 48px; color: #f59e0b; margin-right: 1rem;" 
               fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
          </svg>
          <h2 style="margin: 0; font-size: 1.5rem; color: #1f2937;">Session Expiring Soon</h2>
        </div>
        <p style="color: #4b5563; margin-bottom: 1rem; font-size: 1rem;">
          Your session will expire in <strong id="session-time-remaining">5 minutes</strong>.
        </p>
        <p style="color: #6b7280; margin-bottom: 1.5rem; font-size: 0.9rem;">
          Click anywhere or interact with the page to extend your session.
        </p>
        <button id="session-warning-dismiss" style="
          background: #3b82f6;
          color: white;
          border: none;
          padding: 0.75rem 1.5rem;
          border-radius: 6px;
          cursor: pointer;
          font-size: 1rem;
          font-weight: 500;
          width: 100%;
        ">
          Continue Working
        </button>
      </div>
    `;

    // Add dismiss button handler
    modal.querySelector('#session-warning-dismiss').addEventListener('click', (e) => {
      e.stopPropagation();
      this.updateActivity();
      this.dismissWarning();
    });

    // Dismiss on background click
    modal.addEventListener('click', (e) => {
      if (e.target === modal) {
        this.updateActivity();
        this.dismissWarning();
      }
    });

    return modal;
  }

  /**
   * Handle session expiration
   */
  handleSessionExpired() {
    console.log('[session-manager.js] Handling session expiration');
    
    // Stop checking
    this.stopSessionCheck();

    // Remove warning
    this.dismissWarning();

    // Store expiration message
    sessionStorage.setItem('sessionExpiredMessage', SESSION_CONFIG.MESSAGES.SESSION_EXPIRED);

    // Logout user
    if (window.authManager) {
      window.authManager.logout();
    } else {
      // Fallback manual logout
      localStorage.removeItem(SESSION_CONFIG.KEYS.TOKEN);
      localStorage.removeItem(SESSION_CONFIG.KEYS.USER);
      localStorage.removeItem(SESSION_CONFIG.KEYS.LOGIN_TIME);
      localStorage.removeItem(SESSION_CONFIG.KEYS.LAST_ACTIVITY);
      window.location.href = '/frontend/index.html';
    }
  }

  /**
   * Extend session (called on login with remember me)
   */
  extendSession(rememberMe = false) {
    localStorage.setItem(SESSION_CONFIG.KEYS.REMEMBER_ME, rememberMe.toString());
    this.setLoginTime();
    console.log('[session-manager.js] Session extended, Remember Me:', rememberMe);
  }

  /**
   * Log current session information
   */
  logSessionInfo() {
    const loginTime = parseInt(localStorage.getItem(SESSION_CONFIG.KEYS.LOGIN_TIME) || '0');
    const rememberMe = localStorage.getItem(SESSION_CONFIG.KEYS.REMEMBER_ME) === 'true';
    const timeRemaining = getTimeRemaining();

    console.log('[session-manager.js] Session Info:');
    console.log('  - Login Time:', new Date(loginTime).toLocaleString());
    console.log('  - Remember Me:', rememberMe);
    console.log('  - Time Remaining:', formatTimeRemaining(timeRemaining));
    console.log('  - Session Timeout:', getSessionTimeout() / 1000 / 60, 'minutes');
  }

  /**
   * Clean up session manager
   */
  cleanup() {
    this.stopSessionCheck();
    if (this.activityTimeout) {
      clearTimeout(this.activityTimeout);
    }
    if (this.warningModal && this.warningModal.parentNode) {
      this.warningModal.parentNode.removeChild(this.warningModal);
    }
    console.log('[session-manager.js] Session manager cleaned up');
  }
}

// Create global instance
const sessionManager = new SessionManager();

// Auto-initialize on DOM load
document.addEventListener('DOMContentLoaded', () => {
  sessionManager.init();
});

// Export for use in other scripts
window.sessionManager = sessionManager;

console.log('[session-manager.js] Session manager loaded');
