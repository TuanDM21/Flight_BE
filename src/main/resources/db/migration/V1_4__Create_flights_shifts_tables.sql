-- V1_4__Create_flights_shifts_tables.sql
-- Creates flights and shifts tables for operational management

CREATE TABLE shifts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    shift_code VARCHAR(50) NOT NULL UNIQUE,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    location VARCHAR(255) NOT NULL,
    description TEXT,
    team_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES teams(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE flights (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(50) NOT NULL,
    departure_airport_id BIGINT NOT NULL,
    arrival_airport_id BIGINT NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    flight_date DATE NOT NULL,
    actual_departure_time TIME,
    actual_arrival_time TIME,
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    check_in_counters VARCHAR(255),
    gate INT,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (departure_airport_id) REFERENCES airports(id),
    FOREIGN KEY (arrival_airport_id) REFERENCES airports(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for better query performance
CREATE INDEX idx_shifts_code ON shifts(shift_code);
CREATE INDEX idx_shifts_team_id ON shifts(team_id);
CREATE INDEX idx_flights_number ON flights(flight_number);
CREATE INDEX idx_flights_date ON flights(flight_date);
CREATE INDEX idx_flights_departure_airport ON flights(departure_airport_id);
CREATE INDEX idx_flights_arrival_airport ON flights(arrival_airport_id);
CREATE INDEX idx_flights_status ON flights(status);
