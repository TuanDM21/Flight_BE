-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

-- Create teams table
CREATE TABLE IF NOT EXISTS teams (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- Create units table
CREATE TABLE IF NOT EXISTS units (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INTEGER NOT NULL,
    team_id INTEGER,
    unit_id INTEGER,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (team_id) REFERENCES teams(id),
    FOREIGN KEY (unit_id) REFERENCES units(id)
);

-- Create airports table
CREATE TABLE IF NOT EXISTS airports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    airport_code VARCHAR(3) NOT NULL UNIQUE,
    airport_name VARCHAR(255) NOT NULL,
    latitude DECIMAL(10,6) NOT NULL,
    longitude DECIMAL(10,6) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL DEFAULT 'Vietnam',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create flights table
CREATE TABLE IF NOT EXISTS flights (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL,
    departure_airport_id BIGINT NOT NULL,
    arrival_airport_id BIGINT NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    flight_date DATE NOT NULL,
    actual_departure_time TIME,
    actual_arrival_time TIME,
    actual_departure_time_at_arrival TIME,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (departure_airport_id) REFERENCES airports(id),
    FOREIGN KEY (arrival_airport_id) REFERENCES airports(id)
);

-- Create shifts table
CREATE TABLE IF NOT EXISTS shifts (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    shift_code VARCHAR(50) NOT NULL UNIQUE,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    location VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create user_shifts table
CREATE TABLE IF NOT EXISTS user_shifts (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    shift_date DATE NOT NULL,
    shift_id INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (shift_id) REFERENCES shifts(id)
);

-- Create user_flight_shifts table
CREATE TABLE IF NOT EXISTS user_flight_shifts (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    flight_id BIGINT NOT NULL,
    shift_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (flight_id) REFERENCES flights(id),
    UNIQUE KEY unique_user_flight_shift (user_id, flight_id, shift_date)
);

-- Create flight_assignments table
CREATE TABLE IF NOT EXISTS flight_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_shift_id INTEGER NOT NULL,
    flight_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_shift_id) REFERENCES user_shifts(id),
    FOREIGN KEY (flight_id) REFERENCES flights(id)
);

-- Insert default roles
INSERT INTO roles (role_name) VALUES
('ADMIN'),
('MANAGER'),
('STAFF');
