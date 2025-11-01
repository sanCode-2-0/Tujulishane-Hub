-- Add project_no column to projects table
ALTER TABLE projects ADD COLUMN project_no VARCHAR(10) UNIQUE;

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