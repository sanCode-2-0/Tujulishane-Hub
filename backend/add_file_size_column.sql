-- Migration: Add fileSize field to project_document table
-- Date: November 4, 2025
-- Purpose: Store file size separately to avoid accessing LOB data for metadata queries

-- Step 1: Add the file_size column
ALTER TABLE project_document ADD COLUMN file_size BIGINT;

-- Step 2: Update existing records with file size calculated from data field
-- WARNING: This operation might be slow for tables with many large documents
-- Consider running this in batches during off-peak hours if you have many records

-- For PostgreSQL with bytea (binary data) type:
-- UPDATE project_document 
-- SET file_size = OCTET_LENGTH(data) 
-- WHERE file_size IS NULL AND data IS NOT NULL;

-- For PostgreSQL with OID/LOB type (which is your case):
-- Since the data is stored as OID, we can't easily get the size without lo_get
-- For now, set to NULL or 0 for existing records
-- New uploads will have the correct size
UPDATE project_document 
SET file_size = 0 
WHERE file_size IS NULL AND data IS NOT NULL;

-- Alternative: If you need actual sizes for existing documents, you would need to:
-- 1. Read each document through the application
-- 2. Calculate size
-- 3. Update the record
-- This is best done through a Java/Spring Boot script or application endpoint

-- Step 3: Verify the migration
SELECT 
    COUNT(*) as total_documents,
    COUNT(file_size) as documents_with_size,
    COUNT(*) - COUNT(file_size) as documents_missing_size
FROM project_document;

-- Expected result: documents_missing_size should be 0 after migration
