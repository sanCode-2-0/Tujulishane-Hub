-- Update project_theme_assignments check constraint to include all valid theme codes
-- This fixes the constraint violation error when ADV_SBC and other themes are used

-- Drop the existing constraint if it exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'project_theme_assignments_project_theme_check'
        AND conrelid = 'project_theme_assignments'::regclass
    ) THEN
        ALTER TABLE project_theme_assignments DROP CONSTRAINT project_theme_assignments_project_theme_check;
    END IF;
END $$;

-- Add the updated constraint with all valid theme codes from the ProjectTheme enum
ALTER TABLE project_theme_assignments
ADD CONSTRAINT project_theme_assignments_project_theme_check
CHECK (project_theme IN (
    'GBV',
    'AYPSRH',
    'MNH',
    'FP',
    'CH',
    'AH',
    'ADV_SBC',
    'MONITORING_EVALUATION',
    'RESEARCH_LEARNING'
));

-- Verify the constraint was created
SELECT conname, pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conname = 'project_theme_assignments_project_theme_check';