package com.project.quanlycanghangkhong.dto;

import java.time.LocalTime;

public class ShiftDTO {
    private Integer id;
    private String shiftCode;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String description;
    private Integer teamId;
    private String teamName;

    public ShiftDTO() {}

    public ShiftDTO(Integer id, String shiftCode, LocalTime startTime, LocalTime endTime, String location, String description, Integer teamId, String teamName) {
        this.id = id;
        this.shiftCode = shiftCode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getShiftCode() { return shiftCode; }
    public void setShiftCode(String shiftCode) { this.shiftCode = shiftCode; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
}
