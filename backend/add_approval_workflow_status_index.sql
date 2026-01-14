-- Add index on approval_workflow_status field for improved query performance
-- This index optimizes filtering projects by approval status, especially for public views
-- that need to hide rejected/non-approved projects

-- Check if index already exists before creating
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_indexes
        WHERE schemaname = 'public'
        AND tablename = 'projects'
        AND indexname = 'idx_projects_approval_workflow_status'
    ) THEN
        CREATE INDEX idx_projects_approval_workflow_status 
        ON projects(approval_workflow_status);
        
        RAISE NOTICE 'Index idx_projects_approval_workflow_status created successfully';
    ELSE
        RAISE NOTICE 'Index idx_projects_approval_workflow_status already exists';
    END IF;
END
$$;

-- Add composite index for common query patterns (status + created_at for sorting)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_indexes
        WHERE schemaname = 'public'
        AND tablename = 'projects'
        AND indexname = 'idx_projects_status_created_at'
    ) THEN
        CREATE INDEX idx_projects_status_created_at 
        ON projects(approval_workflow_status, created_at DESC);
        
        RAISE NOTICE 'Index idx_projects_status_created_at created successfully';
    ELSE
        RAISE NOTICE 'Index idx_projects_status_created_at already exists';
    END IF;
END
$$;

-- Verify indexes were created
SELECT 
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'projects'
AND indexname LIKE '%approval_workflow_status%'
ORDER BY indexname;
