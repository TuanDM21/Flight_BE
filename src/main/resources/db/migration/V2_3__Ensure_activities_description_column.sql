-- V2_2__Ensure_activities_description_column.sql
-- Fix schema validation: ensure description column exists in activities table
-- This migration is safe for CI/CD - only adds column if missing

-- Add description column if it doesn't exist (MariaDB/MySQL syntax)
ALTER TABLE activities 
ADD COLUMN IF NOT EXISTS description TEXT 
AFTER pinned;
