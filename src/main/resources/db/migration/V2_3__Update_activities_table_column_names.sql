-- V2_3__Update_activities_table_column_names.sql
-- Update column names in activities table to match Entity field names

ALTER TABLE activities 
CHANGE COLUMN name title VARCHAR(255) NOT NULL,
CHANGE COLUMN notes description TEXT,
CHANGE COLUMN start_time start_date DATETIME NOT NULL,
CHANGE COLUMN end_time end_date DATETIME NOT NULL;
