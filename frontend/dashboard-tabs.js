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
    // Get userRole from localStorage
    const userRole = localStorage.getItem('userRole') || 'PARTNER';
    const tabs = roleTabs[userRole] || roleTabs['PARTNER'];
    // Render sidebar tabs
    const sidebarTabsContainer = document.getElementById('sidebar-tabs');
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
    // Tab switching logic
    const sidebarTabs = document.querySelectorAll('.sidebar-tab');
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
        sidebarTabs.forEach(btn => btn.classList.remove('bg-primary/10', 'text-primary', 'dark:bg-primary/20', 'dark:text-white'));
        const activeBtn = Array.from(sidebarTabs).find(btn => btn.getAttribute('data-tab') === tab);
        if (activeBtn) activeBtn.classList.add('bg-primary/10', 'text-primary', 'dark:bg-primary/20', 'dark:text-white');
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
});
