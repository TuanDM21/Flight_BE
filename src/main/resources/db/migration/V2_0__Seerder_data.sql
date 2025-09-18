-- V2_0__Insert_sample_data.sql
-- Inserts sample data for Airport Management System

-- Insert default roles
INSERT INTO roles (role_name, created_at, updated_at) VALUES
('ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MEMBER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('OFFICE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('UNIT_VICE_LEAD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('UNIT_LEAD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TEAM_VICE_LEAD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TEAM_LEAD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('VICE_DIRECTOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DIRECTOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


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

-- Insert default admin user (password will be hashed in application)

-- Insert User 
-- Nguyễn Thành Nam - Director
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Nguyễn Thành Nam',
    'NamNT@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    1,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'DIRECTOR';

-- Đinh Hải Đức - Director
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Đinh Hải Đức',
    'DucDH@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    1,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'VICE_DIRECTOR';

-- Nguyễn Văn Thành - Vice Director
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Nguyễn Văn Thành',
    'ThanhNV@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    1,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'VICE_DIRECTOR';

-- Nguyễn Danh Tuyên - TEAM_LEAD team 2
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Nguyễn Danh Tuyên',
    'TuyenND@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    2,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_LEAD';

-- Lâm Duy Hải - TEAM_VICE_LEAD team 2
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Lâm Duy Hải',
    'HaiLD@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    2,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_VICE_LEAD';

-- Hoàng Ngọc Tuân - TEAM_LEAD team 3
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Hoàng Ngọc Tuân',
    'TuanHN@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    3,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_LEAD';

-- Đoàn Hải - TEAM_VICE_LEAD team 3
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Đoàn Hải',
    'HaiD@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    3,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_VICE_LEAD';

-- Nguyễn Quang Trường - TEAM_VICE_LEAD team 3
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Nguyễn Quang Trường',
    'TruongNQ@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    3,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_VICE_LEAD';

-- Nguyễn Trung Kiên - TEAM_VICE_LEAD team 3
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Nguyễn Trung Kiên',
    'KienNT@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    3,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_VICE_LEAD';

-- Trần Thị Hà Giang - TEAM_LEAD team 5
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Trần Thị Hà Giang',
    'GiangTTH@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    5,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_LEAD';

-- Nguyễn Trung Thành - TEAM_VICE_LEAD team 5
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Nguyễn Trung Thành',
    'ThanhNT@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    5,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_VICE_LEAD';

-- Nguyễn Xuân Hải - TEAM_VICE_LEAD team 5
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Nguyễn Xuân Hải',
    'HaiNX@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    5,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_VICE_LEAD';

-- Phan Thanh Nam - TEAM_LEAD team 4
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Phan Thanh Nam',
    'NamPT@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    4,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_LEAD';

-- Phan Thị Hải Yên - TEAM_VICE_LEAD team 4
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Phan Thị Hải Yên',
    'YenPTH@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    4,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_VICE_LEAD';

-- Nguyễn Thị Hằng - TEAM_VICE_LEAD team 4
INSERT INTO users (name, email, password, role_id, team_id, unit_id, created_at, updated_at)
SELECT
    'Nguyễn Thị Hằng',
    'HangNT@vdh.com',
    '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K',
    r.id,
    4,
    NULL,
    NOW(),
    NOW()
FROM roles r WHERE r.role_name = 'TEAM_VICE_LEAD';

INSERT INTO `airportdb`.`users`
(`name`, `email`, `password`, `expo_push_token`, `role_id`, `team_id`, `unit_id`, `created_at`, `updated_at`)
VALUES
-- Trần Công Phượng - Tổ trưởng
('Trần Công Phượng', 'PhuongTC@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 5, 2, 2, NOW(), NOW()),

-- Hồ Minh Thắng - Tổ phó
('Hồ Minh Thắng', 'ThangHM@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 4, 2, 1, NOW(), NOW()),

-- Vũ Quốc Sơn - Tổ trưởng
('Vũ Quốc Sơn', 'SonVQ@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 5, 4, 3, NOW(), NOW()),

-- Lương Thị Thanh Tâm - Tổ phó
('Lương Thị Thanh Tâm', 'TamLTT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 4, 4, 3, NOW(), NOW()),

-- Nguyễn Duy Hạnh - Tổ trưởng
('Nguyễn Duy Hạnh', 'HanhND@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 5, 4, 4, NOW(), NOW()),

-- Trần Thị Bích Lan - Tổ phó
('Trần Thị Bích Lan', 'LanTTB@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 4, 4, 4, NOW(), NOW()),

-- Phan Xuân Nhân - Tổ trưởng
('Phan Xuân Nhân', 'NhanPX@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 5, 5, 5, NOW(), NOW()),

-- Trần Thanh Bình - Tổ phó
('Trần Thanh Bình', 'BinhTT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 4, 5, 5, NOW(), NOW()),

-- Đỗ Thành Trung - Tổ trưởng
('Đỗ Thành Trung', 'TrungDT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 5, 5, 6, NOW(), NOW()),

-- Võ Huy Chương - Tổ phó
('Võ Huy Chương', 'ChuongVH@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 4, 5, 6, NOW(), NOW()),

-- Nguyễn Thị Phương Thảo - Tổ trưởng
('Nguyễn Thị Phương Thảo', 'ThaoNTP@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 5, 3, 8, NOW(), NOW());


INSERT INTO `airportdb`.`users`
(`name`, `email`, `password`, `expo_push_token`, `role_id`, `team_id`, `unit_id`, `created_at`, `updated_at`)
VALUES
('Đỗ Minh Tuấn', 'TuanDM@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 2, 2, 1, NOW(), NOW()),
('Nguyễn Danh Quang', 'QuangND@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 2, 2, 1, NOW(), NOW()),
('Nguyễn Tiến Đạt', 'DatNT@vdh.com', '$2a$10$c93eqLHOQjIhMzB9xR6yauH1KNE8aWK2Hez.pnCoq5noxZVjKgD0K', NULL, 2, 2, 1, NOW(), NOW());

-- -- Insert default shifts for different teams
-- INSERT INTO shifts (shift_code, start_time, end_time, location, description, team_id, created_at, updated_at)
-- SELECT
--     'SEC-DAY',
--     '06:00:00',
--     '14:00:00',
--     'Terminal 1',
--     'Ca trực an ninh ban ngày',
--     t.id,
--     NOW(),
--     NOW()
-- FROM teams t WHERE t.team_name = 'Security Team';

-- INSERT INTO shifts (shift_code, start_time, end_time, location, description, team_id, created_at, updated_at)
-- SELECT
--     'SEC-NIGHT',
--     '18:00:00',
--     '02:00:00',
--     'Terminal 1',
--     'Ca trực an ninh ban đêm',
--     t.id,
--     NOW(),
--     NOW()
-- FROM teams t WHERE t.team_name = 'Security Team';

-- INSERT INTO shifts (shift_code, start_time, end_time, location, description, team_id, created_at, updated_at)
-- SELECT
--     'GRD-MORNING',
--     '05:00:00',
--     '13:00:00',
--     'Sân bay',
--     'Ca sáng điều hành mặt đất',
--     t.id,
--     NOW(),
--     NOW()
-- FROM teams t WHERE t.team_name = 'Ground Operations';

-- INSERT INTO shifts (shift_code, start_time, end_time, location, description, team_id, created_at, updated_at)
-- SELECT
--     'GRD-EVENING',
--     '13:00:00',
--     '21:00:00',
--     'Sân bay',
--     'Ca chiều điều hành mặt đất',
--     t.id,
--     NOW(),
--     NOW()
-- FROM teams t WHERE t.team_name = 'Ground Operations';

-- INSERT INTO shifts (shift_code, start_time, end_time, location, description, team_id, created_at, updated_at)
-- SELECT
--     'PASS-DAY',
--     '06:00:00',
--     '18:00:00',
--     'Terminal 1 & 2',
--     'Ca phục vụ hành khách ban ngày',
--     t.id,
--     NOW(),
--     NOW()
-- FROM teams t WHERE t.team_name = 'Passenger Services';

-- Insert sample flights
-- INSERT INTO flights (flight_number, departure_airport_id, arrival_airport_id, departure_time, arrival_time, flight_date, status, created_at, updated_at)
-- SELECT
--     'VN101',
--     dep.id,
--     arr.id,
--     '06:00:00',
--     '08:30:00',
--     CURDATE(),
--     'SCHEDULED',
--     NOW(),
--     NOW()
-- FROM airports dep, airports arr
-- WHERE dep.airport_code = 'HAN' AND arr.airport_code = 'SGN';

-- INSERT INTO flights (flight_number, departure_airport_id, arrival_airport_id, departure_time, arrival_time, flight_date, status, created_at, updated_at)
-- SELECT
--     'VN102',
--     dep.id,
--     arr.id,
--     '09:00:00',
--     '11:30:00',
--     CURDATE(),
--     'SCHEDULED',
--     NOW(),
--     NOW()
-- FROM airports dep, airports arr
-- WHERE dep.airport_code = 'SGN' AND arr.airport_code = 'HAN';

-- INSERT INTO flights (flight_number, departure_airport_id, arrival_airport_id, departure_time, arrival_time, flight_date, status, created_at, updated_at)
-- SELECT
--     'VN201',
--     dep.id,
--     arr.id,
--     '10:00:00',
--     '11:15:00',
--     CURDATE(),
--     'SCHEDULED',
--     NOW(),
--     NOW()
-- FROM airports dep, airports arr
-- WHERE dep.airport_code = 'HAN' AND arr.airport_code = 'DAD';

-- INSERT INTO flights (flight_number, departure_airport_id, arrival_airport_id, departure_time, arrival_time, flight_date, status, created_at, updated_at)
-- SELECT
--     'VN202',
--     dep.id,
--     arr.id,
--     '12:00:00',
--     '13:15:00',
--     CURDATE(),
--     'SCHEDULED',
--     NOW(),
--     NOW()
-- FROM airports dep, airports arr
-- WHERE dep.airport_code = 'DAD' AND arr.airport_code = 'HAN';

-- -- Insert sample tasks
-- INSERT INTO task (title, content, instructions, notes, created_by, deleted, status, priority, parent_id, created_at, updated_at)
-- SELECT
--     'Kiểm tra hệ thống an ninh Terminal 1',
--     'Thực hiện kiểm tra toàn diện hệ thống an ninh tại Terminal 1 bao gồm camera, cửa ra vào, và thiết bị quét',
--     '1. Kiểm tra tất cả camera hoạt động\n2. Test cửa ra vào tự động\n3. Kiểm tra thiết bị quét hành lý\n4. Báo cáo các vấn đề phát hiện',
--     'Ưu tiên kiểm tra khu vực check-in và boarding gate',
--     u.id,
--     FALSE,
--     'OPEN',
--     'HIGH',
--     NULL,
--     NOW(),
--     NOW()
-- FROM users u WHERE u.email = 'admin@airport.com';

-- INSERT INTO task (title, content, instructions, notes, created_by, deleted, status, priority, parent_id, created_at, updated_at)
-- SELECT
--     'Cập nhật thông tin chuyến bay mùa đông',
--     'Cập nhật lịch chuyến bay cho mùa đông và thông báo cho các bộ phận liên quan',
--     '1. Tải file lịch bay mới từ hãng\n2. Import vào hệ thống\n3. Thông báo cho Ground Operations\n4. Cập nhật thông tin hiển thị',
--     'Deadline: cuối tuần này',
--     u.id,
--     FALSE,
--     'OPEN',
--     'NORMAL',
--     NULL,
--     NOW(),
--     NOW()
-- FROM users u WHERE u.email = 'manager@airport.com';

-- -- Insert sample activities
-- INSERT INTO activities (name, notes, start_time, end_time, location, pinned, created_at, updated_at)
-- VALUES
--     ('Họp định kỳ ban quản lý',
--      'Họp báo cáo tình hình hoạt động tuần',
--      DATE_ADD(NOW(), INTERVAL 1 DAY),
--      DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 2 HOUR),
--      'Phòng họp tầng 3',
--      FALSE,
--      NOW(),
--      NOW());

-- INSERT INTO activities (name, notes, start_time, end_time, location, pinned, created_at, updated_at)
-- VALUES
--     ('Đào tạo an ninh hàng không',
--      'Khóa đào tạo định kỳ về an ninh hàng không cho nhân viên',
--      DATE_ADD(NOW(), INTERVAL 5 DAY),
--      DATE_ADD(DATE_ADD(NOW(), INTERVAL 5 DAY), INTERVAL 4 HOUR),
--      'Phòng đào tạo',
--      FALSE,
--      NOW(),
--      NOW());

-- -- Insert sample assignments
-- INSERT INTO assignment (task_id, recipient_type, recipient_id, assigned_by, assigned_at, due_at, status, note)
-- SELECT
--     t.id,
--     'USER',
--     u.id,
--     admin.id,
--     NOW(),
--     DATE_ADD(NOW(), INTERVAL 7 DAY),
--     'WORKING',
--     'Công việc quan trọng, cần hoàn thành đúng hạn'
-- FROM task t, users u, users admin
-- WHERE t.title = 'Kiểm tra hệ thống an ninh Terminal 1'
-- AND u.email = 'manager@airport.com'
-- AND admin.email = 'admin@airport.com';

-- INSERT INTO assignment (task_id, recipient_type, recipient_id, assigned_by, assigned_at, due_at, status, note)
-- SELECT
--     t.id,
--     'TEAM',
--     team.id,
--     admin.id,
--     NOW(),
--     DATE_ADD(NOW(), INTERVAL 3 DAY),
--     'WORKING',
--     'Cần phối hợp với IT để hoàn thành'
-- FROM task t, teams team, users admin
-- WHERE t.title = 'Cập nhật thông tin chuyến bay mùa đông'
-- AND team.team_name = 'Ground Operations'
-- AND admin.email = 'admin@airport.com';

-- -- Insert user permissions
-- INSERT INTO user_permissions (user_id, permission_code, value, created_at, updated_at)
-- SELECT
--     u.id,
--     'MANAGE_USERS',
--     TRUE,
--     NOW(),
--     NOW()
-- FROM users u WHERE u.email = 'admin@airport.com';

-- INSERT INTO user_permissions (user_id, permission_code, value, created_at, updated_at)
-- SELECT
--     u.id,
--     'MANAGE_FLIGHTS',
--     TRUE,
--     NOW(),
--     NOW()
-- FROM users u WHERE u.email = 'admin@airport.com';

-- INSERT INTO user_permissions (user_id, permission_code, value, created_at, updated_at)
-- SELECT
--     u.id,
--     'VIEW_REPORTS',
--     TRUE,
--     NOW(),
--     NOW()
-- FROM users u WHERE u.email = 'manager@airport.com';

-- INSERT INTO user_permissions (user_id, permission_code, value, created_at, updated_at)
-- SELECT
--     u.id,
--     'MANAGE_TASKS',
--     TRUE,
--     NOW(),
--     NOW()
-- FROM users u WHERE u.email = 'manager@airport.com';

-- -- Insert user shifts
-- INSERT INTO user_shifts (user_id, shift_date, shift_id, created_at, updated_at)
-- SELECT
--     u.id,
--     CURDATE(),
--     s.id,
--     NOW(),
--     NOW()
-- FROM users u, shifts s
-- WHERE u.email = 'manager@airport.com'
-- AND s.shift_code = 'SEC-DAY';

-- INSERT INTO user_shifts (user_id, shift_date, shift_id, created_at, updated_at)
-- SELECT
--     u.id,
--     DATE_ADD(CURDATE(), INTERVAL 1 DAY),
--     s.id,
--     NOW(),
--     NOW()
-- FROM users u, shifts s
-- WHERE u.email = 'manager@airport.com'
-- AND s.shift_code = 'GRD-MORNING';
