-- V1_7__Create_activities_notifications_tables.sql
-- Creates activities and notifications tables

-- Activities table (matches Activity entity)
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

-- Activity participants table (matches ActivityParticipant entity)
CREATE TABLE activity_participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    participant_type VARCHAR(20) NOT NULL,
    participant_id BIGINT NOT NULL,
    FOREIGN KEY (activity_id) REFERENCES activities(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Notification table (matches Notification entity)
CREATE TABLE notification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    type VARCHAR(50),
    title VARCHAR(255),
    content TEXT,
    related_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for better query performance
CREATE INDEX idx_activities_start_date ON activities(start_date);
CREATE INDEX idx_activity_participants_activity ON activity_participants(activity_id);
CREATE INDEX idx_activity_participants_type ON activity_participants(participant_type, participant_id);
CREATE INDEX idx_notification_user_id ON notification(user_id);
CREATE INDEX idx_notification_type ON notification(type);
CREATE INDEX idx_notification_is_read ON notification(is_read);
