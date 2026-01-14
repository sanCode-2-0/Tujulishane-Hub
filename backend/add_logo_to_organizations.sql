-- Add logo storage columns to organizations table
ALTER TABLE organizations ADD COLUMN logo_data LONGBLOB COMMENT 'Binary logo image data';
ALTER TABLE organizations ADD COLUMN logo_content_type VARCHAR(100) COMMENT 'MIME type of the logo (e.g., image/jpeg, image/png)';

-- Create index on logo_content_type for queries
CREATE INDEX idx_logo_content_type ON organizations(logo_content_type);

-- Commit changes
COMMIT;
