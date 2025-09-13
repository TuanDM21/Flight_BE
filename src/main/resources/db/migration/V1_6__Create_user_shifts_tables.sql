-- V1_8__Create_user_shifts_tables.sql
-- Creates user shift related tables

-- User shifts table
CREATE TABLE user_shifts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    shift_date DATE NOT NULL,
    shift_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (shift_id) REFERENCES shifts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User flight shifts table
CREATE TABLE user_flight_shifts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    flight_id BIGINT NOT NULL,
    shift_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (flight_id) REFERENCES flights(id),
    UNIQUE KEY uk_user_flight_shift (user_id, flight_id, shift_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Flight assignments table
CREATE TABLE flight_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_shift_id INT NOT NULL,
    flight_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_shift_id) REFERENCES user_shifts(id),
    FOREIGN KEY (flight_id) REFERENCES flights(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for better query performance
CREATE INDEX idx_user_shifts_user_id ON user_shifts(user_id);
CREATE INDEX idx_user_shifts_shift_date ON user_shifts(shift_date);
CREATE INDEX idx_user_flight_shifts_user_id ON user_flight_shifts(user_id);
CREATE INDEX idx_user_flight_shifts_flight_id ON user_flight_shifts(flight_id);
CREATE INDEX idx_user_flight_shifts_shift_date ON user_flight_shifts(shift_date);
CREATE INDEX idx_flight_assignments_user_shift_id ON flight_assignments(user_shift_id);
CREATE INDEX idx_flight_assignments_flight_id ON flight_assignments(flight_id);