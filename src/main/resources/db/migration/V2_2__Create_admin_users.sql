-- V2_2__Create_admin_users.sql
-- Create admin users for system and user management

-- Insert System Admin (for developers/IT support)
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'System Administrator',
    'system.admin@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', -- password: admin123
    r.id,
    NULL, -- System admin không thuộc team nào
    NULL, -- System admin không thuộc unit nào
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'SYSTEM_ADMIN';

-- Insert User Admin (for airport business management)
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Business Administrator',
    'user.admin@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', -- password: admin123
    r.id,
    NULL, -- User admin có thể quản lý tất cả teams
    NULL, -- User admin có thể quản lý tất cả units
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'USER_ADMIN';
