-- Migration to recreate activities table with new field names
-- Drop old table and create new one with updated field names:
-- name -> title, notes -> description, start_time -> start_date, end_time -> end_date

-- Drop old activities table (and related tables due to foreign key constraints)
DROP TABLE IF EXISTS activity_participants;
DROP TABLE IF EXISTS activities;

-- Create activities table with new field names
CREATE TABLE activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Recreate activity participants table
CREATE TABLE activity_participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    participant_type VARCHAR(20) NOT NULL,
    participant_id BIGINT NOT NULL,
    FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Recreate indexes for better query performance
CREATE INDEX idx_activities_start_date ON activities(start_date);
CREATE INDEX idx_activity_participants_activity ON activity_participants(activity_id);
CREATE INDEX idx_activity_participants_type ON activity_participants(participant_type, participant_id);
