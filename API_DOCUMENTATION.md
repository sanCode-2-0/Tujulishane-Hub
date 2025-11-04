# Tujulishane Hub API Documentation

---

## 🆕 Two-Tier SUPER_ADMIN Approval System

The system now implements a **two-tier approval workflow** with specialized thematic area reviewers and a final approver. This ensures thorough, expert evaluation of all projects.

**📖 For complete documentation on the two-tier approval system, see [TWO_TIER_APPROVAL_DOCUMENTATION.md](./TWO_TIER_APPROVAL_DOCUMENTATION.md)**

### Quick Overview:

- **SUPER_ADMIN_REVIEWER**: 5-6 thematic area specialists who review projects in their domain (GBV, AYPSRH, MNH, FP, CH, AH)
- **SUPER_ADMIN_APPROVER**: Single final approver who grants ultimate approval
- **Two-step process**: Thematic review → Final approval

---

## How to Create a SUPER_ADMIN User (Initial System Setup)

To create the first SUPER_ADMIN user, use the special bootstrap endpoint. This should only be done once during initial setup.

**Step 1:** Set the `BOOTSTRAP_SECRET` environment variable on your server to a secure value (e.g., `xN8y0stcAh0meLwCgXeeErTmK9X5PMO4383292903`).

**Step 2:** Run the following `curl` command to create the SUPER_ADMIN user:

```bash
curl -X POST https://api-tujulishane-hub.onrender.com/api/auth/bootstrap/super-admin \
    -H "Content-Type: application/json" \
    -d '{
        "email": "admin@moh.gov.ke",
        "name": "MOH System Administrator",
        "secretKey": "xN8y0stcAh0meLwCgXeeErTmK9X5PMO4"
    }'
```

**Example Response (200 OK):**

```json
{
  "status": 200,
  "message": "SUPER_ADMIN user created successfully. You can now login with OTP.",
  "data": null
}
```

**Notes:**

- This endpoint can only be used once.
- The `secretKey` must match the `BOOTSTRAP_SECRET` environment variable.
- After successful creation, log in using the OTP sent to the provided email.

---

**API Version**: 1.0  
**Authentication**: JWT Bearer Token  
**Response Format**: JSON

## Table of Contents

1. [Two-Tier SUPER_ADMIN Approval System](#-two-tier-super_admin-approval-system) ⭐
2. [Authentication](#authentication)
3. [User Management](#user-management)
4. [Organization Management](#organization-management)
5. [Project Management](#project-management)
6. [Project Reports](#project-reports)
7. [Data Models](#data-models)
8. [Error Handling](#error-handling)

---

## Authentication

### Base Path: `/api/auth`

All authentication endpoints use OTP (One-Time Password) verification instead of traditional passwords.

#### User Registration

**POST** `/api/auth/register`

Register a new user in the system.

**Request Body:**

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "organizationId": 1 // Optional
}
```

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Registration received. Check your email for the verification OTP.",
  "data": null
}
```

#### Email Verification

**POST** `/api/auth/verify`

Verify email address using OTP sent during registration.

**Request Body:**

```json
{
  "email": "john.doe@example.com",
  "otp": "123456"
}
```

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "User verified successfully.",
  "data": null
}
```

#### Login (Request OTP)

**POST** `/api/auth/login`

Request login OTP for an existing verified user.

**Request Body:**

```json
{
  "email": "john.doe@example.com"
}
```

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Login OTP sent to your email.",
  "data": null
}
```

#### Login Verification

**POST** `/api/auth/verify/login`

Verify login OTP and receive access token.

**Request Body:**

```json
{
  "email": "john.doe@example.com",
  "otp": "123456"
}
```

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Login successful.",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  }
}
```

#### Resend Login OTP

**POST** `/api/auth/send-login-otp`

Resend login OTP to user's email.

**Request Body:**

```json
{
  "email": "john.doe@example.com"
}
```

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Login OTP sent to your email.",
  "data": null
}
```

#### Get Current User Profile

**GET** `/api/auth/profile`

🔒 **Requires Authentication**

Get the current authenticated user's profile information.

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "User profile retrieved successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "role": "PARTNER",
    "approvalStatus": "APPROVED",
    "status": "ACTIVE",
    "emailVerified": true,
    "organization": {
      "id": 1,
      "name": "Health Partners Kenya"
    },
    "createdAt": "2024-01-15T10:30:00",
    "lastLogin": "2024-01-20T09:15:00"
  }
}
```

#### Bootstrap Super Admin (Initial Setup Only)

**POST** `/api/auth/bootstrap/super-admin`

🚨 **IMPORTANT: Use only once for initial system setup**

Create the first SUPER_ADMIN user in the system. This endpoint can only be used once and requires a secret key.

**Request Body:**

```json
{
  "email": "admin@moh.gov.ke",
  "name": "MOH Administrator",
  "secretKey": "your-bootstrap-secret-key"
}
```

**Environment Variable Required:**
Set `BOOTSTRAP_SECRET` environment variable to a secure secret key before using this endpoint.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "SUPER_ADMIN user created successfully. You can now login with OTP.",
  "data": null
}
```

**Error Responses:**

If SUPER_ADMIN already exists:

```json
{
  "status": 409,
  "message": "SUPER_ADMIN users already exist. This endpoint can only be used once.",
  "data": null
}
```

If invalid secret key:

```json
{
  "status": 403,
  "message": "Invalid bootstrap secret key",
  "data": null
}
```

**Headers:**

```
Authorization: Bearer <your-jwt-token>
```

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "User profile retrieved successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "role": "PARTNER",
    "approvalStatus": "APPROVED",
    "status": "ACTIVE",
    "emailVerified": true,
    "organization": {
      "id": 1,
      "name": "Health Partners Kenya"
    },
    "createdAt": "2024-01-15T10:30:00",
    "lastLogin": "2024-01-20T09:15:00"
  }
}
```

---

## User Management

### Admin Endpoints

#### Get Pending Users

**GET** `/api/auth/admin/pending-users`

🔒 **Requires Authentication: ADMIN or SUPER_ADMIN**

Get all users awaiting approval.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Pending users retrieved successfully",
  "data": [
    {
      "id": 2,
      "name": "Jane Smith",
      "email": "jane.smith@example.com",
      "role": "PARTNER",
      "approvalStatus": "PENDING",
      "organization": {
        "id": 2,
        "name": "Medical NGO"
      },
      "createdAt": "2024-01-20T14:30:00"
    }
  ]
}
```

#### Approve User

**POST** `/api/auth/admin/approve-user/{userId}`

🔒 **Requires Authentication: ADMIN or SUPER_ADMIN**

Approve a pending user.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "User approved successfully",
  "data": null
}
```

#### Reject User

**POST** `/api/auth/admin/reject-user/{userId}`

🔒 **Requires Authentication: ADMIN or SUPER_ADMIN**

Reject a pending user.

**Request Body:**

```json
{
  "reason": "Incomplete documentation"
}
```

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "User rejected successfully",
  "data": null
}
```

#### Get Users by Status

**GET** `/api/auth/admin/users/status/{status}`

🔒 **Requires Authentication: ADMIN or SUPER_ADMIN**

Get users filtered by approval status.

**Path Parameters:**

- `status`: PENDING, APPROVED, REJECTED

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Users retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "approvalStatus": "APPROVED",
      "role": "PARTNER"
    }
  ]
}
```

#### Get All Users

**GET** `/api/auth/admin/users`

🔒 **Requires Authentication: ADMIN or SUPER_ADMIN**

Get all users in the system.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "All users retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "role": "PARTNER",
      "approvalStatus": "APPROVED",
      "status": "ACTIVE"
    }
  ]
}
```

---

## Organization Management

### Base Path: `/api/organizations`

#### Create Organization

**POST** `/api/organizations`

🔒 **Requires Authentication**

Create a new organization.

**Request Body:**

```json
{
  "name": "Health Partners Kenya",
  "organizationType": "NGO",
  "description": "A non-profit organization focused on improving healthcare access in rural Kenya.",
  "contactEmail": "info@healthpartners.ke",
  "contactPhone": "+254-700-123456",
  "address": "123 Health Street, Nairobi, Kenya",
  "websiteUrl": "https://healthpartners.ke",
  "registrationNumber": "NGO/2020/001"
}
```

**Organization Types:**

- `NGO` - Non-Governmental Organization
- `GOVERNMENT_AGENCY` - Government Agency
- `PRIVATE_COMPANY` - Private Company/Corporation
- `INTERNATIONAL_ORG` - International Organization
- `FOUNDATION` - Foundation/Trust
- `COMMUNITY_GROUP` - Community-Based Organization
- `ACADEMIC_INSTITUTION` - University/Research Institution
- `HEALTHCARE_PROVIDER` - Hospital/Clinic
- `DONOR_AGENCY` - Donor/Funding Agency
- `OTHER` - Other type

**Response (201 Created):**

```json
{
  "status": 201,
  "message": "Organization created successfully",
  "data": {
    "id": 1,
    "name": "Health Partners Kenya",
    "organizationType": "NGO",
    "description": "A non-profit organization...",
    "contactEmail": "info@healthpartners.ke",
    "approvalStatus": "PENDING",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

#### Get All Organizations

**GET** `/api/organizations`

Get paginated list of organizations.

**Query Parameters:**

- `page` (default: 0) - Page number
- `size` (default: 10) - Page size
- `sortBy` (default: "id") - Sort field
- `sortDir` (default: "desc") - Sort direction (asc/desc)

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Organizations retrieved successfully",
  "data": {
    "organizations": [
      {
        "id": 1,
        "name": "Health Partners Kenya",
        "organizationType": "NGO",
        "approvalStatus": "APPROVED",
        "contactEmail": "info@healthpartners.ke"
      }
    ],
    "currentPage": 0,
    "totalItems": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

#### Get Organization by ID

**GET** `/api/organizations/{id}`

Get specific organization details.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Organization retrieved successfully",
  "data": {
    "id": 1,
    "name": "Health Partners Kenya",
    "organizationType": "NGO",
    "description": "A non-profit organization...",
    "contactEmail": "info@healthpartners.ke",
    "contactPhone": "+254-700-123456",
    "address": "123 Health Street, Nairobi, Kenya",
    "websiteUrl": "https://healthpartners.ke",
    "registrationNumber": "NGO/2020/001",
    "approvalStatus": "APPROVED",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

#### Search Organizations

**GET** `/api/organizations/search`

Search organizations by name.

**Query Parameters:**

- `keyword` (required) - Search term

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Organizations search completed",
  "data": [
    {
      "id": 1,
      "name": "Health Partners Kenya",
      "organizationType": "NGO",
      "approvalStatus": "APPROVED"
    }
  ]
}
```

#### Get Approved Organizations

**GET** `/api/organizations/approved`

Get all approved organizations.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Approved organizations retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Health Partners Kenya",
      "organizationType": "NGO",
      "approvalStatus": "APPROVED"
    }
  ]
}
```

#### Update Organization

**PUT** `/api/organizations/{id}`

🔒 **Requires Authentication**

Update organization details.

**Request Body:** Same as create organization

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Organization updated successfully",
  "data": {
    "id": 1,
    "name": "Updated Organization Name",
    "organizationType": "NGO"
    // ... other fields
  }
}
```

### Admin Endpoints

#### Get Pending Organizations

**GET** `/api/organizations/admin/pending`

🔒 **Requires Authentication: SUPER_ADMIN**

Get all organizations awaiting approval.

#### Approve Organization

**POST** `/api/organizations/admin/approve/{id}`

🔒 **Requires Authentication: SUPER_ADMIN**

Approve a pending organization.

#### Reject Organization

**POST** `/api/organizations/admin/reject/{id}`

🔒 **Requires Authentication: SUPER_ADMIN**

Reject a pending organization.

**Request Body:**

```json
{
  "reason": "Incomplete documentation"
}
```

#### Get Organization Statistics

**GET** `/api/organizations/admin/stats`

🔒 **Requires Authentication: SUPER_ADMIN**

Get organization statistics.

#### Delete Organization

**DELETE** `/api/organizations/admin/{id}`

🔒 **Requires Authentication: SUPER_ADMIN**

Delete an organization.

---

## Project Management

### Base Path: `/api/projects`

#### Create Project

**POST** `/api/projects`

🔒 **Requires Authentication: PARTNER role + approved user**

Create a new project.

**Request Body:**

```json
{
  "partner": "Health Partners Kenya",
  "title": "Maternal Health Initiative in Kakamega County",
  "projectTheme": "MNH",
  "startDate": "2024-02-01",
  "endDate": "2024-12-31",
  "activityType": "Healthcare Service Delivery",
  "county": "Kakamega",
  "subCounty": "Lugari",
  "mapsAddress": "Lugari Sub-County Hospital, Kakamega County, Kenya",
  "contactPersonName": "Dr. Sarah Johnson",
  "contactPersonRole": "Project Coordinator",
  "contactPersonEmail": "sarah.johnson@healthpartners.ke",
  "objectives": "Improve maternal health outcomes by establishing 5 new health centers and training 50 healthcare workers.",
  "budget": 500000.0
}
```

**Project Themes:**

- `GBV` - Gender Based Violence
- `AYPSRH` - Adolescent and Young People Sexual and Reproductive Health
- `MNH` - Maternity and Newborn Health
- `FP` - Family Planning
- `CH` - Child Health
- `AH` - Adolescent Health

**Response (201 Created):**

```json
{
  "status": 201,
  "message": "Project created successfully",
  "data": {
    "id": 1,
    "partner": "Health Partners Kenya",
    "title": "Maternal Health Initiative in Kakamega County",
    "projectTheme": "MNH",
    "startDate": "2024-02-01",
    "endDate": "2024-12-31",
    "status": "pending",
    "approvalStatus": "PENDING",
    "budget": 500000.0,
    "latitude": -0.2763,
    "longitude": 34.7516,
    "createdAt": "2024-01-20T15:30:00"
  }
}
```

#### Get All Projects

**GET** `/api/projects`

Get paginated list of projects (public access).

**Query Parameters:**

- `page` (default: 0) - Page number
- `size` (default: 10) - Page size
- `sortBy` (default: "id") - Sort field
- `sortDir` (default: "desc") - Sort direction

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Projects retrieved successfully",
  "data": {
    "projects": [
      {
        "id": 1,
        "partner": "Health Partners Kenya",
        "title": "Maternal Health Initiative",
        "projectTheme": "MNH",
        "county": "Kakamega",
        "status": "active",
        "budget": 500000.0,
        "completionPercentage": 35
      }
    ],
    "currentPage": 0,
    "totalItems": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

#### Get Project by ID

**GET** `/api/projects/{id}`

Get detailed project information.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Project found",
  "data": {
    "id": 1,
    "partner": "Health Partners Kenya",
    "title": "Maternal Health Initiative in Kakamega County",
    "projectTheme": "MNH",
    "startDate": "2024-02-01",
    "endDate": "2024-12-31",
    "activityType": "Healthcare Service Delivery",
    "county": "Kakamega",
    "subCounty": "Lugari",
    "mapsAddress": "Lugari Sub-County Hospital, Kakamega County, Kenya",
    "contactPersonName": "Dr. Sarah Johnson",
    "contactPersonRole": "Project Coordinator",
    "contactPersonEmail": "sarah.johnson@healthpartners.ke",
    "objectives": "Improve maternal health outcomes...",
    "budget": 500000.0,
    "latitude": -0.2763,
    "longitude": 34.7516,
    "status": "active",
    "completionPercentage": 35,
    "approvalStatus": "APPROVED",
    "hasReports": true,
    "createdAt": "2024-01-20T15:30:00",
    "updatedAt": "2024-01-25T10:15:00"
  }
}
```

#### Update Project

**PUT** `/api/projects/{id}`

🔒 **Requires Authentication: Project owner or ADMIN**

Update project details.

**Request Body:** Same as create project

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Project updated successfully",
  "data": {
    // Updated project object
  }
}
```

#### Delete Project

**DELETE** `/api/projects/{id}`

🔒 **Requires Authentication: Project owner or ADMIN**

Delete a project.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Project deleted successfully",
  "data": null
}
```

#### Search Projects

**GET** `/api/projects/search`

Search projects with multiple filters.

**Query Parameters:**

- `partner` (optional) - Partner organization name
- `title` (optional) - Project title keyword
- `status` (optional) - Project status
- `county` (optional) - County name
- `activityType` (optional) - Activity type

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Search completed successfully",
  "data": [
    {
      "id": 1,
      "title": "Maternal Health Initiative",
      "partner": "Health Partners Kenya",
      "county": "Kakamega",
      "status": "active"
    }
  ]
}
```

#### Get Projects by Status

**GET** `/api/projects/status/{status}`

Get projects filtered by status.

**Path Parameters:**

- `status`: active, pending, stalled, completed, abandoned

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Projects retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Maternal Health Initiative",
      "status": "active",
      "partner": "Health Partners Kenya"
    }
  ]
}
```

#### Get Projects with Coordinates

**GET** `/api/projects/with-coordinates`

Get projects that have geographic coordinates (for map display).

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Projects with coordinates retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Maternal Health Initiative",
      "latitude": -0.2763,
      "longitude": 34.7516,
      "county": "Kakamega",
      "partner": "Health Partners Kenya"
    }
  ]
}
```

#### Get Projects in Bounding Box

**GET** `/api/projects/in-bounds`

Get projects within a geographic bounding box.

**Query Parameters:**

- `minLat` (required) - Minimum latitude
- `maxLat` (required) - Maximum latitude
- `minLng` (required) - Minimum longitude
- `maxLng` (required) - Maximum longitude

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Projects in bounding box retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Maternal Health Initiative",
      "latitude": -0.2763,
      "longitude": 34.7516
    }
  ]
}
```

#### Get Projects by Date Range

**GET** `/api/projects/date-range`

Get projects within a date range.

**Query Parameters:**

- `startDate` (required) - Start date (YYYY-MM-DD)
- `endDate` (required) - End date (YYYY-MM-DD)

#### Get Active Projects

**GET** `/api/projects/active`

Get currently active projects.

#### Get Project Statistics

**GET** `/api/projects/statistics`

Get comprehensive project statistics.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Statistics retrieved successfully",
  "data": {
    "totalProjects": 45,
    "projectsWithCoordinates": 38,
    "coordinatesCoveragePercentage": 84.4,
    "statusCounts": {
      "active": 25,
      "completed": 15,
      "pending": 3,
      "stalled": 2
    },
    "countyCounts": {
      "Kakamega": 8,
      "Nairobi": 12,
      "Kiambu": 6
    }
  }
}
```

#### Get My Projects

**GET** `/api/projects/my-projects`

🔒 **Requires Authentication: PARTNER role**

Get projects created by the authenticated user.

### Admin Endpoints

#### Approve Project

**POST** `/api/projects/admin/approve/{projectId}`

🔒 **Requires Authentication: ADMIN or SUPER_ADMIN**

Approve a pending project.

#### Reject Project

**POST** `/api/projects/admin/reject/{projectId}`

🔒 **Requires Authentication: ADMIN or SUPER_ADMIN**

Reject a pending project.

**Request Body:**

```json
{
  "reason": "Insufficient documentation"
}
```

#### Get Projects by Approval Status

**GET** `/api/projects/admin/approval-status/{status}`

🔒 **Requires Authentication: ADMIN or SUPER_ADMIN**

Get projects filtered by approval status.

**Path Parameters:**

- `status`: PENDING, APPROVED, REJECTED

#### Get Admin Dashboard Statistics

**GET** `/api/projects/admin/dashboard-stats`

🔒 **Requires Authentication: ADMIN or SUPER_ADMIN**

Get administrative dashboard statistics.

---

## Project Reports

### Base Path: `/api/reports`

#### Create Project Report

**POST** `/api/reports/projects/{projectId}`

🔒 **Requires Authentication**

Create a new report for a specific project.

**Request Body:**

```json
{
  "title": "Q1 2024 Progress Report - Maternal Health Initiative",
  "summary": "Quarterly progress summary highlighting key achievements and challenges.",
  "content": "Detailed report content with comprehensive project updates, metrics, and analysis.",
  "outcomesAchieved": "Established 3 out of 5 planned health centers. Trained 35 healthcare workers.",
  "challengesFaced": "Delays in construction due to weather conditions. Shortage of specialized medical equipment.",
  "lessonsLearned": "Early stakeholder engagement is crucial. Weather contingency planning needed.",
  "recommendations": "Accelerate procurement process. Establish backup supplier relationships.",
  "beneficiariesReached": 1250,
  "budgetUtilized": 175000.0,
  "budgetVariance": -25000.0,
  "completionPercentage": 35,
  "reportType": "INTERIM",
  "attachments": "[\"report-q1-2024.pdf\", \"budget-analysis.xlsx\"]",
  "images": "[\"health-center-1.jpg\", \"training-session.jpg\"]"
}
```

**Report Types:**

- `COMPLETION` - Final project completion report
- `INTERIM` - Interim/progress report
- `FINANCIAL` - Financial report
- `IMPACT` - Impact assessment report
- `EVALUATION` - Project evaluation report

**Response (201 Created):**

```json
{
  "status": 201,
  "message": "Report created successfully",
  "data": {
    "id": 1,
    "title": "Q1 2024 Progress Report - Maternal Health Initiative",
    "summary": "Quarterly progress summary...",
    "reportType": "INTERIM",
    "reportStatus": "DRAFT",
    "beneficiariesReached": 1250,
    "budgetUtilized": 175000.0,
    "completionPercentage": 35,
    "submittedBy": 1,
    "createdAt": "2024-01-25T14:30:00",
    "project": {
      "id": 1,
      "title": "Maternal Health Initiative"
    }
  }
}
```

#### Get Report by ID

**GET** `/api/reports/{id}`

Get specific report details. Access control applies:

- Published reports: Public access
- Unpublished reports: Author or Admin only

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Report retrieved successfully",
  "data": {
    "id": 1,
    "title": "Q1 2024 Progress Report",
    "summary": "Quarterly progress summary...",
    "content": "Detailed report content...",
    "outcomesAchieved": "Established 3 out of 5 planned health centers...",
    "challengesFaced": "Delays in construction...",
    "lessonsLearned": "Early stakeholder engagement...",
    "recommendations": "Accelerate procurement process...",
    "beneficiariesReached": 1250,
    "budgetUtilized": 175000.0,
    "budgetVariance": -25000.0,
    "completionPercentage": 35,
    "reportType": "INTERIM",
    "reportStatus": "PUBLISHED",
    "attachments": "[\"report-q1-2024.pdf\", \"budget-analysis.xlsx\"]",
    "images": "[\"health-center-1.jpg\", \"training-session.jpg\"]",
    "submittedBy": 1,
    "submittedAt": "2024-01-25T16:00:00",
    "publishedAt": "2024-01-26T10:00:00",
    "project": {
      "id": 1,
      "title": "Maternal Health Initiative",
      "partner": "Health Partners Kenya"
    }
  }
}
```

#### Get Published Reports

**GET** `/api/reports/published`

Get paginated list of published reports (public access).

**Query Parameters:**

- `page` (default: 0) - Page number
- `size` (default: 10) - Page size
- `sortBy` (default: "publishedAt") - Sort field
- `sortDir` (default: "desc") - Sort direction

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Published reports retrieved successfully",
  "data": {
    "reports": [
      {
        "id": 1,
        "title": "Q1 2024 Progress Report",
        "summary": "Quarterly progress summary...",
        "reportType": "INTERIM",
        "beneficiariesReached": 1250,
        "completionPercentage": 35,
        "publishedAt": "2024-01-26T10:00:00",
        "project": {
          "title": "Maternal Health Initiative",
          "partner": "Health Partners Kenya"
        }
      }
    ],
    "currentPage": 0,
    "totalItems": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

#### Get Project Reports

**GET** `/api/reports/projects/{projectId}`

Get all reports for a specific project. Access control applies.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Project reports retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Q1 2024 Progress Report",
      "reportType": "INTERIM",
      "reportStatus": "PUBLISHED",
      "completionPercentage": 35,
      "publishedAt": "2024-01-26T10:00:00"
    }
  ]
}
```

#### Get Completion Reports

**GET** `/api/reports/completion`

Get paginated list of completion reports.

**Query Parameters:**

- `page` (default: 0) - Page number
- `size` (default: 10) - Page size

#### Search Reports

**GET** `/api/reports/search`

Search reports with multiple filters.

**Query Parameters:**

- `projectId` (optional) - Specific project ID
- `reportType` (optional) - Report type filter
- `reportStatus` (optional) - Report status filter
- `keyword` (optional) - Search in title and content

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Reports search completed",
  "data": [
    {
      "id": 1,
      "title": "Q1 2024 Progress Report",
      "reportType": "INTERIM",
      "project": {
        "title": "Maternal Health Initiative"
      }
    }
  ]
}
```

#### Update Report

**PUT** `/api/reports/{id}`

🔒 **Requires Authentication: Report author only**

Update report details.

**Request Body:** Same as create report

#### Submit Report for Review

**POST** `/api/reports/{id}/submit`

🔒 **Requires Authentication: Report author only**

Submit a draft report for admin review.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Report submitted for review successfully",
  "data": null
}
```

#### Delete Report

**DELETE** `/api/reports/{id}`

🔒 **Requires Authentication: Report author or Admin**

Delete a report.

### Admin Endpoints

#### Get Reports for Review

**GET** `/api/reports/admin/review`

🔒 **Requires Authentication: SUPER_ADMIN**

Get all reports pending review.

#### Set Report Under Review

**POST** `/api/reports/admin/{id}/review`

🔒 **Requires Authentication: SUPER_ADMIN**

Mark a report as under review.

#### Approve Report

**POST** `/api/reports/admin/{id}/approve`

🔒 **Requires Authentication: SUPER_ADMIN**

Approve a report for publication.

#### Reject Report

**POST** `/api/reports/admin/{id}/reject`

🔒 **Requires Authentication: SUPER_ADMIN**

Reject a report and send back for revision.

#### Publish Report

**POST** `/api/reports/admin/{id}/publish`

🔒 **Requires Authentication: SUPER_ADMIN**

Publish an approved report.

#### Get Report Statistics

**GET** `/api/reports/admin/stats`

🔒 **Requires Authentication: SUPER_ADMIN**

Get comprehensive report statistics.

---

## Data Models

### User

```typescript
interface User {
  id: number;
  name: string;
  email: string;
  role: "SUPER_ADMIN" | "PARTNER";
  approvalStatus: "PENDING" | "APPROVED" | "REJECTED";
  status: "INACTIVE" | "ACTIVE";
  emailVerified: boolean;
  verified: boolean; // Legacy field
  approvedBy?: number;
  approvedAt?: string; // ISO 8601
  rejectionReason?: string;
  createdAt: string; // ISO 8601
  updatedAt: string; // ISO 8601
  lastLogin?: string; // ISO 8601
  organization?: Organization;
}
```

### Organization

```typescript
interface Organization {
  id: number;
  name: string;
  organizationType:
    | "NGO"
    | "GOVERNMENT_AGENCY"
    | "PRIVATE_COMPANY"
    | "INTERNATIONAL_ORG"
    | "FOUNDATION"
    | "COMMUNITY_GROUP"
    | "ACADEMIC_INSTITUTION"
    | "HEALTHCARE_PROVIDER"
    | "DONOR_AGENCY"
    | "OTHER";
  description?: string;
  contactEmail?: string;
  contactPhone?: string;
  address?: string;
  websiteUrl?: string;
  registrationNumber?: string;
  approvalStatus: "PENDING" | "APPROVED" | "REJECTED";
  approvedBy?: number;
  approvedAt?: string; // ISO 8601
  rejectionReason?: string;
  createdAt: string; // ISO 8601
  updatedAt: string; // ISO 8601
}
```

### Project

```typescript
interface Project {
  id: number;
  partner: string;
  title: string;
  projectTheme: "GBV" | "AYPSRH" | "MNH" | "FP" | "CH" | "AH";
  startDate: string; // YYYY-MM-DD
  endDate: string; // YYYY-MM-DD
  activityType: string;
  county: string;
  subCounty?: string;
  mapsAddress?: string;
  contactPersonName?: string;
  contactPersonRole?: string;
  contactPersonEmail?: string;
  objectives?: string;
  budget?: number;
  latitude?: number;
  longitude?: number;
  status: "active" | "pending" | "stalled" | "completed" | "abandoned";
  completionPercentage: number;
  completedAt?: string; // ISO 8601
  hasReports: boolean;
  approvalStatus: "PENDING" | "APPROVED" | "REJECTED";
  approvedBy?: number;
  approvedAt?: string; // ISO 8601
  rejectionReason?: string;
  createdAt: string; // ISO 8601
  updatedAt: string; // ISO 8601
}
```

### ProjectReport

```typescript
interface ProjectReport {
  id: number;
  project: Project;
  title: string;
  summary?: string;
  content: string;
  outcomesAchieved?: string;
  challengesFaced?: string;
  lessonsLearned?: string;
  recommendations?: string;
  beneficiariesReached?: number;
  budgetUtilized?: number;
  budgetVariance?: number;
  completionPercentage?: number;
  attachments?: string; // JSON array of file paths
  images?: string; // JSON array of image paths
  reportStatus:
    | "DRAFT"
    | "SUBMITTED"
    | "UNDER_REVIEW"
    | "APPROVED"
    | "PUBLISHED"
    | "REJECTED"
    | "ARCHIVED";
  reportType: "COMPLETION" | "INTERIM" | "FINANCIAL" | "IMPACT" | "EVALUATION";
  submittedBy: number;
  submittedAt?: string; // ISO 8601
  reviewedBy?: number;
  reviewedAt?: string; // ISO 8601
  publishedAt?: string; // ISO 8601
  createdAt: string; // ISO 8601
  updatedAt: string; // ISO 8601
}
```

### ApiResponse

```typescript
interface ApiResponse<T> {
  status: number;
  message: string;
  data: T | null;
}
```

---

## Error Handling

### HTTP Status Codes

- **200 OK** - Successful GET, PUT requests
- **201 Created** - Successful POST requests
- **400 Bad Request** - Invalid request data
- **401 Unauthorized** - Authentication required
- **403 Forbidden** - Insufficient permissions
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server error

### Error Response Format

All error responses follow the same format:

```json
{
  "status": 400,
  "message": "Validation error: Name and email are required.",
  "data": null
}
```

### Common Error Scenarios

#### Authentication Errors

```json
{
  "status": 401,
  "message": "JWT token is required",
  "data": null
}
```

#### Validation Errors

```json
{
  "status": 400,
  "message": "Name and email are required.",
  "data": null
}
```

#### Permission Errors

```json
{
  "status": 403,
  "message": "Access denied. Admin role required.",
  "data": null
}
```

#### Not Found Errors

```json
{
  "status": 404,
  "message": "Project not found with ID: 123",
  "data": null
}
```

### Rate Limiting

The API implements rate limiting to prevent abuse. If you exceed the rate limit, you'll receive:

```json
{
  "status": 429,
  "message": "Too many requests. Please try again later.",
  "data": null
}
```

---

## Authentication Headers

For protected endpoints, include the JWT token in the Authorization header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Pagination

List endpoints support pagination with the following parameters:

- `page` - Page number (0-based, default: 0)
- `size` - Page size (default: 10, max: 100)
- `sortBy` - Sort field (default varies by endpoint)
- `sortDir` - Sort direction: "asc" or "desc" (default: "desc")

Paginated responses include metadata:

```json
{
  "status": 200,
  "message": "Data retrieved successfully",
  "data": {
    "items": [...],
    "currentPage": 0,
    "totalItems": 150,
    "totalPages": 15,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

## Testing Endpoints

### Test Authentication

**GET** `/api/protected`

🔒 **Requires Authentication**

Simple endpoint to test authentication.

**Response (200 OK):**

```json
{
  "status": 200,
  "message": "Access granted to protected resource.",
  "data": {
    "user": "john.doe@example.com"
  }
}
```

---

## Environment Configuration

The API behavior can be configured through environment variables:

- `FRONTEND_ORIGIN` - Allowed CORS origins (comma-separated)
- `JWT_EXPIRATION` - JWT token expiration time in seconds (default: 3600)
- `EMAIL_SERVICE_*` - Email service configuration for OTP delivery
