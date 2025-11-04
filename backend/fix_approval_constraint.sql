-- Fix for Database Constraint Violation
-- The issue: projects_approval_status_check constraint is preventing updates
-- Solution: Drop the old constraint or modify it to allow new workflow statuses

-- Step 1: Check what the constraint currently is
SELECT conname, pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conrelid = 'projects'::regclass 
AND conname = 'projects_approval_status_check';

-- Step 2: Drop the old constraint (if it exists)
ALTER TABLE projects DROP CONSTRAINT IF EXISTS projects_approval_status_check;

-- Step 3: Add a new constraint that allows both old and new statuses
-- This allows approval_status to be: PENDING, SUBMITTED, APPROVED, REJECTED
-- And also allows it to be NULL (for projects using only approval_workflow_status)
ALTER TABLE projects 
ADD CONSTRAINT projects_approval_status_check 
CHECK (
    approval_status IS NULL OR
    approval_status IN ('PENDING', 'SUBMITTED', 'APPROVED', 'REJECTED')
);

-- Step 4: For projects in new workflow, set approval_status to match workflow status
UPDATE projects 
SET approval_status = CASE
    WHEN approval_workflow_status = 'APPROVED' THEN 'APPROVED'
    WHEN approval_workflow_status LIKE 'REJECTED%' THEN 'REJECTED'
    WHEN approval_workflow_status IN ('PENDING_REVIEW', 'UNDER_REVIEW', 'REVIEWED', 'PENDING_FINAL_APPROVAL') THEN 'SUBMITTED'
    ELSE approval_status
END
WHERE approval_workflow_status IS NOT NULL;

-- Step 5: Verify the fix
SELECT approval_status, approval_workflow_status, COUNT(*) 
FROM projects 
GROUP BY approval_status, approval_workflow_status;

-- Step 6: Show projects that were updated
SELECT id, title, approval_status, approval_workflow_status, reviewed_by, reviewed_at
FROM projects
WHERE approval_workflow_status IS NOT NULL
ORDER BY id DESC
LIMIT 10;
