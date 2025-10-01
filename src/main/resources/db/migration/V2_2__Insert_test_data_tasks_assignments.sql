-- V2.2: Insert test data for task and assignment tables
-- This file creates sample data for testing report functionality

-- Insert sample tasks (50 tasks) - using correct table name 'task' and available columns
INSERT INTO task (title, content, instructions, status, priority, task_type_id, created_by, created_at, updated_at) VALUES
-- Công việc bảo trì máy bay (task_type_id = 1, created_by = 1)
('Kiểm tra động cơ máy bay VN101', 'Thực hiện kiểm tra định kỳ động cơ máy bay', 'Sử dụng checklist A320, kiểm tra từng bộ phận theo quy trình', 'OPEN', 'HIGH', 1, 1, '2024-09-01 08:00:00', '2024-09-01 08:00:00'),
('Bảo dưỡng hệ thống thủy lực', 'Thay dầu thủy lực cho máy bay VN102', 'Theo hướng dẫn kỹ thuật Boeing 737', 'IN_PROGRESS', 'NORMAL', 1, 1, '2024-09-01 09:00:00', '2024-09-01 10:30:00'),
('Kiểm tra hệ thống điện tử', 'Kiểm tra các hệ thống điện tử trên cabin', 'Sử dụng thiết bị đo chuyên dụng', 'COMPLETED', 'NORMAL', 1, 1, '2024-09-01 10:00:00', '2024-09-01 14:00:00'),
('Thay lốp máy bay', 'Thay bộ lốp chính cho máy bay VN103', 'Kiểm tra áp suất và độ mòn trước khi thay', 'OPEN', 'URGENT', 1, 1, '2024-09-02 07:30:00', '2024-09-02 07:30:00'),
('Kiểm tra hệ thống phanh', 'Kiểm tra và bảo dưỡng hệ thống phanh', 'Test độ nhạy phanh và thay dầu phanh', 'IN_PROGRESS', 'HIGH', 1, 1, '2024-09-02 08:00:00', '2024-09-02 11:00:00'),

-- Công việc an ninh sân bay (task_type_id = 2)
('Kiểm tra camera an ninh khu vực A', 'Kiểm tra hoạt động của 20 camera tại terminal A', 'Kiểm tra từng camera, làm sạch ống kính', 'COMPLETED', 'NORMAL', 2, 2, '2024-09-01 06:00:00', '2024-09-01 08:00:00'),
('Tuần tra an ninh ca đêm', 'Tuần tra khu vực sân bay từ 22h-6h', 'Kiểm tra tất cả các khu vực, báo cáo bất thường', 'COMPLETED', 'HIGH', 2, 2, '2024-09-01 22:00:00', '2024-09-02 06:00:00'),
('Kiểm tra hệ thống báo động', 'Test hệ thống báo động khẩn cấp', 'Kiểm tra từng vùng, test âm thanh và đèn', 'OPEN', 'HIGH', 2, 2, '2024-09-02 09:00:00', '2024-09-02 09:00:00'),
('Kiểm soát ra vào khu vực hạn chế', 'Kiểm tra thẻ từ và danh tính nhân viên', 'Đối chiếu danh sách được phép vào', 'IN_PROGRESS', 'HIGH', 2, 2, '2024-09-02 10:00:00', '2024-09-02 12:00:00'),
('Kiểm tra hành lý đáng nghi', 'Kiểm tra 5 kiện hành lý được báo cáo', 'Sử dụng máy X-ray và kiểm tra thủ công', 'COMPLETED', 'URGENT', 2, 2, '2024-09-02 14:00:00', '2024-09-02 15:30:00'),

-- Công việc vệ sinh sân bay (task_type_id = 3)
('Vệ sinh terminal A tầng 1', 'Làm sạch toàn bộ khu vực chờ tầng 1', 'Lau sàn, vệ sinh ghế ngồi, đổ rác', 'COMPLETED', 'NORMAL', 3, 3, '2024-09-01 05:00:00', '2024-09-01 07:00:00'),
('Vệ sinh nhà vệ sinh công cộng', 'Vệ sinh 12 nhà vệ sinh tại terminal', 'Sử dụng chất tẩy rửa chuyên dụng', 'COMPLETED', 'NORMAL', 3, 3, '2024-09-01 07:00:00', '2024-09-01 09:00:00'),
('Lau dọn khu vực check-in', 'Vệ sinh quầy check-in và khu vực xung quanh', 'Lau chùi quầy, sàn nhà, sắp xếp ghế', 'IN_PROGRESS', 'NORMAL', 3, 3, '2024-09-02 08:00:00', '2024-09-02 10:00:00'),
('Dọn dẹp khu vực hành lý', 'Thu gom rác và vệ sinh băng tải hành lý', 'Kiểm tra hoạt động băng tải khi vệ sinh', 'OPEN', 'NORMAL', 3, 3, '2024-09-02 11:00:00', '2024-09-02 11:00:00'),
('Vệ sinh khu vực ẩm thực', 'Làm sạch food court và các cửa hàng', 'Vệ sinh bàn ghế, sàn nhà, thu gom rác', 'OPEN', 'HIGH', 3, 3, '2024-09-02 18:00:00', '2024-09-02 18:00:00'),

-- Công việc điều phối chuyến bay (task_type_id = 4)
('Lập kế hoạch bay tuần', 'Lập lịch bay cho tuần tới (100 chuyến)', 'Phối hợp với các hãng hàng không', 'COMPLETED', 'HIGH', 4, 4, '2024-08-30 14:00:00', '2024-08-31 17:00:00'),
('Điều phối chuyến bay bị delay', 'Xử lý 5 chuyến bay bị hoãn do thời tiết', 'Thông báo hành khách, sắp xếp lại slot', 'COMPLETED', 'URGENT', 4, 4, '2024-09-01 15:00:00', '2024-09-01 18:00:00'),
('Cập nhật thông tin chuyến bay', 'Cập nhật 20 thay đổi lịch bay trong ngày', 'Thông báo lên màn hình và website', 'IN_PROGRESS', 'HIGH', 4, 4, '2024-09-02 09:00:00', '2024-09-02 11:30:00'),
('Phối hợp với kiểm soát không lưu', 'Điều phối 15 chuyến bay giờ cao điểm', 'Liên lạc với tower, xác nhận runway', 'OPEN', 'HIGH', 4, 4, '2024-09-02 16:00:00', '2024-09-02 16:00:00'),
('Xử lý khẩn cấp y tế', 'Điều phối đưa bệnh nhân cấp cứu', 'Liên hệ y bác sĩ, chuẩn bị ambulance', 'COMPLETED', 'URGENT', 4, 4, '2024-09-01 23:30:00', '2024-09-02 01:00:00'),

-- Công việc phục vụ hành khách (task_type_id = 5)
('Hỗ trợ hành khách đặc biệt', 'Hỗ trợ 3 hành khách khuyết tật', 'Sử dụng wheelchair, hỗ trợ check-in', 'COMPLETED', 'HIGH', 5, 5, '2024-09-01 10:00:00', '2024-09-01 12:00:00'),
('Xử lý hành lý thất lạc', 'Tìm và trả 8 kiện hành lý thất lạc', 'Tra cứu hệ thống, liên hệ hành khách', 'IN_PROGRESS', 'HIGH', 5, 5, '2024-09-02 08:00:00', '2024-09-02 14:00:00'),
('Hướng dẫn hành khách transit', 'Hỗ trợ 50 hành khách transit', 'Chỉ dẫn đường đi, kiểm tra vé', 'COMPLETED', 'NORMAL', 5, 5, '2024-09-02 09:00:00', '2024-09-02 11:00:00'),
('Xử lý khiếu nại dịch vụ', 'Giải quyết 5 khiếu nại về dịch vụ', 'Lắng nghe, ghi nhận, đưa giải pháp', 'OPEN', 'HIGH', 5, 5, '2024-09-02 13:00:00', '2024-09-02 13:00:00'),
('Hỗ trợ check-in muộn', 'Hỗ trợ 10 hành khách check-in muộn', 'Kiểm tra khả năng lên máy bay', 'COMPLETED', 'URGENT', 5, 5, '2024-09-02 16:30:00', '2024-09-02 17:00:00'),

-- Thêm các task đa dạng khác
('Kiểm tra hệ thống điều hòa T1', 'Bảo trì hệ thống HVAC terminal 1', 'Kiểm tra filter, làm sạch ống gió', 'OPEN', 'NORMAL', 1, 1, '2024-09-03 08:00:00', '2024-09-03 08:00:00'),
('Huấn luyện nhân viên mới', 'Đào tạo 10 nhân viên an ninh mới', 'Đào tạo quy trình, sử dụng thiết bị', 'IN_PROGRESS', 'HIGH', 2, 2, '2024-09-01 14:00:00', '2024-09-02 16:00:00'),
('Kiểm tra thang máy hành khách', 'Bảo trì 8 thang máy trong terminal', 'Test an toàn, bôi trơn bộ phận', 'COMPLETED', 'HIGH', 3, 3, '2024-08-31 09:00:00', '2024-08-31 17:00:00'),
('Cập nhật phần mềm quản lý bay', 'Update flight management system', 'Backup data trước khi update', 'OPEN', 'HIGH', 4, 4, '2024-09-03 10:00:00', '2024-09-03 10:00:00'),
('Tổ chức sự kiện đón khách VIP', 'Đón đoàn khách VIP 50 người', 'Chuẩn bị red carpet, xe limousine', 'COMPLETED', 'URGENT', 5, 5, '2024-09-02 15:00:00', '2024-09-02 18:00:00'),
('Kiểm tra hệ thống báo cháy', 'Test hệ thống phòng cháy chữa cháy', 'Kiểm tra detector, sprinkler system', 'IN_PROGRESS', 'HIGH', 2, 2, '2024-09-02 07:00:00', '2024-09-02 12:00:00'),
('Vệ sinh máy bay sau chuyến bay', 'Dọn dẹp cabin máy bay VN105', 'Thu gom rác, lau ghế, bổ sung amenities', 'COMPLETED', 'NORMAL', 3, 3, '2024-09-02 13:00:00', '2024-09-02 14:30:00'),
('Điều phối slot bay đêm', 'Sắp xếp 8 chuyến bay đêm', 'Phối hợp với airline, kiểm tra crew', 'OPEN', 'NORMAL', 4, 4, '2024-09-02 22:00:00', '2024-09-02 22:00:00'),
('Xử lý hành khách say rượu', 'Xử lý 2 hành khách gây rối', 'Gọi an ninh, báo cáo cơ quan chức năng', 'COMPLETED', 'URGENT', 5, 5, '2024-09-01 20:00:00', '2024-09-01 22:00:00'),
('Tập huấn ứng phó khẩn cấp', 'Drill tình huống cháy nổ', 'Tập trung toàn bộ nhân viên tham gia', 'OPEN', 'HIGH', 2, 2, '2024-09-04 08:00:00', '2024-09-04 08:00:00'),
('Bảo trì băng tải hành lý', 'Sửa chữa 3 băng tải bị hỏng', 'Thay motor, kiểm tra belt', 'OPEN', 'HIGH', 1, 1, '2024-09-03 14:00:00', '2024-09-03 14:00:00'),
('Kiểm tra chất lượng nước', 'Test quality nước sinh hoạt sân bay', 'Lấy mẫu nước, gửi lab kiểm nghiệm', 'COMPLETED', 'NORMAL', 3, 3, '2024-09-01 09:00:00', '2024-09-01 11:00:00'),
('Lập báo cáo tuần hoạt động', 'Tổng hợp báo cáo tuần 36/2024', 'Thu thập số liệu từ các bộ phận', 'IN_PROGRESS', 'NORMAL', 4, 4, '2024-09-02 16:00:00', '2024-09-03 10:00:00'),
('Training sử dụng thiết bị mới', 'Đào tạo sử dụng scanner X-ray mới', 'Hướng dẫn 15 nhân viên an ninh', 'OPEN', 'HIGH', 2, 2, '2024-09-03 13:00:00', '2024-09-03 13:00:00'),
('Kiểm tra kho chứa nhiên liệu', 'Inspection 5 bồn chứa jet fuel', 'Kiểm tra rò rỉ, đo mức nhiên liệu', 'COMPLETED', 'HIGH', 1, 1, '2024-09-01 06:00:00', '2024-09-01 10:00:00'),
('Vệ sinh khu vực cách ly', 'Disinfect khu vực cách ly y tế', 'Sử dụng hóa chất chuyên dụng', 'COMPLETED', 'HIGH', 3, 3, '2024-09-01 16:00:00', '2024-09-01 18:00:00'),
('Điều phối chuyến bay cứu trợ', 'Arrange chuyến bay viện trợ khẩn cấp', 'Ưu tiên slot, miễn phí dịch vụ', 'COMPLETED', 'URGENT', 4, 4, '2024-08-30 10:00:00', '2024-08-30 14:00:00'),
('Hỗ trợ gia đình có trẻ nhỏ', 'Support 20 gia đình có baby', 'Cung cấp stroller, baby room', 'IN_PROGRESS', 'NORMAL', 5, 5, '2024-09-02 11:00:00', '2024-09-02 15:00:00'),
('Maintenance xe cứu hỏa', 'Bảo dưỡng 3 xe cứu hỏa sân bay', 'Kiểm tra bơm nước, thay lốp', 'OPEN', 'HIGH', 1, 1, '2024-09-03 09:00:00', '2024-09-03 09:00:00'),
('Kiểm tra hệ thống wifi public', 'Test tốc độ wifi cho hành khách', 'Kiểm tra coverage và bandwidth', 'OPEN', 'NORMAL', 1, 1, '2024-09-03 11:00:00', '2024-09-03 11:00:00'),
('Patrol khu vực parking', 'Tuần tra bãi đỗ xe terminal', 'Kiểm tra an ninh, hỗ trợ khách', 'IN_PROGRESS', 'NORMAL', 2, 2, '2024-09-02 20:00:00', '2024-09-03 04:00:00'),
('Vệ sinh khu vực VIP lounge', 'Dọn dẹp phòng chờ VIP', 'Vệ sinh kỹ lưỡng, bổ sung amenities', 'COMPLETED', 'HIGH', 3, 3, '2024-09-02 06:00:00', '2024-09-02 08:00:00'),
('Coordination với hàng không quốc tế', 'Điều phối 5 chuyến bay quốc tế', 'Xử lý thủ tục hải quan, xuất nhập cảnh', 'IN_PROGRESS', 'HIGH', 4, 4, '2024-09-02 12:00:00', '2024-09-02 18:00:00'),
('Hỗ trợ hành khách bị lỡ chuyến', 'Arrange chuyến bay thay thế cho 15 khách', 'Liên hệ airline, booking chuyến mới', 'COMPLETED', 'URGENT', 5, 5, '2024-09-02 19:00:00', '2024-09-02 21:00:00');

-- Insert sample assignments (60+ assignments)
INSERT INTO assignment (task_id, recipient_type, recipient_id, assigned_by, assigned_at, due_at, status, note) VALUES
-- Task 1: Kiểm tra động cơ máy bay - assign to users and teams
(1, 'USER', 1, 1, '2024-09-01 08:00:00', '2024-09-01 16:00:00', 'WORKING', 'Ưu tiên kiểm tra động cơ số 1'),
(1, 'USER', 2, 1, '2024-09-01 08:05:00', '2024-09-01 16:00:00', 'WORKING', 'Hỗ trợ kiểm tra động cơ số 2'),
(1, 'TEAM', 1, 1, '2024-09-01 08:10:00', '2024-09-01 17:00:00', 'WORKING', 'Team kỹ thuật support'),

-- Task 2: Bảo dưỡng hệ thống thủy lực
(2, 'USER', 3, 1, '2024-09-01 09:00:00', '2024-09-01 15:00:00', 'DONE', 'Hoàn thành đúng tiến độ'),
(2, 'TEAM', 1, 1, '2024-09-01 09:10:00', '2024-09-01 15:00:00', 'DONE', 'Team phối hợp tốt'),

-- Task 3: Kiểm tra hệ thống điện tử
(3, 'USER', 4, 1, '2024-09-01 10:00:00', '2024-09-01 14:00:00', 'DONE', 'Completed successfully'),

-- Task 4: Thay lốp máy bay - urgent task
(4, 'USER', 1, 1, '2024-09-02 07:30:00', '2024-09-02 12:00:00', 'WORKING', 'Cần hoàn thành gấp'),
(4, 'USER', 2, 1, '2024-09-02 07:35:00', '2024-09-02 12:00:00', 'WORKING', 'Hỗ trợ thay lốp'),
(4, 'TEAM', 1, 1, '2024-09-02 07:40:00', '2024-09-02 13:00:00', 'WORKING', 'All hands on deck'),

-- Task 5: Kiểm tra hệ thống phanh
(5, 'USER', 3, 1, '2024-09-02 08:00:00', '2024-09-02 16:00:00', 'WORKING', 'Đang thực hiện test'),

-- Task 6: Kiểm tra camera an ninh
(6, 'USER', 5, 2, '2024-09-01 06:00:00', '2024-09-01 14:00:00', 'DONE', 'Tất cả camera hoạt động tốt'),
(6, 'TEAM', 2, 2, '2024-09-01 06:15:00', '2024-09-01 14:00:00', 'DONE', 'Team security check'),

-- Task 7: Tuần tra an ninh ca đêm
(7, 'USER', 6, 2, '2024-09-01 22:00:00', '2024-09-02 06:00:00', 'DONE', 'Tuần tra an toàn'),
(7, 'USER', 7, 2, '2024-09-01 22:30:00', '2024-09-02 06:00:00', 'DONE', 'Backup patrol'),

-- Task 8: Kiểm tra hệ thống báo động
(8, 'USER', 5, 2, '2024-09-02 09:00:00', '2024-09-02 17:00:00', 'WORKING', 'Đang test từng zone'),

-- Task 9: Kiểm soát ra vào
(9, 'USER', 6, 2, '2024-09-02 10:00:00', '2024-09-02 18:00:00', 'WORKING', 'Checking access cards'),
(9, 'USER', 7, 2, '2024-09-02 10:30:00', '2024-09-02 18:00:00', 'WORKING', 'Secondary check'),

-- Task 10: Kiểm tra hành lý đáng nghi
(10, 'USER', 5, 2, '2024-09-02 14:00:00', '2024-09-02 16:00:00', 'DONE', 'All clear - no threats'),
(10, 'TEAM', 2, 2, '2024-09-02 14:05:00', '2024-09-02 16:00:00', 'DONE', 'Security team response'),

-- Task 11: Vệ sinh terminal A
(11, 'USER', 8, 3, '2024-09-01 05:00:00', '2024-09-01 07:00:00', 'DONE', 'Terminal sạch sẽ'),
(11, 'TEAM', 3, 3, '2024-09-01 05:15:00', '2024-09-01 07:00:00', 'DONE', 'Cleaning crew'),

-- Task 12: Vệ sinh nhà vệ sinh
(12, 'USER', 9, 3, '2024-09-01 07:00:00', '2024-09-01 09:00:00', 'DONE', 'All restrooms cleaned'),

-- Task 13: Lau dọn khu vực check-in
(13, 'USER', 8, 3, '2024-09-02 08:00:00', '2024-09-02 12:00:00', 'WORKING', 'Đang lau dọn'),
(13, 'USER', 9, 3, '2024-09-02 08:30:00', '2024-09-02 12:00:00', 'WORKING', 'Hỗ trợ vệ sinh'),

-- Task 14: Dọn dẹp khu vực hành lý
(14, 'TEAM', 3, 3, '2024-09-02 11:00:00', '2024-09-02 15:00:00', 'WORKING', 'Team cleaning assignment'),

-- Task 15: Vệ sinh khu vực ẩm thực
(15, 'USER', 8, 3, '2024-09-02 18:00:00', '2024-09-02 22:00:00', 'WORKING', 'Evening cleaning shift'),

-- Task 16: Lập kế hoạch bay tuần
(16, 'USER', 10, 4, '2024-08-30 14:00:00', '2024-08-31 17:00:00', 'DONE', 'Weekly schedule completed'),
(16, 'TEAM', 4, 4, '2024-08-30 14:30:00', '2024-08-31 17:00:00', 'DONE', 'Flight ops team'),

-- Task 17: Điều phối chuyến bay delay
(17, 'USER', 11, 4, '2024-09-01 15:00:00', '2024-09-01 20:00:00', 'DONE', 'Successfully rescheduled'),
(17, 'USER', 12, 4, '2024-09-01 15:15:00', '2024-09-01 20:00:00', 'DONE', 'Passenger notifications sent'),

-- Task 18: Cập nhật thông tin chuyến bay
(18, 'USER', 10, 4, '2024-09-02 09:00:00', '2024-09-02 17:00:00', 'WORKING', 'Updating flight info'),

-- Task 19: Phối hợp với kiểm soát không lưu
(19, 'USER', 11, 4, '2024-09-02 16:00:00', '2024-09-02 20:00:00', 'WORKING', 'Coordinating with tower'),

-- Task 20: Xử lý khẩn cấp y tế
(20, 'USER', 12, 4, '2024-09-01 23:30:00', '2024-09-02 02:00:00', 'DONE', 'Medical emergency handled'),
(20, 'TEAM', 4, 4, '2024-09-01 23:35:00', '2024-09-02 02:00:00', 'DONE', 'Emergency response team'),

-- Task 21: Hỗ trợ hành khách đặc biệt
(21, 'USER', 13, 5, '2024-09-01 10:00:00', '2024-09-01 14:00:00', 'DONE', 'Assisted 3 disabled passengers'),
(21, 'TEAM', 5, 5, '2024-09-01 10:15:00', '2024-09-01 14:00:00', 'DONE', 'Customer service team'),

-- Task 22: Xử lý hành lý thất lạc
(22, 'USER', 14, 5, '2024-09-02 08:00:00', '2024-09-02 18:00:00', 'WORKING', 'Tracking missing luggage'),
(22, 'USER', 15, 5, '2024-09-02 08:30:00', '2024-09-02 18:00:00', 'WORKING', 'Contacting passengers'),

-- Task 23: Hướng dẫn hành khách transit
(23, 'USER', 13, 5, '2024-09-02 09:00:00', '2024-09-02 13:00:00', 'DONE', 'Assisted 50 transit passengers'),

-- Task 24: Xử lý khiếu nại dịch vụ
(24, 'USER', 14, 5, '2024-09-02 13:00:00', '2024-09-02 17:00:00', 'WORKING', 'Resolving service complaints'),

-- Task 25: Hỗ trợ check-in muộn
(25, 'USER', 15, 5, '2024-09-02 16:30:00', '2024-09-02 18:00:00', 'DONE', 'Helped 10 late passengers'),
(25, 'TEAM', 5, 5, '2024-09-02 16:35:00', '2024-09-02 18:00:00', 'DONE', 'Customer service support'),

-- Additional assignments for remaining tasks
(26, 'USER', 1, 1, '2024-09-03 08:00:00', '2024-09-03 16:00:00', 'WORKING', 'HVAC maintenance'),
(27, 'USER', 5, 2, '2024-09-01 14:00:00', '2024-09-03 17:00:00', 'WORKING', 'Training new security staff'),
(28, 'USER', 8, 3, '2024-08-31 09:00:00', '2024-08-31 17:00:00', 'DONE', 'Elevator maintenance completed'),
(29, 'USER', 10, 4, '2024-09-03 10:00:00', '2024-09-03 18:00:00', 'WORKING', 'Software update in progress'),
(30, 'USER', 13, 5, '2024-09-02 15:00:00', '2024-09-02 18:00:00', 'DONE', 'VIP event completed successfully'),

-- More assignments to reach 60+
(31, 'USER', 6, 2, '2024-09-02 07:00:00', '2024-09-02 15:00:00', 'WORKING', 'Fire system testing'),
(32, 'USER', 9, 3, '2024-09-02 13:00:00', '2024-09-02 15:00:00', 'DONE', 'Aircraft cabin cleaned'),
(33, 'USER', 11, 4, '2024-09-02 22:00:00', '2024-09-03 06:00:00', 'WORKING', 'Night flight coordination'),
(34, 'USER', 7, 2, '2024-09-01 20:00:00', '2024-09-01 22:00:00', 'DONE', 'Disruptive passenger handled'),
(35, 'USER', 5, 2, '2024-09-04 08:00:00', '2024-09-04 12:00:00', 'WORKING', 'Emergency drill preparation'),
(36, 'USER', 2, 1, '2024-09-03 14:00:00', '2024-09-03 18:00:00', 'WORKING', 'Baggage conveyor repair'),
(37, 'USER', 8, 3, '2024-09-01 09:00:00', '2024-09-01 11:00:00', 'DONE', 'Water quality test completed'),
(38, 'USER', 12, 4, '2024-09-02 16:00:00', '2024-09-03 12:00:00', 'WORKING', 'Weekly report compilation'),
(39, 'USER', 6, 2, '2024-09-03 13:00:00', '2024-09-03 17:00:00', 'WORKING', 'X-ray equipment training'),
(40, 'USER', 3, 1, '2024-09-01 06:00:00', '2024-09-01 10:00:00', 'DONE', 'Fuel storage inspection completed');
