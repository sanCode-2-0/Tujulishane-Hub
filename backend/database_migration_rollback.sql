-- Rollback Script for Two-Tier SUPER_ADMIN Approval System
-- Execute this script if you need to revert the two-tier approval changes

-- ============================================================
-- WARNING: This will remove all two-tier approval data
-- Backup your database before running this script!
-- ============================================================

-- ============================================================
-- 1. REVERT PROJECT WORKFLOW STATUS TO APPROVAL STATUS
-- ============================================================
-- Map workflow statuses back to simple approval statuses
UPDATE projects 
SET approval_status = CASE
    WHEN approval_workflow_status IN ('APPROVED') THEN 'APPROVED'
    WHEN approval_workflow_status IN ('REJECTED_BY_REVIEWER', 'REJECTED_BY_APPROVER') THEN 'REJECTED'
    WHEN approval_workflow_status IN ('PENDING_REVIEW', 'UNDER_REVIEW', 'REVIEWED', 'PENDING_FINAL_APPROVAL') THEN 'PENDING'
    ELSE approval_status
END;

-- ============================================================
-- 2. DROP COLUMNS FROM PROJECTS TABLE
-- ============================================================
ALTER TABLE projects DROP COLUMN IF EXISTS reviewed_by;
ALTER TABLE projects DROP COLUMN IF EXISTS reviewed_at;
ALTER TABLE projects DROP COLUMN IF EXISTS reviewer_comments;
ALTER TABLE projects DROP COLUMN IF EXISTS approval_workflow_status;

-- ============================================================
-- 3. DROP COLUMN FROM USERS TABLE
-- ============================================================
ALTER TABLE users DROP COLUMN IF EXISTS thematic_area;

-- ============================================================
-- 4. REVERT USER ROLES (Optional)
-- ============================================================
-- Convert SUPER_ADMIN_REVIEWER back to PARTNER or SUPER_ADMIN
UPDATE users SET role = 'SUPER_ADMIN' WHERE role = 'SUPER_ADMIN_REVIEWER';

-- Convert SUPER_ADMIN_APPROVER back to SUPER_ADMIN
UPDATE users SET role = 'SUPER_ADMIN' WHERE role = 'SUPER_ADMIN_APPROVER';

-- ============================================================
-- 5. VERIFICATION QUERIES
-- ============================================================
-- Run these queries to verify the rollback was successful

-- Check that thematic_area column is removed
-- SELECT column_name FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'thematic_area';
-- Should return 0 rows

-- Check that review columns are removed from projects
-- SELECT column_name FROM information_schema.columns WHERE table_name = 'projects' AND column_name IN ('reviewed_by', 'reviewed_at', 'reviewer_comments', 'approval_workflow_status');
-- Should return 0 rows

-- Check that no users have new roles
-- SELECT role, COUNT(*) FROM users GROUP BY role;
-- Should not show SUPER_ADMIN_REVIEWER or SUPER_ADMIN_APPROVER
