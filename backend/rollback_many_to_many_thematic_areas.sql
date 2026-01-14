-- Rollback Migration: Remove Many-to-Many Relationship for Reviewer Thematic Areas
-- Purpose: Revert to single thematic area per reviewer
-- Date: December 10, 2025

-- Drop the join table
DROP TABLE IF EXISTS reviewer_thematic_areas;

-- Note: This rollback does NOT restore data to the legacy single thematic_area column
-- Manual data restoration may be required if needed
