-- Check current constraint on projects table
SELECT conname, pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conrelid = 'projects'::regclass 
AND contype = 'c';

-- Check all columns in projects table
SELECT column_name, data_type, column_default
FROM information_schema.columns
WHERE table_name = 'projects'
ORDER BY ordinal_position;

-- Check current approval_status values
SELECT DISTINCT approval_status, approval_workflow_status, COUNT(*) 
FROM projects 
GROUP BY approval_status, approval_workflow_status;
