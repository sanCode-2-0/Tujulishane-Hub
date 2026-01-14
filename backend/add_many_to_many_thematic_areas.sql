-- Migration: Add Many-to-Many Relationship for Reviewer Thematic Areas
-- Purpose: Allow one reviewer to be assigned to multiple thematic areas
-- Date: December 10, 2025

-- Create the join table for reviewer-thematic area assignments
CREATE TABLE IF NOT EXISTS reviewer_thematic_areas (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    thematic_area VARCHAR(50) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT,
    CONSTRAINT fk_reviewer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_assigned_by_user FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT unique_user_theme UNIQUE (user_id, thematic_area)
);

-- Create index for faster lookups
CREATE INDEX idx_reviewer_thematic_areas_user_id ON reviewer_thematic_areas(user_id);
CREATE INDEX idx_reviewer_thematic_areas_thematic_area ON reviewer_thematic_areas(thematic_area);

-- Migrate existing single thematic area assignments to the new table
-- This preserves backward compatibility
INSERT INTO reviewer_thematic_areas (user_id, thematic_area, assigned_at)
SELECT id, thematic_area, CURRENT_TIMESTAMP
FROM users
WHERE role = 'SUPER_ADMIN_REVIEWER' 
  AND thematic_area IS NOT NULL
ON CONFLICT (user_id, thematic_area) DO NOTHING;

-- Add comment to the table
COMMENT ON TABLE reviewer_thematic_areas IS 'Many-to-many relationship between reviewers and thematic areas';
COMMENT ON COLUMN reviewer_thematic_areas.user_id IS 'Reference to the reviewer user';
COMMENT ON COLUMN reviewer_thematic_areas.thematic_area IS 'Thematic area code (GBV, AYPSRH, MNH, FP, CH, AH, etc.)';
COMMENT ON COLUMN reviewer_thematic_areas.assigned_at IS 'Timestamp when the assignment was made';
COMMENT ON COLUMN reviewer_thematic_areas.assigned_by IS 'User ID of the admin who made the assignment';

-- Note: The legacy 'thematic_area' column in the users table is kept for backward compatibility
-- It will be gradually phased out as the system migrates to the many-to-many relationship
