-- Migration: Add new fields to project_document table for admin dashboard
-- Date: December 2024
-- Purpose: Add uploaded_by, status, upload_date, created_at fields to support admin dashboard functionality

-- Step 1: Add the uploaded_by column (foreign key to user table)
ALTER TABLE project_document ADD COLUMN uploaded_by BIGINT;
ALTER TABLE project_document ADD CONSTRAINT fk_project_document_uploaded_by
    FOREIGN KEY (uploaded_by) REFERENCES "user"(id);

-- Step 2: Add the status column
ALTER TABLE project_document ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE';

-- Step 3: Add the upload_date column
ALTER TABLE project_document ADD COLUMN upload_date TIMESTAMP;

-- Step 4: Add the created_at column
ALTER TABLE project_document ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Step 5: Update existing records to set default values
-- Set uploaded_by to NULL for existing records (will show as "Unknown" in UI)
-- Set status to ACTIVE for existing records
-- Set upload_date and created_at to current timestamp for existing records
UPDATE project_document SET
    status = 'ACTIVE',
    upload_date = CURRENT_TIMESTAMP,
    created_at = CURRENT_TIMESTAMP
WHERE status IS NULL;