# Two-Tier SUPER_ADMIN Approval System Documentation

## Overview

The Tujulishane Hub now implements a sophisticated two-tier approval system for project reviews. This system separates approval responsibilities between specialized **Thematic Area Reviewers** and a **Final Approver**, ensuring thorough and expert evaluation of projects.

---

## User Roles

### 1. SUPER_ADMIN (Legacy)

- **Purpose**: Backward compatibility
- **Permissions**: Full system access including both reviewer and approver capabilities
- **Use Case**: For existing administrators until migration to new roles

### 2. SUPER_ADMIN_APPROVER

- **Purpose**: Final approval authority
- **Count**: Typically 1 user
- **Responsibilities**:
  - Review projects that have been approved by thematic reviewers
  - Make final approval or rejection decisions
  - Manage reviewer assignments
  - Full administrative access
- **Permissions**: Can approve/reject at final stage, manage users and reviewers

### 3. SUPER_ADMIN_REVIEWER

- **Purpose**: Thematic area specialist
- **Count**: 5-6 users (one per thematic area)
- **Responsibilities**:
  - Review projects in their assigned thematic area
  - Approve projects for final review or request revisions
  - Provide expert feedback on project proposals
- **Thematic Areas**:
  - **GBV** - Gender-Based Violence
  - **AYPSRH** - Adolescent and Young People Sexual and Reproductive Health
  - **MNH** - Maternal and Newborn Health
  - **FP** - Family Planning
  - **CH** - Child Health
  - **AH** - Adolescent Health

### 4. DONOR

- Donor organizations/funding agencies
- Can manage linked partner organizations

### 5. PARTNER

- Partner organizations implementing projects
- Can create and manage their own projects

---

## Two-Tier Approval Workflow

### Workflow Statuses

Projects move through the following statuses in the approval workflow:

1. **PENDING_REVIEW** - Project submitted, awaiting review by thematic reviewer
2. **UNDER_REVIEW** - Project is currently being reviewed
3. **REVIEWED** - Thematic reviewer approved, awaiting final approval
4. **PENDING_FINAL_APPROVAL** - Ready for final approver's decision
5. **APPROVED** - Final approval granted, project is active
6. **REJECTED_BY_REVIEWER** - Rejected by thematic reviewer, needs revisions
7. **REJECTED_BY_APPROVER** - Rejected by final approver

### Approval Process Flow

```
Partner submits project
         ↓
  PENDING_REVIEW
         ↓
Thematic Reviewer reviews
         ↓
    ┌─────────┴─────────┐
    ↓                   ↓
 Approved          Rejected
    ↓                   ↓
PENDING_FINAL_    REJECTED_BY_
APPROVAL          REVIEWER
    ↓
Final Approver reviews
    ↓
┌───────┴───────┐
↓               ↓
Approved    Rejected
↓               ↓
APPROVED    REJECTED_BY_
           APPROVER
```

---

## API Endpoints

### Base Path: `/api`

### Project Review Endpoints

#### Get Projects for Review (Reviewer)

**GET** `/api/projects/admin/projects-for-review`

🔒 **Requires Authentication: SUPER_ADMIN_REVIEWER or SUPER_ADMIN**

Get projects awaiting review filtered by the reviewer's assigned thematic area.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Projects for review retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "GBV Prevention Program",
      "partner": "Partner Organization",
      "themes": [
        {
          "code": "GBV",
          "displayName": "Gender Based Violence"
        }
      ],
      "approvalWorkflowStatus": "PENDING_REVIEW",
      "approvalStatus": "PENDING"
    }
  ]
}
```

---

#### Review Project (Reviewer)

**POST** `/api/projects/admin/review/{projectId}`

🔒 **Requires Authentication: SUPER_ADMIN_REVIEWER or SUPER_ADMIN**

Review a project (first step in two-tier approval). Reviewer can approve for final review or reject with comments.

**Path Parameters:**

- `projectId` - ID of the project to review

**Request Body:**

```json
{
  "approved": true,
  "comments": "Project proposal is well-structured and aligns with thematic area objectives. Recommended for final approval."
}
```

**Response (200 OK) - Approved:**

```json
{
  "status": 200,
  "message": "Project reviewed and approved for final approval",
  "data": null
}
```

**Response (200 OK) - Rejected:**

```json
{
  "status": 200,
  "message": "Project review completed - revisions required",
  "data": null
}
```

**Error Response (403 Forbidden) - Wrong Thematic Area:**

```json
{
  "status": 403,
  "message": "This project is not in your assigned thematic area",
  "data": null
}
```

---

#### Get Projects Awaiting Final Approval (Approver)

**GET** `/api/projects/admin/projects-awaiting-final-approval`

🔒 **Requires Authentication: SUPER_ADMIN_APPROVER or SUPER_ADMIN**

Get all projects that have been reviewed and are awaiting final approval.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Projects awaiting final approval retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "GBV Prevention Program",
      "partner": "Partner Organization",
      "reviewedBy": 5,
      "reviewedAt": "2025-11-04T10:30:00",
      "reviewerComments": "Well-structured proposal...",
      "approvalWorkflowStatus": "PENDING_FINAL_APPROVAL"
    }
  ]
}
```

---

#### Final Approve Project (Approver)

**POST** `/api/projects/admin/final-approve/{projectId}`

🔒 **Requires Authentication: SUPER_ADMIN_APPROVER or SUPER_ADMIN**

Grant final approval to a reviewed project (second step in two-tier approval).

**Path Parameters:**

- `projectId` - ID of the project to approve

**Request Body (Optional):**

```json
{
  "comments": "Final approval granted. Project aligns with MOH strategic objectives."
}
```

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Project finally approved successfully",
  "data": null
}
```

**Error Response (400 Bad Request) - Not Reviewed:**

```json
{
  "status": 400,
  "message": "Project must be reviewed before final approval",
  "data": null
}
```

---

#### Final Reject Project (Approver)

**POST** `/api/projects/admin/final-reject/{projectId}`

🔒 **Requires Authentication: SUPER_ADMIN_APPROVER or SUPER_ADMIN**

Reject a project at the final approval stage.

**Path Parameters:**

- `projectId` - ID of the project to reject

**Request Body:**

```json
{
  "reason": "Project does not align with current national health priorities. Please revise and resubmit."
}
```

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Project rejected at final approval stage",
  "data": null
}
```

---

### Reviewer Management Endpoints

#### Assign Thematic Area to Reviewer

**POST** `/api/auth/admin/assign-thematic-area/{userId}`

🔒 **Requires Authentication: SUPER_ADMIN_APPROVER or SUPER_ADMIN**

Assign a thematic area to a reviewer.

**Path Parameters:**

- `userId` - ID of the user (must be SUPER_ADMIN_REVIEWER)

**Request Body:**

```json
{
  "thematicArea": "GBV"
}
```

**Valid Thematic Area Codes:**

- `GBV` - Gender-Based Violence
- `AYPSRH` - Adolescent and Young People Sexual and Reproductive Health
- `MNH` - Maternal and Newborn Health
- `FP` - Family Planning
- `CH` - Child Health
- `AH` - Adolescent Health

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Thematic area assigned successfully",
  "data": null
}
```

---

#### Update User Role with Thematic Area

**POST** `/api/auth/admin/update-role-with-theme/{userId}`

🔒 **Requires Authentication: SUPER_ADMIN_APPROVER or SUPER_ADMIN**

Update a user's role and optionally assign a thematic area (for creating reviewers).

**Path Parameters:**

- `userId` - ID of the user

**Request Body:**

```json
{
  "role": "SUPER_ADMIN_REVIEWER",
  "thematicArea": "MNH"
}
```

**Valid Roles:**

- `SUPER_ADMIN`
- `SUPER_ADMIN_APPROVER`
- `SUPER_ADMIN_REVIEWER`
- `DONOR`
- `PARTNER`

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "User role updated successfully",
  "data": null
}
```

---

#### Get All Reviewers

**GET** `/api/auth/admin/reviewers`

🔒 **Requires Authentication: SUPER_ADMIN_APPROVER or SUPER_ADMIN**

Get all users with the SUPER_ADMIN_REVIEWER role.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Reviewers retrieved successfully",
  "data": [
    {
      "id": 5,
      "name": "Dr. Jane Smith",
      "email": "jane.smith@moh.go.ke",
      "role": "SUPER_ADMIN_REVIEWER",
      "thematicArea": "GBV"
    },
    {
      "id": 6,
      "name": "Dr. John Kamau",
      "email": "john.kamau@moh.go.ke",
      "role": "SUPER_ADMIN_REVIEWER",
      "thematicArea": "MNH"
    }
  ]
}
```

---

#### Get Reviewers by Thematic Area

**GET** `/api/auth/admin/reviewers/by-theme/{themeCode}`

🔒 **Requires Authentication: SUPER_ADMIN_APPROVER or SUPER_ADMIN**

Get all reviewers assigned to a specific thematic area.

**Path Parameters:**

- `themeCode` - Thematic area code (e.g., GBV, MNH, FP)

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Reviewers for thematic area retrieved successfully",
  "data": [
    {
      "id": 5,
      "name": "Dr. Jane Smith",
      "email": "jane.smith@moh.go.ke",
      "role": "SUPER_ADMIN_REVIEWER",
      "thematicArea": "GBV"
    }
  ]
}
```

---

#### Get All Approvers

**GET** `/api/auth/admin/approvers`

🔒 **Requires Authentication: SUPER_ADMIN_APPROVER or SUPER_ADMIN**

Get all users with approver privileges (SUPER_ADMIN_APPROVER and legacy SUPER_ADMIN).

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Approvers retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "MOH Administrator",
      "email": "admin@moh.go.ke",
      "role": "SUPER_ADMIN_APPROVER"
    }
  ]
}
```

---

## Data Models

### User

```typescript
interface User {
  id: number;
  name: string;
  email: string;
  role:
    | "SUPER_ADMIN"
    | "SUPER_ADMIN_APPROVER"
    | "SUPER_ADMIN_REVIEWER"
    | "DONOR"
    | "PARTNER";
  thematicArea?: "GBV" | "AYPSRH" | "MNH" | "FP" | "CH" | "AH"; // Only for SUPER_ADMIN_REVIEWER
  approvalStatus: "PENDING" | "APPROVED" | "REJECTED";
  status: "INACTIVE" | "ACTIVE";
  emailVerified: boolean;
  createdAt: string;
  updatedAt: string;
}
```

### Project (Updated Fields)

```typescript
interface Project {
  // ... existing fields ...

  // Two-tier approval workflow fields
  approvalWorkflowStatus:
    | "PENDING_REVIEW"
    | "UNDER_REVIEW"
    | "REVIEWED"
    | "PENDING_FINAL_APPROVAL"
    | "APPROVED"
    | "REJECTED_BY_REVIEWER"
    | "REJECTED_BY_APPROVER";

  reviewedBy?: number; // User ID of thematic reviewer
  reviewedAt?: string; // ISO 8601 timestamp
  reviewerComments?: string; // Comments from reviewer and approver

  // Legacy fields (still maintained)
  approvalStatus: "PENDING" | "APPROVED" | "REJECTED" | "SUBMITTED";
  approvedBy?: number;
  approvedAt?: string;
  rejectionReason?: string;
}
```

---

## Migration Guide

### For Existing Systems

1. **Run Database Migration**

   ```bash
   psql -U your_user -d tujulishane_hub -f backend/database_migration_two_tier_approval.sql
   ```

2. **Create Reviewer Accounts**

   - Create 5-6 user accounts (one per thematic area)
   - Assign role `SUPER_ADMIN_REVIEWER` using the update endpoint
   - Assign thematic areas using the assign endpoint

3. **Designate Final Approver**

   - Update one existing SUPER_ADMIN to SUPER_ADMIN_APPROVER
   - Or keep as SUPER_ADMIN for backward compatibility

4. **Update Existing Projects**
   - The migration script automatically sets `approval_workflow_status`
   - Existing approved projects are marked as `APPROVED`
   - Existing pending projects are marked as `PENDING_REVIEW`

### Rollback

If you need to revert the changes:

```bash
psql -U your_user -d tujulishane_hub -f backend/database_migration_rollback.sql
```

---

## Best Practices

### For Reviewers

1. **Review projects in your thematic area promptly**
2. **Provide clear, actionable feedback** in comments
3. **Only approve projects** that meet thematic standards
4. **Communicate with partners** if clarification is needed

### For Final Approver

1. **Review the thematic reviewer's comments** before making decisions
2. **Ensure alignment** with MOH strategic objectives
3. **Provide final comments** to guide partners
4. **Monitor reviewer performance** and workload distribution

### For System Administrators

1. **Assign only one thematic area per reviewer** for clear accountability
2. **Monitor approval pipeline** to prevent bottlenecks
3. **Ensure each thematic area has an assigned reviewer**
4. **Regularly review workflow metrics**

---

## Workflow Automation & Notifications

The system automatically sends email notifications at key workflow stages:

1. **Project Submitted** → Partner receives confirmation
2. **Review Completed** → Partner receives reviewer feedback
3. **Final Approval** → Partner receives approval notification
4. **Rejection** → Partner receives rejection with reason and guidance

---

## Security Considerations

- **Role-based access control** ensures reviewers can only see projects in their thematic area
- **Validation checks** prevent unauthorized approval actions
- **Audit trail** maintained through reviewedBy, reviewedAt, approvedBy, and approvedAt fields
- **JWT authentication** required for all approval endpoints

---

## Frequently Asked Questions

**Q: Can a reviewer approve projects outside their thematic area?**  
A: No, the system validates that projects match the reviewer's assigned thematic area.

**Q: What happens to existing SUPER_ADMIN users?**  
A: They retain full permissions including both reviewer and approver capabilities for backward compatibility.

**Q: Can a project have multiple themes?**  
A: Yes, projects can have multiple themes. Any reviewer whose thematic area matches one of the project's themes can review it.

**Q: Can the final approver reject a project that was approved by a reviewer?**  
A: Yes, the final approver has full authority to approve or reject any project, regardless of reviewer recommendation.

**Q: How do we handle reviewer unavailability?**  
A: Legacy SUPER_ADMIN users can review any project. Alternatively, temporarily assign another reviewer to cover the thematic area.

---

## Support

For technical issues or questions about the two-tier approval system, contact the development team or refer to the main API documentation.
