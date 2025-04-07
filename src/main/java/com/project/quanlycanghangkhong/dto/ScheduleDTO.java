package com.project.quanlycanghangkhong.dto;

import java.sql.Time;
import java.time.LocalDate;

// DTO chứa thông tin lịch trực
public class ScheduleDTO {
    private int scheduleId;
    private LocalDate shiftDate;
    private int userId;
    private String userName;
    private Integer teamId;
    private String teamName;
    private Integer unitId;
    private String unitName;
    private Integer shiftId;
    private String shiftCode;
    private Time startTime;
    private Time endTime;
    private String location;
    private String description;
    
	public ScheduleDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ScheduleDTO(int scheduleId, LocalDate shiftDate, int userId, String userName, Integer teamId,
			String teamName, Integer unitId, String unitName, Integer shiftId, String shiftCode, Time startTime,
			Time endTime, String location, String description) {
		super();
		this.scheduleId = scheduleId;
		this.shiftDate = shiftDate;
		this.userId = userId;
		this.userName = userName;
		this.teamId = teamId;
		this.teamName = teamName;
		this.unitId = unitId;
		this.unitName = unitName;
		this.shiftId = shiftId;
		this.shiftCode = shiftCode;
		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;
		this.description = description;
	}

	public int getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}

	public LocalDate getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(LocalDate shiftDate) {
		this.shiftDate = shiftDate;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getTeamId() {
		return teamId;
	}

	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Integer getShiftId() {
		return shiftId;
	}

	public void setShiftId(Integer shiftId) {
		this.shiftId = shiftId;
	}

	public String getShiftCode() {
		return shiftCode;
	}

	public void setShiftCode(String shiftCode) {
		this.shiftCode = shiftCode;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
    
    
 
    // Getters & Setters
    // ... (code omitted cho ngắn gọn)
}
