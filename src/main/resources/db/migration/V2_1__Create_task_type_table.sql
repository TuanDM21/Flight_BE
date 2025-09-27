-- V2_1__Create_task_type_table.sql
-- Creates task_type table and adds foreign key to task table

-- Task Type table
CREATE TABLE task_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    INDEX idx_task_type_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add task_type_id column to task table
ALTER TABLE task 
ADD COLUMN task_type_id INT DEFAULT NULL AFTER priority,
ADD FOREIGN KEY fk_task_type_id (task_type_id) REFERENCES task_type(id);

-- Insert default task types
INSERT INTO task_type (name) VALUES
('Kết Luận Giao Ban'),
('Công việc giao ban ngày'),
('Công việc theo dõi giám sát'),
('Xử lý văn bản'),
('Công việc ban giám đốc giao');

-- Add index for better query performance
CREATE INDEX idx_task_task_type_id ON task(task_type_id);
