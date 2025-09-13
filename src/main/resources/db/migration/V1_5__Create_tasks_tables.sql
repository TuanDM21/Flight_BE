-- V1_5__Create_tasks_tables.sql
-- Creates task management tables

-- Task table (singular name to match entity)
CREATE TABLE task (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    instructions TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    status ENUM('OPEN', 'IN_PROGRESS', 'COMPLETED', 'OVERDUE') DEFAULT 'OPEN',
    priority ENUM('LOW', 'NORMAL', 'HIGH', 'URGENT') DEFAULT 'NORMAL',
    parent_id INT,
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (parent_id) REFERENCES task(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Assignment table (matches Assignment entity)
CREATE TABLE assignment (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    recipient_type VARCHAR(20) NOT NULL,
    recipient_id INT NOT NULL,
    assigned_by INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    completed_by INT,
    status ENUM('WORKING', 'DONE', 'CANCELLED', 'OVERDUE') DEFAULT 'WORKING',
    note TEXT,
    FOREIGN KEY (task_id) REFERENCES task(id),
    FOREIGN KEY (assigned_by) REFERENCES users(id),
    FOREIGN KEY (completed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Assignment comment history table
CREATE TABLE assignment_comment_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Attachment table
CREATE TABLE attachment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT,
    uploaded_by_user_id INT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (task_id) REFERENCES task(id),
    FOREIGN KEY (uploaded_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes
CREATE INDEX idx_task_status ON task(status);
CREATE INDEX idx_task_created_by ON task(created_by);
CREATE INDEX idx_assignment_status ON assignment(status);
CREATE INDEX idx_assignment_task_id ON assignment(task_id);
CREATE INDEX idx_attachment_task_id ON attachment(task_id);
