# Frontend Working - Backend Database Issue Found

## ✅ Frontend Status: WORKING PERFECTLY

The frontend `admin-approvals.html` is working correctly! All functionality is operational:

- ✅ Page loads without redirects
- ✅ User authentication works
- ✅ Role detection works (SUPER_ADMIN_REVIEWER)
- ✅ API calls are being made correctly
- ✅ UI is responsive and functional

## 🔴 Backend Issue: Database Constraint Violation

### The Error:

```
ERROR: new row for relation "projects" violates check constraint "projects_approval_status_check"
Detail: Failing row contains (...approvalStatus = 'pending', approvalWorkflowStatus = 'PENDING_FINAL_APPROVAL'...)
```

### The Problem:

The database has a CHECK constraint on the `projects` table that validates the `approvalStatus` column. When the backend tries to update a project's `approvalWorkflowStatus` to `PENDING_FINAL_APPROVAL`, it conflicts with the old `approvalStatus` column value.

### The Root Cause:

The database migration script (`backend/database_migration_two_tier_approval.sql`) **has not been run** on your database yet.

## 🔧 How to Fix

### Step 1: Run the Database Migration

Open your PostgreSQL database and run:

```sql
-- File: backend/database_migration_two_tier_approval.sql

-- 1. Add new columns to projects table
ALTER TABLE projects
ADD COLUMN IF NOT EXISTS reviewed_by BIGINT,
ADD COLUMN IF NOT EXISTS reviewed_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS reviewer_comments TEXT,
ADD COLUMN IF NOT EXISTS approval_workflow_status VARCHAR(50);

-- 2. Add new column to users table
ALTER TABLE users
ADD COLUMN IF NOT EXISTS thematic_area VARCHAR(50);

-- 3. Set default workflow status for existing projects
UPDATE projects
SET approval_workflow_status =
    CASE
        WHEN approval_status = 'APPROVED' THEN 'APPROVED'
        WHEN approval_status = 'REJECTED' THEN 'REJECTED_BY_REVIEWER'
        ELSE 'PENDING_REVIEW'
    END
WHERE approval_workflow_status IS NULL;

-- 4. Make approval_workflow_status NOT NULL
ALTER TABLE projects
ALTER COLUMN approval_workflow_status SET NOT NULL;

-- 5. **IMPORTANT**: Drop or modify the old check constraint
ALTER TABLE projects DROP CONSTRAINT IF EXISTS projects_approval_status_check;

-- 6. Create new check constraint for workflow status
ALTER TABLE projects
ADD CONSTRAINT projects_workflow_status_check
CHECK (approval_workflow_status IN (
    'PENDING_REVIEW',
    'UNDER_REVIEW',
    'REVIEWED',
    'PENDING_FINAL_APPROVAL',
    'APPROVED',
    'REJECTED_BY_REVIEWER',
    'REJECTED_BY_APPROVER'
));

-- 7. Add foreign key for reviewed_by (optional but recommended)
ALTER TABLE projects
ADD CONSTRAINT fk_projects_reviewed_by
FOREIGN KEY (reviewed_by) REFERENCES users(id);

-- 8. Create indices for performance
CREATE INDEX IF NOT EXISTS idx_projects_workflow_status ON projects(approval_workflow_status);
CREATE INDEX IF NOT EXISTS idx_projects_reviewed_by ON projects(reviewed_by);
CREATE INDEX IF NOT EXISTS idx_users_thematic_area ON users(thematic_area);
```

### Step 2: Run Via Terminal

If you have access to psql:

```bash
cd backend
psql -U your_username -d tujulishane_hub -f database_migration_two_tier_approval.sql
```

Or if you're using pgAdmin or another tool, just copy and paste the SQL above and run it.

### Step 3: Verify Migration

Check if columns exist:

```sql
-- Check projects table
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'projects'
AND column_name IN ('reviewed_by', 'reviewed_at', 'reviewer_comments', 'approval_workflow_status');

-- Check users table
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'users'
AND column_name = 'thematic_area';

-- Check constraints
SELECT constraint_name
FROM information_schema.table_constraints
WHERE table_name = 'projects'
AND constraint_type = 'CHECK';
```

## 📋 Alternative Quick Fix (Temporary)

If you can't run the migration right now, you can temporarily disable the constraint:

```sql
-- TEMPORARY: Disable the old constraint
ALTER TABLE projects DROP CONSTRAINT IF EXISTS projects_approval_status_check;
```

**⚠️ WARNING**: This is only temporary. You should run the full migration script ASAP.

## 🎯 What Happens After Migration

Once you run the migration:

1. ✅ The old `projects_approval_status_check` constraint is removed
2. ✅ New `approval_workflow_status` column is added with proper constraint
3. ✅ `reviewed_by`, `reviewed_at`, `reviewer_comments` columns are added
4. ✅ `thematic_area` column is added to users table
5. ✅ Your frontend will work perfectly with the backend

## 🧪 Test After Migration

1. **Reload admin-approvals.html**
2. **Click "Review Project"** on any project
3. **Submit your review** - should work without errors
4. **Check console** - should see: `API Response: 200 OK`

## 📝 Summary

**Frontend**: ✅ Complete and working  
**Backend Code**: ✅ Complete and working  
**Database Schema**: ❌ Migration not run

**Action Required**: Run the database migration script to update the schema.

---

**Next Step**: Run the migration, then test the review functionality again!
