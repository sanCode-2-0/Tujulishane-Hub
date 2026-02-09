/**
 * Counter Utilities
 * Provides functions for fetching and formatting data counters
 */

/**
 * Format large numbers with appropriate suffix (1,234 or 1.2K)
 * @param {number} num - The number to format
 * @param {boolean} useAbbreviation - Whether to use K/M abbreviation for large numbers
 * @returns {string} Formatted number string
 */
function formatCount(num, useAbbreviation = false) {
    if (num == null || isNaN(num)) return '0';
    
    const number = parseInt(num);
    
    if (useAbbreviation) {
        if (number >= 1000000) {
            return (number / 1000000).toFixed(1) + 'M';
        } else if (number >= 1000) {
            return (number / 1000).toFixed(1) + 'K';
        }
    }
    
    // Use toLocaleString for comma separation
    return number.toLocaleString();
}

/**
 * Fetch project counts from the API
 * @returns {Promise<Object>} Object containing project counts
 */
async function fetchProjectCounts() {
    try {
        const response = await fetch(`${window.BASE_URL}/api/projects/counts`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error(`Failed to fetch project counts: ${response.status}`);
        }
        
        const result = await response.json();
        return result.data || {};
    } catch (error) {
        console.error('Error fetching project counts:', error);
        return {
            total: 0,
            active: 0,
            pending: 0,
            approved: 0,
            rejected: 0
        };
    }
}

/**
 * Fetch user counts from the API
 * @returns {Promise<Object>} Object containing user counts
 */
async function fetchUserCounts() {
    try {
        const userRole = localStorage.getItem('userRole') || 'PARTNER';
        
        // Use appropriate endpoint based on user role
        let endpoint = '/api/auth/counts';
        if (['SUPER_ADMIN', 'SUPER_ADMIN_APPROVER', 'SUPER_ADMIN_REVIEWER'].includes(userRole)) {
            endpoint = '/api/auth/admin/users-counts'; // Use admin-specific endpoint
        }
        
        const response = await fetch(`${window.BASE_URL}${endpoint}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            console.warn(`Failed to fetch user counts from ${endpoint}: ${response.status}`);
            // Return default values for non-admin users instead of throwing error
            return {
                total: 0,
                partners: 0,
                donors: 0,
                admins: 0,
                active: 0,
                pending: 0
            };
        }
        
        const result = await response.json();
        return result.data || {
            total: 0,
            partners: 0,
            donors: 0,
            admins: 0,
            active: 0,
            pending: 0
        };
    } catch (error) {
        console.error('Error fetching user counts:', error);
        return {
            total: 0,
            partners: 0,
            donors: 0,
            admins: 0,
            active: 0,
            pending: 0
        };
    }
}

/**
 * Fetch project statistics from the API
 * @returns {Promise<Object>} Object containing project statistics
 */
async function fetchProjectStatistics() {
    try {
        const response = await fetch(`${window.BASE_URL}/api/projects/statistics`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error(`Failed to fetch project statistics: ${response.status}`);
        }
        
        const result = await response.json();
        return result.data || {};
    } catch (error) {
        console.error('Error fetching project statistics:', error);
        return {
            totalProjects: 0,
            statusCounts: {},
            countyCounts: {}
        };
    }
}

/**
 * Create a counter card HTML element
 * @param {Object} options - Counter card options
 * @param {string} options.title - Counter title
 * @param {number} options.count - Counter value
 * @param {string} options.icon - Icon class (e.g., 'fas fa-project-diagram')
 * @param {string} options.color - Color theme (e.g., 'blue', 'green', 'yellow', 'red')
 * @param {boolean} options.loading - Whether to show loading state
 * @param {boolean} options.useAbbreviation - Whether to abbreviate large numbers
 * @returns {string} HTML string for counter card
 */
function createCounterCard({
    title = 'Count',
    count = 0,
    icon = 'fas fa-hashtag',
    color = 'blue',
    loading = false,
    useAbbreviation = false
}) {
    const colorClasses = {
        blue: 'bg-blue-500 text-white',
        green: 'bg-green-500 text-white',
        yellow: 'bg-yellow-500 text-white',
        red: 'bg-red-500 text-white',
        purple: 'bg-purple-500 text-white',
        indigo: 'bg-indigo-500 text-white',
        pink: 'bg-pink-500 text-white',
        gray: 'bg-gray-500 text-white'
    };
    
    const bgColor = colorClasses[color] || colorClasses.blue;
    const formattedCount = loading ? '...' : formatCount(count, useAbbreviation);
    
    return `
        <div class="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow duration-200">
            <div class="flex items-center justify-between">
                <div class="flex-1">
                    <p class="text-gray-500 text-sm font-medium uppercase tracking-wider mb-2">${title}</p>
                    <p class="text-3xl font-bold text-gray-800 ${loading ? 'animate-pulse' : ''}">${formattedCount}</p>
                </div>
                <div class="${bgColor} rounded-full p-4 shadow-lg">
                    <i class="${icon} text-2xl"></i>
                </div>
            </div>
        </div>
    `;
}

/**
 * Create a counter grid container
 * @param {Array} counters - Array of counter configurations
 * @returns {string} HTML string for counter grid
 */
function createCounterGrid(counters) {
    const counterCards = counters.map(counter => createCounterCard(counter)).join('');
    
    return `
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            ${counterCards}
        </div>
    `;
}

/**
 * Update counter display with animation
 * @param {HTMLElement} element - The element to update
 * @param {number} newValue - The new counter value
 * @param {boolean} useAbbreviation - Whether to abbreviate large numbers
 */
function updateCounter(element, newValue, useAbbreviation = false) {
    if (!element) return;
    
    const oldValue = parseInt(element.textContent.replace(/[^0-9]/g, '')) || 0;
    const formattedValue = formatCount(newValue, useAbbreviation);
    
    // Add animation class
    element.classList.add('animate-pulse');
    
    // Update value after short delay
    setTimeout(() => {
        element.textContent = formattedValue;
        element.classList.remove('animate-pulse');
        
        // Add pop effect if value increased
        if (newValue > oldValue) {
            element.classList.add('scale-110');
            setTimeout(() => element.classList.remove('scale-110'), 200);
        }
    }, 150);
}

/**
 * Create loading skeleton for counters
 * @param {number} count - Number of skeleton cards to create
 * @returns {string} HTML string for loading skeletons
 */
function createCounterLoadingSkeleton(count = 4) {
    const skeletons = Array(count).fill(null).map(() => `
        <div class="bg-white rounded-lg shadow-md p-6 animate-pulse">
            <div class="flex items-center justify-between">
                <div class="flex-1">
                    <div class="h-4 bg-gray-200 rounded w-24 mb-3"></div>
                    <div class="h-8 bg-gray-300 rounded w-16"></div>
                </div>
                <div class="bg-gray-200 rounded-full p-4 w-16 h-16"></div>
            </div>
        </div>
    `).join('');
    
    return `
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            ${skeletons}
        </div>
    `;
}

// Export functions for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        formatCount,
        fetchProjectCounts,
        fetchUserCounts,
        fetchProjectStatistics,
        createCounterCard,
        createCounterGrid,
        updateCounter,
        createCounterLoadingSkeleton
    };
}
