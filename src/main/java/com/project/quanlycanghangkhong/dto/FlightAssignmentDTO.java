package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.project.quanlycanghangkhong.model.FlightAssignment;
import com.project.quanlycanghangkhong.model.UserShift;
import com.project.quanlycanghangkhong.model.Flight;

public class FlightAssignmentDTO {

    private Long id;
    
    // User Shift info
    private Integer userShiftId;
    private String userName;      // from UserShift.getUser().getName()
    private LocalDate shiftDate;  // from UserShift.getShiftDate()
    
    // Flight info
    private Long flightId;
    private String flightNumber;
    private String departureAirport;
    private String arrivalAirport;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default Constructor
    public FlightAssignmentDTO() {}

    // Constructor to convert entity to DTO
    public FlightAssignmentDTO(FlightAssignment assignment) {
        this.id = assignment.getId();
        
        UserShift us = assignment.getUserShift();
        if (us != null) {
            this.userShiftId = us.getId();
            if (us.getUser() != null) {
                this.userName = us.getUser().getName();
            }
            this.shiftDate = us.getShiftDate();
        }
        
        Flight flight = assignment.getFlight();
        if (flight != null) {
            this.flightId = flight.getId();
            this.flightNumber = flight.getFlightNumber();
            this.departureAirport = flight.getDepartureAirport();
            this.arrivalAirport = flight.getArrivalAirport();
            this.departureTime = flight.getDepartureTime();
            this.arrivalTime = flight.getArrivalTime();
        }
        this.createdAt = assignment.getCreatedAt();
        this.updatedAt = assignment.getUpdatedAt();
    }

    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getUserShiftId() {
        return userShiftId;
    }
    public void setUserShiftId(Integer userShiftId) {
        this.userShiftId = userShiftId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public LocalDate getShiftDate() {
        return shiftDate;
    }
    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
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
    public String getDepartureAirport() {
        return departureAirport;
    }
    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }
    public String getArrivalAirport() {
        return arrivalAirport;
    }
    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
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

	public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
