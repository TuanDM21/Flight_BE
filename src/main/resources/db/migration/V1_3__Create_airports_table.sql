-- V1_3__Create_airports_table.sql
-- Creates airports table for flight management

CREATE TABLE airports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    airport_code VARCHAR(3) NOT NULL UNIQUE,
    airport_name VARCHAR(255) NOT NULL,
    latitude DECIMAL(10,6) NOT NULL,
    longitude DECIMAL(10,6) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL DEFAULT 'Vietnam',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create indexes for better query performance
CREATE INDEX idx_airports_code ON airports(airport_code);
CREATE INDEX idx_airports_city ON airports(city);
CREATE INDEX idx_airports_country ON airports(country);
