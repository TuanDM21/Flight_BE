package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class UserFlightShiftResponseSearchDTO {
    private Long scheduleId;
    private String userName;
    private String teamName;
    private String unitName;
    private LocalDate shiftDate;
    private String flightNumber;
    private LocalTime departureTime; // thay thế startTime
    private LocalTime arrivalTime;   // thay thế endTime
//    private String location;
//    private String description;

    // Constructor đầy đủ theo thứ tự của query
    public UserFlightShiftResponseSearchDTO(Long scheduleId, String userName, String teamName, String unitName,
                                      LocalDate shiftDate, String flightNumber, LocalTime departureTime
                                      , LocalTime arrivalTime
//                                      String location, String description
                                      ) {
        this.scheduleId = scheduleId;
        this.userName = userName;
        this.teamName = teamName;
        this.unitName = unitName;
        this.shiftDate = shiftDate;
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
//        this.location = location;
//        this.description = description;
    }

    // Getters & Setters
    public Long getScheduleId() {
        return scheduleId;
    }
    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public String getUnitName() {
        return unitName;
    }
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    public LocalDate getShiftDate() {
        return shiftDate;
    }
    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }
    public String getFlightNumber() {
        return flightNumber;
    }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
  
    public LocalTime getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(LocalTime departureTime) {
		this.departureTime = departureTime;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

//	public String getLocation() {
//        return location;
//    }
//    public void setLocation(String location) {
//        this.location = location;
//    }
//    public String getDescription() {
//        return description;
//    }
//    public void setDescription(String description) {
//        this.description = description;
//    }
}
