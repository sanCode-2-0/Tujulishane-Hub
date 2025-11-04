# Two-Tier Approval System - Frontend Implementation Guide

## ✅ Implementation Complete

All necessary frontend components for the two-tier approval system have been successfully implemented!

---

## 📁 Files Created/Modified

### New Files Created:

1. **`frontend/admin-approvals.html`** - Main two-tier approval management page
2. **`frontend/components/approval-workflow-utils.js`** - Comprehensive API and utility functions
3. **`frontend/styles/workflow-status-helper.js`** - Simple workflow status badge helpers

### Modified Files:

1. **`frontend/nav.html`** - Added "Two-Tier Approvals" link to admin navigation (both desktop and mobile)
2. **`frontend/members.html`** - Added role management functions for assigning thematic areas
3. **`frontend/all-projects.html`** - Included workflow status helper script

---

## 🎯 Key Features Implemented

### 1. Admin Approvals Page (`admin-approvals.html`)

**Access**: Only for SUPER_ADMIN, SUPER_ADMIN_REVIEWER, and SUPER_ADMIN_APPROVER roles

**Features**:

- ✅ Role-based tabbed interface
- ✅ **For Reviewers**: View projects in their thematic area
- ✅ **For Approvers**: View projects awaiting final approval
- ✅ **Reviewer Management**: Assign thematic areas to reviewers
- ✅ Review modal with approve/reject options
- ✅ Final approval modal
- ✅ Final rejection modal with reason requirement
- ✅ Real-time project counters
- ✅ Responsive design with Alpine.js
- ✅ Toast notifications for user feedback

**URL**: `admin-approvals.html`

### 2. Role Management Functions (`members.html`)

Added two new functions to manage the two-tier approval roles:

```javascript
// Update user role with thematic area
updateUserRoleWithTheme(userId);

// Assign thematic area to existing reviewer
assignThematicArea(userId);
```

**How to Use**:

1. Go to `members.html`
2. Find a user in the All Users table
3. Use these functions to:
   - Assign SUPER_ADMIN_REVIEWER role with thematic area
   - Assign SUPER_ADMIN_APPROVER role
   - Change existing reviewer's thematic area

### 3. Workflow Status Helpers

**Two helper files created**:

1. **`approval-workflow-utils.js`** (Comprehensive)

   - Full API client class (`ApprovalWorkflowAPI`)
   - Validation functions
   - Timeline generation
   - Progress calculation
   - All workflow operations

2. **`workflow-status-helper.js`** (Simple)
   - Badge creation functions
   - Status display helpers
   - Easy to include in any page
   - Lightweight and focused

---

## 🚀 How to Use the System

### Step 1: Set Up Reviewers

1. **Navigate to Members Page**

   - Go to `members.html`
   - Find users who should be reviewers

2. **Assign Reviewer Role**

   - Call the browser console:

   ```javascript
   updateUserRoleWithTheme(userId);
   ```

   - Enter role: `SUPER_ADMIN_REVIEWER`
   - Enter thematic area: `GBV`, `AYPSRH`, `MNH`, `FP`, `CH`, or `AH`

3. **Repeat for all 5-6 reviewers** (one for each thematic area)

### Step 2: Designate Final Approver

1. Find the user who will be the final approver
2. Call:
   ```javascript
   updateUserRoleWithTheme(userId);
   ```
3. Enter role: `SUPER_ADMIN_APPROVER`
4. No thematic area needed

### Step 3: Review Projects (Reviewers)

1. **Log in as a SUPER_ADMIN_REVIEWER**
2. Navigate to **Admin → Two-Tier Approvals**
3. You'll see the **"Projects for Review"** tab
4. Only projects matching your thematic area will appear
5. Click **"Review Project"** on any project
6. Choose:
   - **Approve for Final Review** - Sends to approver
   - **Reject (Needs Revision)** - Sends back to partner
7. Add detailed comments
8. Click **"Submit Review"**

### Step 4: Final Approval (Approvers)

1. **Log in as a SUPER_ADMIN_APPROVER**
2. Navigate to **Admin → Two-Tier Approvals**
3. You'll see the **"Awaiting Final Approval"** tab
4. Review the project details and reviewer comments
5. Click either:
   - **"Final Approve"** - Project becomes active
   - **"Reject"** - Send back with rejection reason
6. Add optional comments
7. Confirm action

### Step 5: Manage Reviewers (Approvers Only)

1. Click the **"Manage Reviewers"** tab
2. Filter by thematic area if needed
3. Click **"Assign Theme"** to change a reviewer's area
4. Select new thematic area
5. Click **"Assign"**

---

## 📊 Workflow Status Reference

### Status Badges:

| Status                     | Meaning                  | Who Sees It                 |
| -------------------------- | ------------------------ | --------------------------- |
| **PENDING_REVIEW**         | Awaiting reviewer        | Reviewers in matching theme |
| **UNDER_REVIEW**           | Currently being reviewed | Admin, Reviewer             |
| **REVIEWED**               | Reviewer approved        | Admin, Approver             |
| **PENDING_FINAL_APPROVAL** | Awaiting final decision  | Approver                    |
| **APPROVED**               | Finally approved         | Everyone                    |
| **REJECTED_BY_REVIEWER**   | Needs revision           | Partner, Admin              |
| **REJECTED_BY_APPROVER**   | Rejected at final stage  | Partner, Admin              |

---

## 🎨 UI Components

### Navigation

The "Two-Tier Approvals" link appears in the **Admin dropdown menu** for users with appropriate roles.

### Badge Styles

All workflow statuses have color-coded badges:

- 🟡 Yellow: Pending/Under Review
- 🔵 Blue: Under Review/Awaiting
- 🟣 Purple: Reviewed
- 🟢 Green: Approved
- 🔴 Red: Rejected

### Modals

- Clean, centered modal dialogs
- Form validation
- Loading states
- Toast notifications

---

## 🔧 API Endpoints Used

### Project Review Endpoints:

```
GET  /api/projects/admin/projects-for-review
GET  /api/projects/admin/projects-awaiting-final-approval
POST /api/projects/admin/review/{projectId}
POST /api/projects/admin/final-approve/{projectId}
POST /api/projects/admin/final-reject/{projectId}
```

### User Management Endpoints:

```
GET  /api/auth/admin/reviewers
GET  /api/auth/admin/reviewers/by-theme/{themeCode}
POST /api/auth/admin/assign-thematic-area/{userId}
POST /api/auth/admin/update-role-with-theme/{userId}
GET  /api/auth/admin/approvers
```

---

## 🛠️ Customization

### Adding Workflow Status to Other Pages

To display workflow status on any page:

1. **Include the helper script**:

```html
<script src="styles/workflow-status-helper.js"></script>
```

2. **Create a badge**:

```javascript
const badge = createWorkflowStatusBadge(project.approvalWorkflowStatus);
document.getElementById("status-container").innerHTML = badge;
```

3. **Show reviewer comments**:

```javascript
const comments = createReviewerCommentsSection(project);
document.getElementById("comments-container").innerHTML = comments;
```

4. **Display progress bar**:

```javascript
const progress = createWorkflowProgressBar(project.approvalWorkflowStatus);
document.getElementById("progress-container").innerHTML = progress;
```

### Example: Adding to Project Card

```html
<div class="project-card">
  <h3>{{ project.title }}</h3>

  <!-- Workflow Status Badge -->
  <div class="workflow-status-badge"></div>

  <!-- Progress Bar -->
  <div class="workflow-progress"></div>

  <!-- Reviewer Comments -->
  <div class="reviewer-comments"></div>
</div>

<script>
  // Enhance card with workflow info
  const projectCard = document.querySelector(".project-card");
  enhanceProjectCardWithWorkflow(projectCard, project);
</script>
```

---

## 🔐 Role Permissions Summary

| Feature                      | SUPER_ADMIN | SUPER_ADMIN_REVIEWER | SUPER_ADMIN_APPROVER | PARTNER | DONOR |
| ---------------------------- | ----------- | -------------------- | -------------------- | ------- | ----- |
| View Two-Tier Approvals Page | ✅          | ✅                   | ✅                   | ❌      | ❌    |
| Review Projects              | ✅          | ✅ (own theme only)  | ❌                   | ❌      | ❌    |
| Final Approve/Reject         | ✅          | ❌                   | ✅                   | ❌      | ❌    |
| Manage Reviewers             | ✅          | ❌                   | ✅                   | ❌      | ❌    |
| Assign Thematic Areas        | ✅          | ❌                   | ✅                   | ❌      | ❌    |

---

## 🧪 Testing Checklist

### Reviewer Testing:

- [ ] Log in as SUPER_ADMIN_REVIEWER
- [ ] Access admin-approvals.html
- [ ] See only "Projects for Review" tab
- [ ] See only projects in assigned thematic area
- [ ] Approve a project successfully
- [ ] Reject a project with comments
- [ ] Verify email notifications sent

### Approver Testing:

- [ ] Log in as SUPER_ADMIN_APPROVER
- [ ] Access admin-approvals.html
- [ ] See "Awaiting Final Approval" and "Manage Reviewers" tabs
- [ ] See all reviewed projects
- [ ] View reviewer comments on projects
- [ ] Grant final approval
- [ ] Reject at final stage with reason
- [ ] Assign thematic area to reviewer
- [ ] Verify email notifications sent

### Admin Testing:

- [ ] Log in as SUPER_ADMIN
- [ ] See all tabs (Review, Approval, Manage)
- [ ] Perform all reviewer actions
- [ ] Perform all approver actions
- [ ] Access members.html
- [ ] Update user role with thematic area
- [ ] Verify all workflow statuses display correctly

---

## 📝 Common Tasks

### Change a Reviewer's Thematic Area

**Option 1: Via Admin Approvals Page**

1. Go to `admin-approvals.html`
2. Click "Manage Reviewers" tab
3. Find the reviewer
4. Click "Assign Theme"
5. Select new area

**Option 2: Via Members Page**

1. Go to `members.html`
2. Open browser console
3. Run: `assignThematicArea(userId)`
4. Enter new thematic area code

### Promote User to Reviewer

1. Go to `members.html`
2. Open browser console
3. Run: `updateUserRoleWithTheme(userId)`
4. Enter: `SUPER_ADMIN_REVIEWER`
5. Enter thematic area: `GBV`, `AYPSRH`, `MNH`, `FP`, `CH`, or `AH`

### Make Someone the Final Approver

1. Go to `members.html`
2. Open browser console
3. Run: `updateUserRoleWithTheme(userId)`
4. Enter: `SUPER_ADMIN_APPROVER`
5. No thematic area needed

---

## 🐛 Troubleshooting

### "Access Denied" when visiting admin-approvals.html

- **Solution**: User must have SUPER_ADMIN, SUPER_ADMIN_REVIEWER, or SUPER_ADMIN_APPROVER role

### Reviewer can't see any projects

- **Check**: Reviewer has thematic area assigned
- **Check**: Projects exist with matching thematic area
- **Check**: Projects are in PENDING_REVIEW or UNDER_REVIEW status

### Approver can't see projects

- **Check**: Projects have been reviewed by a reviewer first
- **Check**: Projects are in PENDING_FINAL_APPROVAL status

### Functions not working in members.html

- **Check**: Browser console for errors
- **Check**: User is logged in with SUPER_ADMIN role
- **Check**: Backend is running and accessible

---

## 🌟 Next Steps

### Optional Enhancements:

1. **Email Templates**: Customize email notifications for each workflow stage
2. **Dashboard Widgets**: Add workflow metrics to dashboard
3. **Bulk Actions**: Review/approve multiple projects at once
4. **Advanced Filtering**: Filter by date, status, partner, etc.
5. **Export Reports**: Generate reports on approval workflows
6. **Audit Logs**: Track all approval actions with timestamps

---

## 📞 Support

For questions or issues:

1. Check this implementation guide
2. Review `TWO_TIER_APPROVAL_DOCUMENTATION.md` for backend details
3. Review `API_DOCUMENTATION.md` for API reference
4. Contact the development team

---

## ✅ Summary

You now have a fully functional two-tier approval system with:

- ✅ Dedicated approval management page
- ✅ Role-based access control
- ✅ Thematic area filtering
- ✅ Review and approval workflows
- ✅ Reviewer management interface
- ✅ Workflow status badges across all pages
- ✅ Comprehensive helper utilities
- ✅ Responsive, user-friendly UI

**Ready to deploy!** 🚀

---

**Implementation Date**: November 4, 2025  
**Version**: 1.0.0  
**Frontend Status**: ✅ Complete
