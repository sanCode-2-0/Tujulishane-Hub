-- Migration: Update user_documents table to support admin dashboard
-- Date: December 2024
-- Purpose: Update user_documents table to use User relationships and add missing fields for admin dashboard

-- Step 1: Rename user_id column to old_user_id (temporary)
ALTER TABLE user_documents ADD COLUMN old_user_id BIGINT;
UPDATE user_documents SET old_user_id = user_id;

-- Step 2: Drop the old user_id column
ALTER TABLE user_documents DROP COLUMN user_id;

-- Step 3: Add new user_id column as foreign key
ALTER TABLE user_documents ADD COLUMN user_id BIGINT;
ALTER TABLE user_documents ADD CONSTRAINT fk_user_documents_user
    FOREIGN KEY (user_id) REFERENCES users(id);

-- Step 4: Migrate data from old_user_id to user_id
UPDATE user_documents SET user_id = old_user_id;

-- Step 5: Drop the temporary column
ALTER TABLE user_documents DROP COLUMN old_user_id;

-- Step 6: Add the uploaded_by column
ALTER TABLE user_documents ADD COLUMN uploaded_by BIGINT;
ALTER TABLE user_documents ADD CONSTRAINT fk_user_documents_uploaded_by
    FOREIGN KEY (uploaded_by) REFERENCES users(id);

-- Step 7: Add the status column
ALTER TABLE user_documents ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE';

-- Step 8: Rename uploaded_at to upload_date
ALTER TABLE user_documents RENAME COLUMN uploaded_at TO upload_date;

-- Step 9: Add created_at column
ALTER TABLE user_documents ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Step 10: Update existing records to set default values
-- Set uploaded_by to the same as user_id (users upload their own documents)
UPDATE user_documents SET uploaded_by = user_id WHERE uploaded_by IS NULL;
-- Set status to ACTIVE for existing records
UPDATE user_documents SET status = 'ACTIVE' WHERE status IS NULL;
-- Set created_at to upload_date for existing records
UPDATE user_documents SET created_at = upload_date WHERE created_at IS NULL;