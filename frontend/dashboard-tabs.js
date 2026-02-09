// dashboard-tabs.js
// Handles sidebar tab rendering and tab switching for dashboard.html

// Tab definitions by role
const allTabs = [
    { key: 'dashboard', label: 'Dashboard', icon: 'dashboard' },
    { key: 'projects', label: 'Projects', icon: 'folder' },
    { key: 'users', label: 'Users', icon: 'group' },
    { key: 'organizations', label: 'Organizations', icon: 'apartment' },
    { key: 'announcements', label: 'Announcements', icon: 'campaign' },
    { key: 'collaboration', label: 'Collaboration Requests', icon: 'handshake' },
    { key: 'pastprojects', label: 'Past Projects', icon: 'history' },
    { key: 'projectdocs', label: 'Project Documents', icon: 'description' },
    { key: 'projectreports', label: 'Project Reports', icon: 'assessment' },
    { key: 'userdocs', label: 'User Documents', icon: 'badge' },
    { key: 'collaborators', label: 'Project Collaborators', icon: 'groups' }
];
const roleTabs = {
    SUPER_ADMIN: allTabs,
    SUPER_ADMIN_APPROVER: allTabs,
    SUPER_ADMIN_REVIEWER: allTabs,
    DONOR: [
        allTabs[0], // Dashboard
        allTabs[1], // Projects
        allTabs[3], // Organizations
        allTabs[6], // Past Projects
        allTabs[7], // Project Documents
        allTabs[8], // Project Reports
        allTabs[10] // Project Collaborators
    ],
    PARTNER: [
        allTabs[0], // Dashboard
        allTabs[1], // Projects
        allTabs[6], // Past Projects
        allTabs[7], // Project Documents
        allTabs[8], // Project Reports
        allTabs[10] // Project Collaborators
    ]
};

document.addEventListener('DOMContentLoaded', function() {
    // Wait a bit for navigation to load
    setTimeout(() => {
        initDashboardTabs();
    }, 100);
    
    // Also try when navigation is loaded
    window.addEventListener('navLoaded', initDashboardTabs);
});

function initDashboardTabs() {
    console.log('Initializing dashboard tabs...');
    
    // Get userRole from localStorage
    const userRole = localStorage.getItem('userRole') || 'PARTNER';
    const tabs = roleTabs[userRole] || roleTabs['PARTNER'];
    
    // Render desktop sidebar tabs
    const sidebarTabsContainer = document.getElementById('sidebar-tabs');
    if (sidebarTabsContainer) {
        sidebarTabsContainer.innerHTML = '';
        tabs.forEach((tab, idx) => {
            const a = document.createElement('a');
            a.className = 'sidebar-tab flex items-center gap-3 px-3 py-2 rounded-lg text-neutral-text dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800';
            a.setAttribute('data-tab', tab.key);
            a.setAttribute('href', '#');
            a.innerHTML = `<span class="material-symbols-outlined">${tab.icon}</span><span class="text-sm font-medium">${tab.label}</span>`;
            if (idx === 0) {
                a.classList.add('bg-primary/10', 'text-primary', 'dark:bg-primary/20', 'dark:text-white');
            }
            sidebarTabsContainer.appendChild(a);
        });
    }
    
// Render mobile navigation tabs (in nav dropdown)
    const mobileSidebarTabsContainer = document.getElementById('mobile-sidebar-tabs');
    const mobileDashboardTabs = document.getElementById('mobile-dashboard-tabs');
    
    // Check if we're on the dashboard page
    const isDashboardPage = window.location.pathname.includes('dashboard.html') || window.location.pathname.endsWith('/dashboard');
    console.log('Is dashboard page:', isDashboardPage, 'Path:', window.location.pathname);
    console.log('Mobile sidebar tabs container:', mobileSidebarTabsContainer);
    console.log('Mobile dashboard tabs container:', mobileDashboardTabs);
    
    if (mobileSidebarTabsContainer && isDashboardPage) {
        console.log('Creating mobile sidebar tabs...');
        mobileSidebarTabsContainer.innerHTML = '';
        tabs.forEach((tab, idx) => {
            const a = document.createElement('a');
            a.className = 'mobile-sidebar-tab flex items-center gap-3 px-3 py-2 rounded-lg text-neutral-text dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800';
            a.setAttribute('data-tab', tab.key);
            a.setAttribute('href', '#');
            a.innerHTML = `<span class="material-symbols-outlined text-lg">${tab.icon}</span><span class="text-sm font-medium">${tab.label}</span>`;
            if (idx === 0) {
                a.classList.add('bg-primary/10', 'text-primary', 'dark:bg-primary/20', 'dark:text-white');
            }
            mobileSidebarTabsContainer.appendChild(a);
        });
        
        // Show mobile dashboard tabs container since we're on dashboard page
        if (mobileDashboardTabs) {
            console.log('Showing mobile dashboard tabs container');
            mobileDashboardTabs.classList.remove('hidden');
        }
    } else {
        console.log('Not creating mobile tabs - either not on dashboard page or container not found');
    }
            mobileSidebarTabsContainer.appendChild(a);
        });
        
        // Show mobile dashboard tabs container since we're on the dashboard page
        const mobileDashboardTabs = document.getElementById('mobile-dashboard-tabs');
        if (mobileDashboardTabs) {
            mobileDashboardTabs.classList.remove('hidden');
        }
    }
    // Tab switching logic
    const sidebarTabs = document.querySelectorAll('.sidebar-tab, .mobile-sidebar-tab');
    const tabSections = {
        dashboard: document.getElementById('tab-dashboard'),
        projects: document.getElementById('tab-projects'),
        users: document.getElementById('tab-users'),
        organizations: document.getElementById('tab-organizations'),
        announcements: document.getElementById('tab-announcements'),
        collaboration: document.getElementById('tab-collaboration'),
        pastprojects: document.getElementById('tab-pastprojects'),
        projectdocs: document.getElementById('tab-projectdocs'),
        projectreports: document.getElementById('tab-projectreports'),
        userdocs: document.getElementById('tab-userdocs'),
        collaborators: document.getElementById('tab-collaborators')
    };
    
    function showTab(tab) {
        Object.values(tabSections).forEach(section => {
            if (section) section.style.display = 'none';
        });
        if (tabSections[tab]) tabSections[tab].style.display = '';
        
        // Update both desktop and mobile tabs
        const allTabs = document.querySelectorAll('.sidebar-tab, .mobile-sidebar-tab');
        allTabs.forEach(btn => btn.classList.remove('bg-primary/10', 'text-primary', 'dark:bg-primary/20', 'dark:text-white'));
        
        const activeTabs = Array.from(allTabs).filter(btn => btn.getAttribute('data-tab') === tab);
        activeTabs.forEach(btn => btn.classList.add('bg-primary/10', 'text-primary', 'dark:bg-primary/20', 'dark:text-white'));
        
        // Close mobile navigation after selection
        setTimeout(() => {
            // Find the Alpine navigation component and close it
            const navComponent = document.querySelector('nav[x-data*="open"]');
            if (navComponent && window.Alpine) {
                window.Alpine.$data(navComponent).open = false;
            }
        }, 100);
    }
    
    sidebarTabs.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const tab = btn.getAttribute('data-tab');
            showTab(tab);
        });
    });
    
    // Show first tab by default
    showTab(tabs[0].key);
}
