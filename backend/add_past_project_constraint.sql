-- Manual migration script to add PAST_PROJECT support
-- Run this on your production database to fix the constraint issue

-- Step 1: Check current constraint
SELECT conname, pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conrelid = 'projects'::regclass 
AND contype = 'c';

-- Step 2: Drop the old constraint if it exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'projects_project_category_check' 
        AND conrelid = 'projects'::regclass
    ) THEN
        ALTER TABLE projects DROP CONSTRAINT projects_project_category_check;
        RAISE NOTICE 'Dropped existing constraint projects_project_category_check';
    ELSE
        RAISE NOTICE 'Constraint projects_project_category_check does not exist';
    END IF;
END $$;

-- Step 3: Add the updated constraint with all four categories
ALTER TABLE projects 
ADD CONSTRAINT projects_project_category_check 
CHECK (project_category IN ('IMPLEMENTING', 'RESEARCH', 'PRIORITY', 'PAST_PROJECT'));

-- Step 4: Verify the new constraint
SELECT conname, pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conrelid = 'projects'::regclass 
AND contype = 'c';

-- Success message
DO $$
BEGIN
    RAISE NOTICE 'Migration completed successfully! PAST_PROJECT is now supported.';
END $$;
