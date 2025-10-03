-- V2_0__Insert_sample_data.sql
-- Inserts sample data for Airport Management System

-- Insert default roles (ordered by hierarchy: highest to lowest)
INSERT INTO roles (role_name, created_at, updated_at) VALUES
('SYSTEM_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),    -- Highest: Technical admin
('USER_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),      -- Business admin
('DIRECTOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),        -- Airport director
('VICE_DIRECTOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),   -- Vice director
('TEAM_LEAD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),       -- Team leader
('TEAM_VICE_LEAD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- Team vice leader
('UNIT_LEAD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),       -- Unit leader
('UNIT_VICE_LEAD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- Unit vice leader
('OFFICE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),          -- Office staff
('MEMBER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);          -- Lowest: Regular member


-- Insert default teams

INSERT INTO teams (team_name, created_at, updated_at) VALUES
('Ban Giám Đốc', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Đội Kỹ Thuật', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Văn Phòng', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Đội Phục Vụ Mặt Đất', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Đội An Ninh Hàng Không', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert default unit

INSERT INTO units (unit_name, team_id, description, created_at, updated_at) VALUES
('Tổ Vận Hành Trang Thiết Bị Nhà Ga', 2, 'Quản lý và vận hành trang thiết bị tại nhà ga', NOW(), NOW()),
('Tổ Vận Hành Trang Thiết Bị Khu Bay', 2, 'Quản lý và vận hành trang thiết bị khu bay', NOW(), NOW()),
('Tổ Phục Vụ Hành Khách - Cân Bằng Trọng Tải', 4, 'Hỗ trợ hành khách và cân bằng trọng tải', NOW(), NOW()),
('Tổ Vệ Sinh - Bốc Xếp', 4, 'Đảm bảo vệ sinh và bốc xếp hành lý', NOW(), NOW()),
('Tổ An Ninh Kiểm Soát', 5, 'Thực hiện kiểm soát an ninh', NOW(), NOW()),
('Tổ An Ninh Soi Chiếu', 5, 'Thực hiện soi chiếu an ninh', NOW(), NOW()),
('Tổ Điều Hành Sân Bay', 3, 'Điều phối hoạt động tại sân bay', NOW(), NOW()),
('Tổ Hành Chính - Tổng Hợp', 3, 'Hành chính và tổng hợp văn phòng', NOW(), NOW()),
('Tổ Kế Toán - Kế hoạch', 3, 'Kế toán và lập kế hoạch', NOW(), NOW());


-- Insert major Vietnamese airports
INSERT INTO airports (airport_code, airport_name, latitude, longitude, city, country, created_at, updated_at) VALUES
('HAN', 'Sân bay Quốc tế Nội Bài', 21.221192, 105.807178, 'Hà Nội', 'Vietnam', NOW(), NOW()),
('SGN', 'Sân bay Quốc tế Tân Sơn Nhất', 10.818963, 106.651857, 'TP Hồ Chí Minh', 'Vietnam', NOW(), NOW()),
('DAD', 'Sân bay Quốc tế Đà Nẵng', 16.043917, 108.199606, 'Đà Nẵng', 'Vietnam', NOW(), NOW()),
('CXR', 'Sân bay Cam Ranh', 12.006389, 109.219444, 'Khánh Hòa', 'Vietnam', NOW(), NOW()),
('HPH', 'Sân bay Cát Bi', 20.819440, 106.724970, 'Hải Phòng', 'Vietnam', NOW(), NOW()),
('VII', 'Sân bay Vinh', 18.737500, 105.671389, 'Nghệ An', 'Vietnam', NOW(), NOW()),
('HUI', 'Sân bay Phú Bài', 16.401500, 107.702500, 'Thừa Thiên Huế', 'Vietnam', NOW(), NOW()),
('VKG', 'Sân bay Rạch Giá', 9.958100, 105.132600, 'Kiên Giang', 'Vietnam', NOW(), NOW()),
('PQC', 'Sân bay Phú Quốc', 10.227008, 103.967181, 'Kiên Giang', 'Vietnam', NOW(), NOW()),
('DLI', 'Sân bay Liên Khương', 11.750000, 108.366700, 'Lâm Đồng', 'Vietnam', NOW(), NOW()),
('VCA', 'Sân bay Cần Thơ', 10.085120, 105.711922, 'Cần Thơ', 'Vietnam', NOW(), NOW()),
('BMV', 'Sân bay Buôn Ma Thuột', 12.668311, 108.120269, 'Đắk Lắk', 'Vietnam', NOW(), NOW()),
('TBB', 'Sân bay Tuy Hòa', 13.045600, 109.333600, 'Phú Yên', 'Vietnam', NOW(), NOW()),
('VDH', 'Sân bay Đồng Hới', 17.515000, 106.590278, 'Quảng Bình', 'Vietnam', NOW(), NOW());

-- Insert all users (admins first, then other users by hierarchy)
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at) VALUES
-- Admin Users (highest priority)
('System Administrator', 'system.admin@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 1, NULL, NULL, NOW(), NOW()), -- SYSTEM_ADMIN
('Business Administrator', 'user.admin@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 2, NULL, NULL, NOW(), NOW()), -- USER_ADMIN

-- Directors and Vice Directors
('Nguyễn Thành Nam', 'NamNT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 3, 1, NULL, NOW(), NOW()), -- DIRECTOR
('Đinh Hải Đức', 'DucDH@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 4, 1, NULL, NOW(), NOW()), -- VICE_DIRECTOR  
('Nguyễn Văn Thành', 'ThanhNV@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 4, 1, NULL, NOW(), NOW()), -- VICE_DIRECTOR

-- Team Leads
('Nguyễn Danh Tuyên', 'TuyenND@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 5, 2, NULL, NOW(), NOW()), -- TEAM_LEAD team 2
('Hoàng Ngọc Tuân', 'TuanHN@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 5, 3, NULL, NOW(), NOW()), -- TEAM_LEAD team 3
('Trần Thị Hà Giang', 'GiangTTH@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 5, 5, NULL, NOW(), NOW()), -- TEAM_LEAD team 5
('Phan Thanh Nam', 'NamPT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 5, 4, NULL, NOW(), NOW()), -- TEAM_LEAD team 4

-- Team Vice Leads
('Lâm Duy Hải', 'HaiLD@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 6, 2, NULL, NOW(), NOW()), -- TEAM_VICE_LEAD team 2
('Đoàn Hải', 'HaiD@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 6, 3, NULL, NOW(), NOW()), -- TEAM_VICE_LEAD team 3
('Nguyễn Quang Trường', 'TruongNQ@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 6, 3, NULL, NOW(), NOW()), -- TEAM_VICE_LEAD team 3
('Nguyễn Trung Kiên', 'KienNT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 6, 3, NULL, NOW(), NOW()), -- TEAM_VICE_LEAD team 3
('Nguyễn Trung Thành', 'ThanhNT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 6, 5, NULL, NOW(), NOW()), -- TEAM_VICE_LEAD team 5
('Nguyễn Xuân Hải', 'HaiNX@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 6, 5, NULL, NOW(), NOW()), -- TEAM_VICE_LEAD team 5
('Phan Thị Hải Yên', 'YenPTH@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 6, 4, NULL, NOW(), NOW()), -- TEAM_VICE_LEAD team 4
('Nguyễn Thị Hằng', 'HangNT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 6, 4, NULL, NOW(), NOW()), -- TEAM_VICE_LEAD team 4

-- Unit Leads and Vice Leads
('Trần Công Phượng', 'PhuongTC@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 7, 2, 2, NOW(), NOW()), -- UNIT_LEAD
('Hồ Minh Thắng', 'ThangHM@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 8, 2, 1, NOW(), NOW()), -- UNIT_VICE_LEAD
('Vũ Quốc Sơn', 'SonVQ@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 7, 4, 3, NOW(), NOW()), -- UNIT_LEAD
('Lương Thị Thanh Tâm', 'TamLTT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 8, 4, 3, NOW(), NOW()), -- UNIT_VICE_LEAD
('Nguyễn Duy Hạnh', 'HanhND@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 7, 4, 4, NOW(), NOW()), -- UNIT_LEAD
('Trần Thị Bích Lan', 'LanTTB@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 8, 4, 4, NOW(), NOW()), -- UNIT_VICE_LEAD
('Phan Xuân Nhân', 'NhanPX@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 7, 5, 5, NOW(), NOW()), -- UNIT_LEAD
('Trần Thanh Bình', 'BinhTT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 8, 5, 5, NOW(), NOW()), -- UNIT_VICE_LEAD
('Đỗ Thành Trung', 'TrungDT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 7, 5, 6, NOW(), NOW()), -- UNIT_LEAD
('Võ Huy Chương', 'ChuongVH@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 8, 5, 6, NOW(), NOW()), -- UNIT_VICE_LEAD
('Nguyễn Thị Phương Thảo', 'ThaoNTP@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', 7, 3, 8, NOW(), NOW()); -- UNIT_LEAD
