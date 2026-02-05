/**
 * Network Debugging Utility
 * Provides detailed logging for network requests to help debug browser vs curl differences
 */

class NetworkDebugger {
    constructor() {
        this.enabled = true;
        this.requestHistory = [];
        this.maxHistorySize = 50;
    }

    /**
     * Log detailed request information
     * @param {string} requestType - Type of request (e.g., 'login', 'otp-verify', 'api-call')
     * @param {Object} config - Request configuration
     * @param {Response} response - Response object
     * @param {number} duration - Request duration in milliseconds
     */
    logRequest(requestType, config, response, duration) {
        if (!this.enabled) return;

        const logEntry = {
            timestamp: new Date().toISOString(),
            requestType,
            request: {
                url: config.url,
                method: config.method,
                headers: config.headers,
                bodySize: config.body ? config.body.length : 0,
                bodyPreview: config.body ? this.sanitizeBody(config.body) : null
            },
            response: {
                status: response.status,
                ok: response.ok,
                statusText: response.statusText,
                headers: this.getResponseHeaders(response),
                duration: `${duration.toFixed(2)}ms`
            },
            environment: this.getEnvironmentInfo()
        };

        console.log(`[NetworkDebugger] ${requestType} request:`, logEntry);
        
        // Store in history for later analysis
        this.requestHistory.push(logEntry);
        if (this.requestHistory.length > this.maxHistorySize) {
            this.requestHistory.shift();
        }
    }

    /**
     * Log error information
     * @param {string} requestType - Type of request
     * @param {Object} config - Request configuration
     * @param {Error} error - Error object
     * @param {number} duration - Request duration before error
     */
    logError(requestType, config, error, duration) {
        if (!this.enabled) return;

        const logEntry = {
            timestamp: new Date().toISOString(),
            requestType,
            request: {
                url: config.url,
                method: config.method,
                headers: config.headers,
                bodySize: config.body ? config.body.length : 0
            },
            error: {
                name: error.name,
                message: error.message,
                stack: error.stack
            },
            duration: `${duration.toFixed(2)}ms`,
            environment: this.getEnvironmentInfo()
        };

        console.error(`[NetworkDebugger] ${requestType} error:`, logEntry);
    }

    /**
     * Get environment information for debugging
     */
    getEnvironmentInfo() {
        return {
            userAgent: navigator.userAgent,
            language: navigator.language,
            platform: navigator.platform,
            cookieEnabled: navigator.cookieEnabled,
            onLine: navigator.onLine,
            doNotTrack: navigator.doNotTrack,
            timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
            currentUrl: window.location.href,
            origin: window.location.origin,
            referrer: document.referrer,
            screen: `${screen.width}x${screen.height}`,
            viewport: `${window.innerWidth}x${window.innerHeight}`,
            localStorageAvailable: this.isStorageAvailable(localStorage),
            sessionStorageAvailable: this.isStorageAvailable(sessionStorage),
            connection: this.getConnectionInfo()
        };
    }

    /**
     * Get response headers as an object
     */
    getResponseHeaders(response) {
        const headers = {};
        response.headers.forEach((value, key) => {
            headers[key] = value;
        });
        return headers;
    }

    /**
     * Sanitize request body for logging (remove sensitive data)
     */
    sanitizeBody(body) {
        try {
            const parsed = JSON.parse(body);
            const sanitized = { ...parsed };
            
            // Redact sensitive fields
            if (sanitized.password) sanitized.password = '[REDACTED]';
            if (sanitized.otp) sanitized.otp = '[REDACTED]';
            if (sanitized.token) sanitized.token = '[REDACTED]';
            if (sanitized.accessToken) sanitized.accessToken = '[REDACTED]';
            
            return sanitized;
        } catch (e) {
            // If not JSON, return first 100 characters
            return body.substring(0, 100) + (body.length > 100 ? '...' : '');
        }
    }

    /**
     * Check if storage is available
     */
    isStorageAvailable(storage) {
        try {
            const test = '__test__';
            storage.setItem(test, test);
            storage.removeItem(test);
            return true;
        } catch (e) {
            return false;
        }
    }

    /**
     * Get connection information if available
     */
    getConnectionInfo() {
        if (navigator.connection) {
            return {
                effectiveType: navigator.connection.effectiveType,
                downlink: navigator.connection.downlink,
                rtt: navigator.connection.rtt
            };
        }
        return null;
    }

    /**
     * Generate a comparison report for browser vs debugging
     */
    generateComparisonReport() {
        const report = {
            timestamp: new Date().toISOString(),
            totalRequests: this.requestHistory.length,
            requests: this.requestHistory,
            environment: this.getEnvironmentInfo(),
            summary: {
                successfulRequests: this.requestHistory.filter(r => r.response.ok).length,
                failedRequests: this.requestHistory.filter(r => !r.response.ok).length,
                averageDuration: this.getAverageDuration(),
                mostCommonErrors: this.getMostCommonErrors()
            }
        };

        console.log('[NetworkDebugger] Comparison Report:', report);
        return report;
    }

    /**
     * Calculate average request duration
     */
    getAverageDuration() {
        if (this.requestHistory.length === 0) return 0;
        
        const total = this.requestHistory.reduce((sum, entry) => {
            const duration = parseFloat(entry.response.duration);
            return sum + duration;
        }, 0);
        
        return (total / this.requestHistory.length).toFixed(2) + 'ms';
    }

    /**
     * Get most common errors
     */
    getMostCommonErrors() {
        const errors = {};
        
        this.requestHistory.forEach(entry => {
            if (!entry.response.ok) {
                const key = `${entry.response.status} - ${entry.response.statusText}`;
                errors[key] = (errors[key] || 0) + 1;
            }
        });
        
        return errors;
    }

    /**
     * Clear request history
     */
    clearHistory() {
        this.requestHistory = [];
        console.log('[NetworkDebugger] Request history cleared');
    }

    /**
     * Enable/disable debugging
     * @param {boolean} enabled 
     */
    setEnabled(enabled) {
        this.enabled = enabled;
        console.log(`[NetworkDebugger] ${enabled ? 'Enabled' : 'Disabled'}`);
    }
}

// Create global instance
window.networkDebugger = new NetworkDebugger();

// Log page load information
console.log('[NetworkDebugger] Page loaded with debugging enabled');

// Expose debugging functions to console for easy access
window.debugNetwork = {
    getReport: () => window.networkDebugger.generateComparisonReport(),
    getHistory: () => window.networkDebugger.requestHistory,
    clearHistory: () => window.networkDebugger.clearHistory(),
    getEnvironment: () => window.networkDebugger.getEnvironmentInfo(),
    disable: () => window.networkDebugger.setEnabled(false),
    enable: () => window.networkDebugger.setEnabled(true),
    help: () => {
        console.log(`
Network Debugger Commands:
- debugNetwork.getReport() - Generate full comparison report
- debugNetwork.getHistory() - Get raw request history
- debugNetwork.getEnvironment() - Get current environment info
- debugNetwork.clearHistory() - Clear request history
- debugNetwork.disable() - Disable debugging
- debugNetwork.enable() - Enable debugging
- debugNetwork.help() - Show this help message

Example usage:
debugNetwork.getReport() // See detailed comparison between requests
debugNetwork.getHistory() // See all logged requests
        `);
    }
};

console.log('[NetworkDebugger] Debug commands available via debugNetwork object. Run debugNetwork.help() for usage.');