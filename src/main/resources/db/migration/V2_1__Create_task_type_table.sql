-- V2_1__Create_task_type_table.sql
-- Creates task_type table and adds foreign key to task table

-- Task Type table
CREATE TABLE IF NOT EXISTS task_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    INDEX idx_task_type_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add task_type_id column to task table if it doesn't exist
ALTER TABLE task
ADD COLUMN IF NOT EXISTS task_type_id INT DEFAULT NULL AFTER priority;

-- Add foreign key constraint if it doesn't exist
SET @fk_exists := (SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
                   WHERE TABLE_SCHEMA = 'airport_db'
                   AND TABLE_NAME = 'task'

                   AND CONSTRAINT_NAME = 'fk_task_type_id');

SET @sql = IF(@fk_exists = 0,
    'ALTER TABLE task ADD FOREIGN KEY fk_task_type_id (task_type_id) REFERENCES task_type(id);',
    'SELECT "Foreign key fk_task_type_id already exists" as message;'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Insert default task types if they don't exist
INSERT IGNORE INTO task_type (name) VALUES
('Kết Luận Giao Ban'),
('Công việc giao ban ngày'),
('Công việc theo dõi giám sát'),
('Xử lý văn bản'),
('Công việc ban giám đốc giao');

-- Add index for better query performance if it doesn't exist
SET @index_exists := (SELECT COUNT(*) FROM information_schema.STATISTICS
                      WHERE TABLE_SCHEMA = 'airport_db'
                      AND TABLE_NAME = 'task'
                      AND INDEX_NAME = 'idx_task_task_type_id');

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_task_task_type_id ON task(task_type_id);',
    'SELECT "Index idx_task_task_type_id already exists" as message;'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
