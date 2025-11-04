-- Database Migration Script for Two-Tier SUPER_ADMIN Approval System
-- Execute this script to add support for thematic area reviewers and two-tier approval workflow

-- ============================================================
-- 1. ALTER USERS TABLE - Add thematic_area column
-- ============================================================
ALTER TABLE users ADD COLUMN IF NOT EXISTS thematic_area VARCHAR(50);

-- Add comment explaining the column
COMMENT ON COLUMN users.thematic_area IS 'Thematic area assigned to SUPER_ADMIN_REVIEWER. Values: GBV, AYPSRH, MNH, FP, CH, AH';

-- ============================================================
-- 2. ALTER PROJECTS TABLE - Add review tracking columns
-- ============================================================
ALTER TABLE projects ADD COLUMN IF NOT EXISTS reviewed_by BIGINT;
ALTER TABLE projects ADD COLUMN IF NOT EXISTS reviewed_at TIMESTAMP;
ALTER TABLE projects ADD COLUMN IF NOT EXISTS reviewer_comments TEXT;
ALTER TABLE projects ADD COLUMN IF NOT EXISTS approval_workflow_status VARCHAR(50) DEFAULT 'PENDING_REVIEW';

-- Add comments explaining the columns
COMMENT ON COLUMN projects.reviewed_by IS 'User ID of the thematic reviewer who reviewed this project';
COMMENT ON COLUMN projects.reviewed_at IS 'Timestamp when the project was reviewed';
COMMENT ON COLUMN projects.reviewer_comments IS 'Comments from the thematic reviewer';
COMMENT ON COLUMN projects.approval_workflow_status IS 'Two-tier workflow status: PENDING_REVIEW, UNDER_REVIEW, REVIEWED, PENDING_FINAL_APPROVAL, APPROVED, REJECTED_BY_REVIEWER, REJECTED_BY_APPROVER';

-- ============================================================
-- 3. UPDATE EXISTING DATA
-- ============================================================

-- Set workflow status for existing projects based on their current approval_status
UPDATE projects 
SET approval_workflow_status = CASE
    WHEN approval_status = 'APPROVED' THEN 'APPROVED'
    WHEN approval_status = 'REJECTED' THEN 'REJECTED_BY_APPROVER'
    WHEN approval_status = 'PENDING' THEN 'PENDING_REVIEW'
    WHEN approval_status = 'SUBMITTED' THEN 'PENDING_REVIEW'
    ELSE 'PENDING_REVIEW'
END
WHERE approval_workflow_status IS NULL OR approval_workflow_status = 'PENDING_REVIEW';

-- ============================================================
-- 4. SAMPLE DATA - Create reviewers for each thematic area
-- ============================================================
-- Note: Replace emails and names with actual reviewer information
-- This is a template for creating 5-6 reviewers

-- GBV Reviewer
INSERT INTO users (name, email, role, thematic_area, status, email_verified, verified, approval_status, created_at, updated_at, approved_at)
VALUES ('GBV Reviewer', 'kapolonbraine@gmail.com', 'SUPER_ADMIN_REVIEWER', 'GBV', 'ACTIVE', true, true, 'APPROVED', NOW(), NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- AYPSRH Reviewer
-- INSERT INTO users (name, email, role, thematic_area, status, email_verified, verified, approval_status, created_at, updated_at, approved_at)
-- VALUES ('AYPSRH Reviewer', 'aypsrh.reviewer@moh.go.ke', 'SUPER_ADMIN_REVIEWER', 'AYPSRH', 'ACTIVE', true, true, 'APPROVED', NOW(), NOW(), NOW())
-- ON CONFLICT (email) DO NOTHING;

-- MNH Reviewer
-- INSERT INTO users (name, email, role, thematic_area, status, email_verified, verified, approval_status, created_at, updated_at, approved_at)
-- VALUES ('MNH Reviewer', 'mnh.reviewer@moh.go.ke', 'SUPER_ADMIN_REVIEWER', 'MNH', 'ACTIVE', true, true, 'APPROVED', NOW(), NOW(), NOW())
-- ON CONFLICT (email) DO NOTHING;

-- FP Reviewer
-- INSERT INTO users (name, email, role, thematic_area, status, email_verified, verified, approval_status, created_at, updated_at, approved_at)
-- VALUES ('FP Reviewer', 'fp.reviewer@moh.go.ke', 'SUPER_ADMIN_REVIEWER', 'FP', 'ACTIVE', true, true, 'APPROVED', NOW(), NOW(), NOW())
-- ON CONFLICT (email) DO NOTHING;

-- CH Reviewer
-- INSERT INTO users (name, email, role, thematic_area, status, email_verified, verified, approval_status, created_at, updated_at, approved_at)
-- VALUES ('CH Reviewer', 'ch.reviewer@moh.go.ke', 'SUPER_ADMIN_REVIEWER', 'CH', 'ACTIVE', true, true, 'APPROVED', NOW(), NOW(), NOW())
-- ON CONFLICT (email) DO NOTHING;

-- AH Reviewer
-- INSERT INTO users (name, email, role, thematic_area, status, email_verified, verified, approval_status, created_at, updated_at, approved_at)
-- VALUES ('AH Reviewer', 'ah.reviewer@moh.go.ke', 'SUPER_ADMIN_REVIEWER', 'AH', 'ACTIVE', true, true, 'APPROVED', NOW(), NOW(), NOW())
-- ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- 5. UPDATE EXISTING SUPER_ADMIN - Optional
-- ============================================================
-- If you want to convert existing SUPER_ADMIN to SUPER_ADMIN_APPROVER
-- Uncomment the following line and specify the email

UPDATE users SET role = 'SUPER_ADMIN_APPROVER' WHERE email = 'braine.kapolon@strathmore.edu';

-- ============================================================
-- 6. VERIFICATION QUERIES
-- ============================================================
-- Run these queries to verify the migration was successful

-- Check users table structure
-- SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'thematic_area';

-- Check projects table structure
-- SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'projects' AND column_name IN ('reviewed_by', 'reviewed_at', 'reviewer_comments', 'approval_workflow_status');

-- Check reviewers
-- SELECT id, name, email, role, thematic_area FROM users WHERE role = 'SUPER_ADMIN_REVIEWER';

-- Check approvers
-- SELECT id, name, email, role FROM users WHERE role IN ('SUPER_ADMIN_APPROVER', 'SUPER_ADMIN');

-- Check project workflow statuses
-- SELECT approval_workflow_status, COUNT(*) FROM projects GROUP BY approval_workflow_status;
