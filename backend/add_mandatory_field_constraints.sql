-- Migration script to add NOT NULL constraints for mandatory project fields
-- Story 6.2: Mandatory Form Fields
-- Date: 2025-12-11

-- Note: Before running this script, ensure all existing records have valid data for these fields
-- If there are NULL values, they must be updated with default values first

-- Start transaction
BEGIN;

-- Add NOT NULL constraints to projects table
ALTER TABLE projects 
    ALTER COLUMN project_category SET NOT NULL,
    ALTER COLUMN start_date SET NOT NULL,
    ALTER COLUMN activity_type SET NOT NULL,
    ALTER COLUMN contact_person_name SET NOT NULL,
    ALTER COLUMN contact_person_role SET NOT NULL,
    ALTER COLUMN objectives SET NOT NULL,
    ALTER COLUMN budget SET NOT NULL;

-- Add NOT NULL constraints to project_locations table
ALTER TABLE project_locations
    ALTER COLUMN latitude SET NOT NULL,
    ALTER COLUMN longitude SET NOT NULL;

-- Verify constraints were added successfully
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
