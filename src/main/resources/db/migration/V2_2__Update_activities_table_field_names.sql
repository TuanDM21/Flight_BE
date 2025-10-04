-- Migration to update activities table field names
-- name -> title, notes -> description, startTime -> startDate, endTime -> endDate

ALTER TABLE activities 
CHANGE COLUMN name title VARCHAR(255) NOT NULL,
CHANGE COLUMN notes description TEXT,
CHANGE COLUMN startTime startDate DATETIME NOT NULL,
CHANGE COLUMN endTime endDate DATETIME NOT NULL;
