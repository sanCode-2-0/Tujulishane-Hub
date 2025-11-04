# Two-Tier Approval System - Quick Reference

## 🎭 Roles

| Role                   | Count  | Purpose                                |
| ---------------------- | ------ | -------------------------------------- |
| `SUPER_ADMIN_APPROVER` | 1      | Final approval authority               |
| `SUPER_ADMIN_REVIEWER` | 5-6    | Thematic area specialists              |
| `SUPER_ADMIN`          | Legacy | Full permissions (backward compatible) |

## 🏷️ Thematic Areas

- **GBV** - Gender-Based Violence
- **AYPSRH** - Adolescent and Young People Sexual and Reproductive Health
- **MNH** - Maternal and Newborn Health
- **FP** - Family Planning
- **CH** - Child Health
- **AH** - Adolescent Health

## 📊 Workflow Statuses

```
PENDING_REVIEW → UNDER_REVIEW → PENDING_FINAL_APPROVAL → APPROVED
                      ↓                    ↓
              REJECTED_BY_REVIEWER  REJECTED_BY_APPROVER
```

## 🔑 Key Endpoints

### For Reviewers

```bash
# Get projects to review (filtered by thematic area)
GET /api/projects/admin/projects-for-review

# Review a project
POST /api/projects/admin/review/{projectId}
{
  "approved": true,
  "comments": "Looks good!"
}
```

### For Approvers

```bash
# Get reviewed projects
GET /api/projects/admin/projects-awaiting-final-approval

# Final approve
POST /api/projects/admin/final-approve/{projectId}
{
  "comments": "Approved"
}

# Final reject
POST /api/projects/admin/final-reject/{projectId}
{
  "reason": "Does not meet requirements"
}
```

### For Admins (Managing Reviewers)

```bash
# Assign thematic area to reviewer
POST /api/auth/admin/assign-thematic-area/{userId}
{
  "thematicArea": "GBV"
}

# Update user role with thematic area
POST /api/auth/admin/update-role-with-theme/{userId}
{
  "role": "SUPER_ADMIN_REVIEWER",
  "thematicArea": "MNH"
}

# Get all reviewers
GET /api/auth/admin/reviewers

# Get reviewers by theme
GET /api/auth/admin/reviewers/by-theme/GBV
```

## 💾 Database Setup

```bash
# Run migration
psql -U your_user -d tujulishane_hub -f backend/database_migration_two_tier_approval.sql

# Rollback (if needed)
psql -U your_user -d tujulishane_hub -f backend/database_migration_rollback.sql
```

## 📧 Notification Flow

1. **Partner submits** → Confirmation email
2. **Reviewer reviews** → Partner gets feedback
3. **Approver approves** → Partner gets approval notice
4. **Any rejection** → Partner gets reason + guidance

## 🛡️ Security Rules

✅ Reviewers only see projects in their thematic area  
✅ Approvers see all reviewed projects  
✅ Partners only see their own projects  
✅ All actions logged with user ID + timestamp

## 📝 Example Workflow

```
1. Partner creates "GBV Prevention Program" project
   Status: PENDING_REVIEW

2. GBV Reviewer reviews it
   POST /api/projects/admin/review/123
   {"approved": true, "comments": "Good proposal"}
   Status: PENDING_FINAL_APPROVAL

3. Approver gives final approval
   POST /api/projects/admin/final-approve/123
   {"comments": "Approved"}
   Status: APPROVED (Project goes live)
```

## 🚨 Common Issues

**Q: Reviewer can't see any projects**  
A: Check that thematic area is assigned to the reviewer

**Q: "Project is not in your assigned thematic area"**  
A: Project themes don't match reviewer's thematic area

**Q: "Project must be reviewed before final approval"**  
A: Project hasn't been reviewed by thematic reviewer yet

## 📚 Full Documentation

- [Complete System Documentation](./TWO_TIER_APPROVAL_DOCUMENTATION.md)
- [Implementation Guide](./IMPLEMENTATION_TWO_TIER_APPROVAL.md)
- [API Documentation](./API_DOCUMENTATION.md)

---

**Last Updated**: November 4, 2025
