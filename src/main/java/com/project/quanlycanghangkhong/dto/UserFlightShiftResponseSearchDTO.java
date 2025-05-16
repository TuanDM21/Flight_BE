package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class UserFlightShiftResponseSearchDTO {
    private Integer scheduleId;
    private String userName;
    private String teamName;
    private String unitName;
    private LocalDate shiftDate;
    private String flightNumber;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String departureAirportCode; // Sân bay đi
    private String arrivalAirportCode;   // Sân bay đến
    private Long flightId;
    private Integer userId;

    public UserFlightShiftResponseSearchDTO(Integer scheduleId, String userName, String teamName, String unitName,
            LocalDate shiftDate, String flightNumber, LocalTime departureTime, LocalTime arrivalTime,
            String departureAirportCode, String arrivalAirportCode, Long flightId, Integer userId) {
        this.scheduleId = scheduleId;
        this.userName = userName;
        this.teamName = teamName;
        this.unitName = unitName;
        this.shiftDate = shiftDate;
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.departureAirportCode = departureAirportCode;
        this.arrivalAirportCode = arrivalAirportCode;
        this.flightId = flightId;
        this.userId = userId;
    }

    // Getters & Setters
    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
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

    public String getDepartureAirportCode() {
        return departureAirportCode;
    }

    public void setDepartureAirportCode(String departureAirportCode) {
        this.departureAirportCode = departureAirportCode;
    }

    public String getArrivalAirportCode() {
        return arrivalAirportCode;
    }

    public void setArrivalAirportCode(String arrivalAirportCode) {
        this.arrivalAirportCode = arrivalAirportCode;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
