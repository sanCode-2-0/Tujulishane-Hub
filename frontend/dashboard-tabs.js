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
    console.log('Available tabs:', tabs);
    
    // Always try to create mobile tabs if on dashboard page, even if container check fails initially
    if (isDashboardPage) {
        console.log('On dashboard page, attempting to create mobile sidebar tabs...');
        
        // Try to get the container multiple times in case of timing issues
        let attempts = 0;
        const maxAttempts = 10;
        
        function tryCreateMobileTabs() {
            attempts++;
            const container = document.getElementById('mobile-sidebar-tabs');
            const parentContainer = document.getElementById('mobile-dashboard-tabs');
            
            console.log(`Attempt ${attempts}: Container found:`, !!container, 'Parent found:', !!parentContainer);
            
            if (container) {
                console.log('Creating mobile sidebar tabs...');
                container.innerHTML = '';
                tabs.forEach((tab, idx) => {
                    const a = document.createElement('a');
                    a.className = 'mobile-sidebar-tab flex items-center gap-3 px-3 py-2 rounded-lg text-neutral-text dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800';
                    a.setAttribute('data-tab', tab.key);
                    a.setAttribute('href', '#');
                    a.innerHTML = `<span class="material-symbols-outlined text-lg">${tab.icon}</span><span class="text-sm font-medium">${tab.label}</span>`;
                    if (idx === 0) {
                        a.classList.add('bg-primary/10', 'text-primary', 'dark:bg-primary/20', 'dark:text-white');
                    }
                    container.appendChild(a);
                    console.log(`Added tab: ${tab.label}`);
                });
                
                // Show mobile dashboard tabs container since we're on dashboard page
                if (parentContainer) {
                    console.log('Showing mobile dashboard tabs container');
                    parentContainer.classList.remove('hidden');
                }
                
                // Add click handlers for mobile tabs
                const mobileTabs = container.querySelectorAll('.mobile-sidebar-tab');
                mobileTabs.forEach(btn => {
                    btn.addEventListener('click', function(e) {
                        e.preventDefault();
                        const tab = btn.getAttribute('data-tab');
                        console.log('Mobile tab clicked:', tab);
                        showTab(tab);
                    });
                });
                
                return; // Success, exit the function
            }
            
            if (attempts < maxAttempts) {
                setTimeout(tryCreateMobileTabs, 100);
            } else {
                console.error('Could not find mobile-sidebar-tabs container after', maxAttempts, 'attempts');
            }
        }
        
        tryCreateMobileTabs();
    } else {
        console.log('Not creating mobile tabs - not on dashboard page');
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
            // Find Alpine navigation component and close it
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