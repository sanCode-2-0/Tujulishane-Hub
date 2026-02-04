/**
 * Session Configuration for Tujulishane Hub
 * Centralized configuration for session timeout, warnings, and persistence
 */

const SESSION_CONFIG = {
  // Session timeout durations (in milliseconds)
  TIMEOUT: {
    SHORT: 60 * 60 * 1000,          // 1 hour
    MEDIUM: 2 * 60 * 60 * 1000,     // 2 hours
    LONG: 8 * 60 * 60 * 1000,       // 8 hours
    FULL_DAY: 24 * 60 * 60 * 1000,  // 24 hours
    REMEMBER_ME: 30 * 24 * 60 * 60 * 1000  // 30 days
  },

  // Active timeout setting (change this to configure session length)
  // Options: 'SHORT', 'MEDIUM', 'LONG', 'FULL_DAY'
  ACTIVE_TIMEOUT: 'SHORT',  // Default: 1 hour

  // Warning before session expiration (in milliseconds)
  WARNING_BEFORE_EXPIRY: 5 * 60 * 1000,  // 5 minutes

  // Session activity tracking
  ACTIVITY_CHECK_INTERVAL: 60 * 1000,  // Check every minute

  // Remember Me configuration
  REMEMBER_ME_ENABLED: true,
  REMEMBER_ME_DURATION: 30 * 24 * 60 * 60 * 1000,  // 30 days

  // Storage keys
  KEYS: {
    TOKEN: 'accessToken',
    USER: 'currentUser',
    REMEMBER_ME: 'rememberMe',
    LOGIN_TIME: 'loginTime',
    LAST_ACTIVITY: 'lastActivity',
    SESSION_TIMEOUT: 'sessionTimeout'
  },

  // Session messages
  MESSAGES: {
    SESSION_EXPIRED: 'Your session has expired. Please log in again.',
    SESSION_WARNING: 'Your session will expire soon. Continue working to stay logged in.',
    LOGGED_OUT: 'You have been logged out successfully.',
    INACTIVITY_LOGOUT: 'You have been logged out due to inactivity.'
  }
};

// Get current timeout duration based on active setting and remember me
function getSessionTimeout() {
  const rememberMe = localStorage.getItem(SESSION_CONFIG.KEYS.REMEMBER_ME) === 'true';
  if (rememberMe && SESSION_CONFIG.REMEMBER_ME_ENABLED) {
    return SESSION_CONFIG.REMEMBER_ME_DURATION;
  }
  return SESSION_CONFIG.TIMEOUT[SESSION_CONFIG.ACTIVE_TIMEOUT];
}

// Get time remaining until session expires
function getTimeRemaining() {
  const loginTime = parseInt(localStorage.getItem(SESSION_CONFIG.KEYS.LOGIN_TIME) || '0');
  const sessionTimeout = parseInt(localStorage.getItem(SESSION_CONFIG.KEYS.SESSION_TIMEOUT) || getSessionTimeout());
  
  if (!loginTime) return 0;
  
  const elapsed = Date.now() - loginTime;
  const remaining = sessionTimeout - elapsed;
  
  return Math.max(0, remaining);
}

// Format time remaining for display
function formatTimeRemaining(milliseconds) {
  const seconds = Math.floor(milliseconds / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  
  if (hours > 0) {
    return `${hours} hour${hours !== 1 ? 's' : ''} ${minutes % 60} minute${minutes % 60 !== 1 ? 's' : ''}`;
  } else if (minutes > 0) {
    return `${minutes} minute${minutes !== 1 ? 's' : ''}`;
  } else {
    return `${seconds} second${seconds !== 1 ? 's' : ''}`;
  }
}

// Export configuration
window.SESSION_CONFIG = SESSION_CONFIG;
window.getSessionTimeout = getSessionTimeout;
window.getTimeRemaining = getTimeRemaining;
window.formatTimeRemaining = formatTimeRemaining;

console.log('[session-config.js] Session configuration loaded');
console.log('[session-config.js] Active timeout:', SESSION_CONFIG.ACTIVE_TIMEOUT, '=', getSessionTimeout() / 1000 / 60, 'minutes');
