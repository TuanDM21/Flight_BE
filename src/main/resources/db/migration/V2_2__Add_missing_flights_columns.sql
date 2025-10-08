-- V2_2__Add_missing_flights_columns.sql
-- Add missing columns to flights table to match Flight entity

ALTER TABLE flights
ADD COLUMN airline VARCHAR(255),
ADD COLUMN actual_departure_time_at_arrival TIME,
ADD COLUMN arrival_time_at_arrival TIME;

-- Create index for airline column for better query performance
CREATE INDEX idx_flights_airline ON flights(airline);
