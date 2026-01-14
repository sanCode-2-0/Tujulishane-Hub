-- Rollback script to remove NOT NULL constraints for mandatory project fields
-- Story 6.2: Mandatory Form Fields - Rollback
-- Date: 2025-12-11

-- Start transaction
BEGIN;

-- Remove NOT NULL constraints from projects table
ALTER TABLE projects 
    ALTER COLUMN project_category DROP NOT NULL,
    ALTER COLUMN start_date DROP NOT NULL,
    ALTER COLUMN activity_type DROP NOT NULL,
    ALTER COLUMN contact_person_name DROP NOT NULL,
    ALTER COLUMN contact_person_role DROP NOT NULL,
    ALTER COLUMN objectives DROP NOT NULL,
    ALTER COLUMN budget DROP NOT NULL;

-- Remove NOT NULL constraints from project_locations table
ALTER TABLE project_locations
    ALTER COLUMN latitude DROP NOT NULL,
    ALTER COLUMN longitude DROP NOT NULL;

-- Verify constraints were removed successfully
SELECT 
    column_name, 
    is_nullable, 
    data_type 
FROM information_schema.columns 
WHERE table_name = 'projects' 
    AND column_name IN (
        'project_category', 
        'start_date', 
        'activity_type', 
        'contact_person_name', 
        'contact_person_role', 
        'objectives', 
        'budget'
    )
ORDER BY column_name;

SELECT 
    column_name, 
    is_nullable, 
    data_type 
FROM information_schema.columns 
WHERE table_name = 'project_locations' 
    AND column_name IN ('latitude', 'longitude')
ORDER BY column_name;

-- Commit transaction
COMMIT;

-- Rollback command (uncomment if needed)
-- ROLLBACK;
