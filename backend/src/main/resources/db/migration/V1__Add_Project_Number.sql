-- Add project_no column to projects table (if it doesn't exist)
-- This migration is idempotent - safe to run even if column already exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'projects' AND column_name = 'project_no'
    ) THEN
        ALTER TABLE projects ADD COLUMN project_no VARCHAR(10) UNIQUE;
    END IF;
END $$;

-- Populate existing projects with generated project numbers
-- Using CTE to assign sequential numbers starting from P-0001
WITH numbered_projects AS (
    SELECT id, 'P-' || LPAD(ROW_NUMBER() OVER (ORDER BY id)::TEXT, 4, '0') AS new_no
    FROM projects
    WHERE project_no IS NULL
)
UPDATE projects
SET project_no = numbered_projects.new_no
FROM numbered_projects
WHERE projects.id = numbered_projects.id;