# Two-Tier Approval Frontend - Quick Start Guide

## 🚀 What Was Implemented

The two-tier approval system frontend has been successfully integrated into your Tujulishane Hub application!

---

## 📁 New Files

### Main Approval Page

- **`frontend/admin-approvals.html`** - Complete two-tier approval management interface

### Utility Files

- **`frontend/components/approval-workflow-utils.js`** - Full API client and utilities
- **`frontend/styles/workflow-status-helper.js`** - Simple badge and status helpers

### Updated Files

- **`frontend/nav.html`** - Added "Two-Tier Approvals" menu link
- **`frontend/members.html`** - Added role management functions
- **`frontend/all-projects.html`** - Included workflow status helpers

---

## 🎯 Quick Access

### For Reviewers (SUPER_ADMIN_REVIEWER)

1. Log in to your account
2. Click **Admin → Two-Tier Approvals**
3. Review projects in your thematic area
4. Approve or reject with comments

### For Approvers (SUPER_ADMIN_APPROVER)

1. Log in to your account
2. Click **Admin → Two-Tier Approvals**
3. Review projects that passed reviewer stage
4. Grant final approval or reject
5. Manage reviewer assignments

### For Super Admins (SUPER_ADMIN)

- Access to all tabs and functions
- Can perform both reviewer and approver actions
- Can manage all reviewers

---

## 🎓 Six Thematic Areas

| Code       | Full Name                                                  |
| ---------- | ---------------------------------------------------------- |
| **GBV**    | Gender-Based Violence                                      |
| **AYPSRH** | Adolescent and Young People Sexual and Reproductive Health |
| **MNH**    | Maternal and Newborn Health                                |
| **FP**     | Family Planning                                            |
| **CH**     | Child Health                                               |
| **AH**     | Adolescent Health                                          |

---

## 🔧 Setup Instructions

### 1. Create Reviewers (One-Time Setup)

Open `members.html` and use the browser console:

```javascript
// Assign reviewer with thematic area
updateUserRoleWithTheme(userId);
// When prompted:
// - Role: SUPER_ADMIN_REVIEWER
// - Thematic Area: GBV (or AYPSRH, MNH, FP, CH, AH)
```

**Repeat for 5-6 reviewers** (one per thematic area)

### 2. Designate Final Approver (One-Time Setup)

```javascript
// Assign approver role
updateUserRoleWithTheme(userId);
// When prompted:
// - Role: SUPER_ADMIN_APPROVER
// - Thematic Area: (leave blank or cancel)
```

### 3. Start Using!

Navigate to: **Admin → Two-Tier Approvals**

---

## 📊 Workflow Process

```
1. Partner submits project
   ↓
2. Reviewer (matches thematic area) reviews
   ├─ Approve → Sends to Final Approver
   └─ Reject → Returns to Partner for revision
   ↓
3. Final Approver makes decision
   ├─ Approve → Project becomes active
   └─ Reject → Returns to Partner
```

---

## 🎨 Features at a Glance

### Reviewers Can:

- ✅ View projects in their thematic area only
- ✅ Approve projects for final review
- ✅ Reject projects with feedback
- ✅ Add detailed comments

### Approvers Can:

- ✅ View all reviewed projects
- ✅ See reviewer comments and decisions
- ✅ Grant final approval
- ✅ Reject at final stage
- ✅ Manage reviewer assignments
- ✅ Reassign thematic areas

### All Admin Roles Can:

- ✅ See real-time project counts
- ✅ View project details
- ✅ Track workflow status
- ✅ Receive email notifications

---

## 🔐 Access Control

| Page/Feature         | SUPER_ADMIN | REVIEWER       | APPROVER        | PARTNER | DONOR |
| -------------------- | ----------- | -------------- | --------------- | ------- | ----- |
| admin-approvals.html | ✅ Full     | ✅ Review Only | ✅ Approve Only | ❌      | ❌    |
| Review Projects      | ✅          | ✅             | ❌              | ❌      | ❌    |
| Final Approval       | ✅          | ❌             | ✅              | ❌      | ❌    |
| Manage Reviewers     | ✅          | ❌             | ✅              | ❌      | ❌    |

---

## 💡 Common Tasks

### Change Reviewer's Thematic Area

1. Go to `admin-approvals.html`
2. Click **Manage Reviewers** tab
3. Find reviewer → Click **Assign Theme**
4. Select new area → **Assign**

### View Workflow Status

All project pages now show workflow status badges:

- 🟡 Pending Review
- 🔵 Under Review / Awaiting Approval
- 🟢 Approved
- 🔴 Rejected

### Check Reviewer Comments

On project detail pages, reviewer feedback is displayed in a highlighted section when available.

---

## 📱 Navigation

### Desktop Menu

**Admin** (dropdown) → **Two-Tier Approvals**

### Mobile Menu

Tap **Admin** → Scroll to **Two-Tier Approvals**

---

## 🐛 Quick Troubleshooting

**Problem**: Can't access admin-approvals.html  
**Solution**: Ensure user has SUPER_ADMIN, SUPER_ADMIN_REVIEWER, or SUPER_ADMIN_APPROVER role

**Problem**: Reviewer sees no projects  
**Solution**: Check thematic area is assigned and projects exist with matching theme

**Problem**: Approver sees no projects  
**Solution**: Projects must be reviewed by a reviewer first

**Problem**: Functions don't work in members.html  
**Solution**: Open browser console (F12) to run the functions

---

## 📖 Full Documentation

For comprehensive details, see:

- **`FRONTEND_TWO_TIER_IMPLEMENTATION.md`** - Complete frontend implementation guide
- **`TWO_TIER_APPROVAL_DOCUMENTATION.md`** - Backend system documentation
- **`IMPLEMENTATION_TWO_TIER_APPROVAL.md`** - Overall implementation summary

---

## ✅ You're Ready!

The two-tier approval system is now live in your application. Navigate to **Admin → Two-Tier Approvals** to start using it!

**Need Help?** Check the full implementation guide or contact your development team.

---

**Version**: 1.0.0  
**Status**: ✅ Production Ready  
**Date**: November 4, 2025
