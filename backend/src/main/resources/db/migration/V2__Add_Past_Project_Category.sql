-- Add PAST_PROJECT to the project_category CHECK constraint
-- This migration updates the constraint to allow PAST_PROJECT alongside existing categories

-- Drop the existing constraint if it exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'projects_project_category_check' 
        AND conrelid = 'projects'::regclass
    ) THEN
        ALTER TABLE projects DROP CONSTRAINT projects_project_category_check;
    END IF;
END $$;

-- Add the updated constraint with all four categories
ALTER TABLE projects 
ADD CONSTRAINT projects_project_category_check 
CHECK (project_category IN ('IMPLEMENTING', 'RESEARCH', 'PRIORITY', 'PAST_PROJECT'));
