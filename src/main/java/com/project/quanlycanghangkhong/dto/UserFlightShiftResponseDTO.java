package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;

public class UserFlightShiftResponseDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private Long flightId;
    private String flightNumber;
    private LocalDate shiftDate;

    // Constructors
    public UserFlightShiftResponseDTO() {
    }

    public UserFlightShiftResponseDTO(Integer id, Integer userId, String userName, Long flightId,
            String flightNumber, LocalDate shiftDate) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.shiftDate = shiftDate;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }
}
