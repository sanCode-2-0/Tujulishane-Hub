# Tujulishane Hub API Documentation

**Base URL:** `/api`  
**Auth:** Bearer JWT token in `Authorization` header  
**Response format:** `{ "success": boolean, "message": string, "data": T }`

---

## Authentication — `/api/auth`

| Method | Endpoint | Auth | Body | Description |
|--------|----------|------|------|-------------|
| GET | `/auth/health` | No | — | Health check |
| POST | `/auth/register` | No | `name`, `email`, `organization?`, `accountType?`, `parentDonorId?`, `organizationId?`, `documents?` (files) | Register PARTNER or DONOR account |
| POST | `/auth/verify` | No | `{ email, otp }` | Verify registration OTP |
| POST | `/auth/login` | No | `{ email }` | Send login OTP |
| POST | `/auth/verify/login` | No | `{ email, otp }` | Verify login OTP → returns JWT |
| POST | `/auth/send-login-otp` | No | `{ email }` | Resend login OTP |
| POST | `/auth/bootstrap/super-admin` | No | `{ email, name, secretKey }` | Bootstrap first SUPER_ADMIN (one-time) |

### User Profile

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/auth/profile` | Yes | Get current user profile |
| GET | `/auth/me/donor` | PARTNER | Get current partner's donor |
| GET | `/auth/partners/available` | Yes | Get available partners |
| GET | `/auth/counts` | Yes | Get user counts summary |
| GET | `/auth/donors` | Yes | Get all donors |
| GET | `/auth/donor/{donorId}/partners` | Yes | Get partners linked to a donor |
| POST | `/auth/partner/{partnerId}/link-donor/{donorId}` | ADMIN | Link partner to donor |
| POST | `/auth/partner/{partnerId}/unlink-donor` | ADMIN | Unlink partner from donor |

### User Documents

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/auth/users/{userId}/supporting-documents` | Yes | List user's supporting documents |
| GET | `/auth/users/{userId}/supporting-documents/{documentId}/view` | Yes | View document inline |
| GET | `/auth/users/{userId}/supporting-documents/{documentId}/download` | Yes | Download document |

### Admin — User Management

> Required roles: `ADMIN`, `SUPER_ADMIN`, `SUPER_ADMIN_APPROVER`, or `SUPER_ADMIN_REVIEWER`

| Method | Endpoint | Role | Body | Description |
|--------|----------|------|------|-------------|
| GET | `/auth/admin/users` | ADMIN | — | Get all users |
| GET | `/auth/admin/users/search?query=` | ADMIN | — | Search users by name or email |
| GET | `/auth/admin/users/status/{status}` | ADMIN | — | Get users by status (`PENDING`, `APPROVED`, `REJECTED`) |
| GET | `/auth/admin/users/by-status/{status}` | SUPER_ADMIN | — | Get users by status |
| GET | `/auth/admin/pending-users` | ADMIN | — | Get pending users |
| POST | `/auth/admin/approve-user/{userId}` | ADMIN | — | Approve user |
| POST | `/auth/admin/reject-user/{userId}` | ADMIN | `{ reason }` | Reject user |
| DELETE | `/auth/admin/delete-user/{userId}` | SUPER_ADMIN_APPROVER | — | Delete user |
| POST | `/auth/admin/update-role-with-theme/{userId}` | ADMIN | `{ role, thematicArea? }` | Update user role |
| GET | `/auth/admin/reviewers` | ADMIN | — | Get all reviewers |
| GET | `/auth/admin/reviewers/by-theme/{themeCode}` | ADMIN | — | Get reviewers by thematic area |
| GET | `/auth/admin/approvers` | ADMIN | — | Get all approvers |
| POST | `/auth/admin/assign-thematic-area/{userId}` | ADMIN | `{ thematicArea }` | Assign thematic area to reviewer |
| POST | `/auth/admin/assign-thematic-areas/{userId}` | ADMIN | `{ thematicAreas: [] }` | Assign multiple thematic areas |
| POST | `/auth/admin/add-thematic-area/{userId}` | ADMIN | `{ thematicArea }` | Add single thematic area |
| DELETE | `/auth/admin/remove-thematic-area/{userId}/{thematicAreaCode}` | ADMIN | — | Remove thematic area |
| POST | `/auth/admin/convert-to-reviewer/{userId}` | ADMIN | `{ thematicAreas: [] }` | Convert user to reviewer |
| POST | `/auth/admin/create-reviewer` | ADMIN | `{ name, email, thematicAreas: [] }` | Create new reviewer |

---

## Projects — `/api/projects`

### Public

| Method | Endpoint | Query Params | Description |
|--------|----------|-------------|-------------|
| GET | `/projects/` | `page`, `size`, `sortBy`, `sortDir` | Get all projects (paginated) |
| GET | `/projects/{id}` | — | Get project by ID |
| GET | `/projects/by-number/{projectNo}` | — | Get project by number |
| GET | `/projects/thematic-areas` | — | List thematic areas |
| GET | `/projects/public/statistics` | — | Public project statistics |

### Authenticated

| Method | Endpoint | Query Params | Description |
|--------|----------|-------------|-------------|
| POST | `/projects/` | — | Create project (multipart: `project` JSON + `supporting_documents?`) |
| PUT | `/projects/{id}` | — | Update project (multipart: `project` JSON + `supporting_documents?`) |
| DELETE | `/projects/{id}` | — | Delete project |
| GET | `/projects/my-projects` | — | Get current user's projects |
| GET | `/projects/all` | — | Get all projects |
| GET | `/projects/search` | `partner`, `title`, `projectNo`, `status`, `county`, `activityType` | Search projects |
| GET | `/projects/status/{status}` | — | Filter by status |
| GET | `/projects/with-coordinates` | — | Projects with map coordinates |
| GET | `/projects/in-bounds` | `minLat`, `maxLat`, `minLng`, `maxLng` | Projects in bounding box |
| GET | `/projects/date-range` | `startDate`, `endDate` | Projects by date range |
| GET | `/projects/active` | — | Currently active projects |
| GET | `/projects/statistics` | — | Statistics (role-based) |
| GET | `/projects/counts` | `status?`, `category?` | Count summary |
| POST | `/projects/geocode-batch` | — | Batch geocode all projects |

### Admin — Project Management

| Method | Endpoint | Role | Body | Description |
|--------|----------|------|------|-------------|
| GET | `/projects/admin/all` | SUPER_ADMIN | — | All projects |
| GET | `/projects/admin/approval-status/{status}` | ADMIN | — | Projects by approval status |
| GET | `/projects/admin/dashboard-stats` | ADMIN | — | Dashboard statistics |
| GET | `/projects/admin/projects-for-review` | REVIEWER | — | Projects pending thematic review |
| GET | `/projects/admin/projects-awaiting-final-approval` | APPROVER | — | Projects awaiting final approval |
| POST | `/projects/admin/approve/{projectId}` | ADMIN | — | Approve project (legacy) |
| POST | `/projects/admin/reject/{projectId}` | ADMIN | `{ reason }` | Reject project (legacy) |
| POST | `/projects/admin/review/{projectId}` | REVIEWER | `{ comments, approved }` | Thematic review |
| POST | `/projects/admin/final-approve/{projectId}` | APPROVER | `{ comments? }` | Final approval |
| POST | `/projects/admin/final-reject/{projectId}` | APPROVER | `{ reason }` | Final rejection |
| POST | `/projects/admin/complete/{id}` | ADMIN | — | Mark project completed |
| POST | `/projects/admin/stall/{id}` | ADMIN | — | Mark project stalled |
| POST | `/projects/{projectId}/archive` | ADMIN | `{ lessonsLearned, successFactors, challenges, recommendations }` | Archive completed project |

### Project Documents

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/projects/{projectId}/documents` | List project documents |
| GET | `/projects/{projectId}/documents/{documentId}/view` | View document inline |
| GET | `/projects/{projectId}/documents/{documentId}` | Download document |

### Project Reports

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/projects/{projectId}/reports/upload` | Upload report document (multipart: `file`) |
| GET | `/projects/{projectId}/reports/documents` | List report documents |
| GET | `/projects/{projectId}/reports/documents/{documentId}` | Download report document |
| DELETE | `/projects/{projectId}/reports/documents/{documentId}` | Delete report document (owner only) |

### Collaborators

| Method | Endpoint | Query Params | Body | Description |
|--------|----------|-------------|------|-------------|
| GET | `/projects/{projectId}/collaborators` | — | — | List collaborators |
| POST | `/projects/{projectId}/collaborators` | `collaboratorEmail`, `role` | — | Add collaborator |
| PUT | `/projects/{projectId}/collaborators/{collaboratorId}` | `newRole` | — | Update collaborator role |
| DELETE | `/projects/{projectId}/collaborators/{collaboratorId}` | — | — | Remove collaborator |
| GET | `/projects/collaborations/my-projects` | — | — | My collaborations (PARTNER) |
| GET | `/projects/admin/collaborators` | — | — | All collaborators (ADMIN) |

---

## Reports — `/api/reports`

| Method | Endpoint | Query Params | Body | Description |
|--------|----------|-------------|------|-------------|
| POST | `/reports/projects/{projectId}` | — | `ProjectReport` | Create report |
| GET | `/reports/{id}` | — | — | Get report by ID |
| PUT | `/reports/{id}` | — | `ProjectReport` | Update report (author only) |
| DELETE | `/reports/{id}` | — | — | Delete report |
| POST | `/reports/{id}/submit` | — | — | Submit for review |
| GET | `/reports/published` | `page`, `size`, `sortBy`, `sortDir` | — | Published reports (paginated) |
| GET | `/reports/projects/{projectId}` | — | — | Reports for a project |
| GET | `/reports/completion` | `page`, `size` | — | Completion reports (paginated) |
| GET | `/reports/search` | `projectId?`, `reportType?`, `reportStatus?`, `keyword?` | — | Search reports |
| GET | `/reports/admin/review` | — | — | Reports pending review (ADMIN) |
| POST | `/reports/admin/{id}/review` | — | — | Mark under review (ADMIN) |
| POST | `/reports/admin/{id}/approve` | — | — | Approve report (SUPER_ADMIN) |
| POST | `/reports/admin/{id}/reject` | — | — | Reject report (SUPER_ADMIN) |
| POST | `/reports/admin/{id}/publish` | — | — | Publish report (SUPER_ADMIN) |
| GET | `/reports/admin/stats` | — | — | Report statistics (SUPER_ADMIN) |

---

## Past Projects — `/api/past-projects`

| Method | Endpoint | Query Params | Description |
|--------|----------|-------------|-------------|
| GET | `/past-projects/` | `page`, `size`, `sortBy`, `sortDir` | All past projects (paginated) |
| GET | `/past-projects/{id}` | — | Get past project by ID |
| GET | `/past-projects/search` | `partner?`, `title?`, `finalStatus?`, `county?`, `activityType?`, `archivedAfter?` | Search past projects |
| GET | `/past-projects/map` | — | Past projects for map display |
| GET | `/past-projects/stats/status` | — | Stats by status |
| POST | `/past-projects/` | — | Create past project (ADMIN) |
| POST | `/past-projects/archive/{projectId}` | — | Archive project (ADMIN) — body: `{ lessonsLearned, successFactors, challenges, recommendations }` |
| PUT | `/past-projects/{id}` | — | Update past project (ADMIN) |

---

## Announcements — `/api/announcements`

| Method | Endpoint | Auth | Body | Description |
|--------|----------|------|------|-------------|
| GET | `/announcements/` | No | — | All active announcements |
| GET | `/announcements/{id}` | No | — | Get announcement by ID |
| POST | `/announcements/` | ADMIN | `AnnouncementRequest` | Create announcement |
| POST | `/announcements/{id}/close` | Owner/ADMIN | — | Close announcement |
| GET | `/announcements/my-announcements` | PARTNER | — | My announcements |
| GET | `/announcements/{id}/messages` | Yes | — | Get announcement messages |
| POST | `/announcements/{id}/messages` | SUPER_ADMIN_REVIEWER | `MessageRequest` | Post message |
| PUT | `/announcements/{announcementId}/messages/{messageId}` | REVIEWER/APPROVER | `MessageRequest` | Update message |
| DELETE | `/announcements/{announcementId}/messages/{messageId}` | REVIEWER/APPROVER | — | Delete message |

---

## General Announcements — `/api/general-announcements`

| Method | Endpoint | Auth | Body | Description |
|--------|----------|------|------|-------------|
| GET | `/general-announcements/` | No | — | All general announcements |
| GET | `/general-announcements/{id}` | No | — | Get by ID |
| POST | `/general-announcements/` | REVIEWER/APPROVER | `GeneralAnnouncementRequest` | Create general announcement |

---

## Collaboration Requests — `/api/collaboration-requests`

| Method | Endpoint | Auth | Body | Description |
|--------|----------|------|------|-------------|
| POST | `/collaboration-requests/announcements/{announcementId}` | PARTNER | `CollaborationRequest` | Submit request |
| GET | `/collaboration-requests/my-requests` | Yes | — | My submitted requests |
| GET | `/collaboration-requests/my-projects` | PARTNER | — | Requests on my projects |
| GET | `/collaboration-requests/{id}` | Yes | — | Get request by ID |
| GET | `/collaboration-requests/admin/pending` | ADMIN | — | Pending requests |
| GET | `/collaboration-requests/admin/all` | ADMIN | — | All requests |
| POST | `/collaboration-requests/admin/{id}/approve` | SUPER_ADMIN | `{ notes? }` | Approve request |
| POST | `/collaboration-requests/admin/{id}/decline` | SUPER_ADMIN | `{ notes? }` | Decline request |

---

## Organizations — `/api/organizations`

| Method | Endpoint | Query Params | Body | Description |
|--------|----------|-------------|------|-------------|
| GET | `/organizations/` | `page`, `size`, `sortBy`, `sortDir` | — | All organizations (paginated) |
| GET | `/organizations/{id}` | — | — | Get by ID |
| GET | `/organizations/{id}/logo` | — | — | Get organization logo (image) |
| GET | `/organizations/search` | `keyword` | — | Search by name |
| GET | `/organizations/approved` | — | — | Approved organizations |
| POST | `/organizations/` | — | `Organization` | Create organization |
| PUT | `/organizations/{id}` | — | `Organization` | Update organization |
| POST | `/organizations/{id}/update-with-logo` | `name`, `organizationType`, etc. | `logo?` (file) | Update with logo |
| GET | `/organizations/admin/pending` | — | — | Pending orgs (SUPER_ADMIN) |
| GET | `/organizations/admin/status/{status}` | — | — | By status (SUPER_ADMIN) |
| POST | `/organizations/admin/approve/{id}` | — | — | Approve org (SUPER_ADMIN) |
| POST | `/organizations/admin/reject/{id}` | — | `{ reason }` | Reject org (SUPER_ADMIN) |
| GET | `/organizations/admin/stats` | — | — | Org statistics (SUPER_ADMIN) |
| DELETE | `/organizations/admin/{id}` | — | — | Delete org (SUPER_ADMIN) |

---

## Misc — `/api`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/protected` | Yes | Auth check |
| GET | `/project-documents` | ADMIN | All project documents |
| GET | `/user-documents` | ADMIN | All user documents |
| GET | `/project-reports` | ADMIN | All project reports |
| POST | `/scaffold` | No | Seed default test users (dev only) |

---

## Roles

| Role | Description |
|------|-------------|
| `PARTNER` | NGO / implementing partner |
| `DONOR` | Funding organization |
| `SUPER_ADMIN` | Full system access |
| `SUPER_ADMIN_APPROVER` | Final project approval |
| `SUPER_ADMIN_REVIEWER` | Thematic review |

## Status Values

**Users / Orgs:** `PENDING` · `APPROVED` · `REJECTED`  
**Projects:** `PENDING` · `APPROVED` · `REJECTED` · `ACTIVE` · `COMPLETED` · `STALLED`  
**Reports:** `DRAFT` · `SUBMITTED` · `UNDER_REVIEW` · `APPROVED` · `REJECTED` · `PUBLISHED`
