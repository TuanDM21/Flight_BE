package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateFlightRequest {
    
    @NotBlank(message = "Flight number is required")
    private String flightNumber;
    
    @NotNull(message = "Departure airport ID is required")
    private Long departureAirportId;
    
    @NotNull(message = "Arrival airport ID is required") 
    private Long arrivalAirportId;
    
    @NotBlank(message = "Departure time is required")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "Departure time must be in HH:mm:ss format")
    private String departureTime;
    
    @NotBlank(message = "Arrival time is required")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "Arrival time must be in HH:mm:ss format")
    private String arrivalTime;
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "Arrival time at arrival must be in HH:mm:ss format")
    private String arrivalTimeatArrival;
    
    private String status;
    
    @NotNull(message = "Flight date is required")
    private LocalDate flightDate;
    
    private String airline;
    
    private String checkInCounters;
    
    private Integer gate;
    
    private String note;

    // Constructors
    public CreateFlightRequest() {}

    // Getters and Setters
    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Long getDepartureAirportId() {
        return departureAirportId;
    }

    public void setDepartureAirportId(Long departureAirportId) {
        this.departureAirportId = departureAirportId;
    }

    public Long getArrivalAirportId() {
        return arrivalAirportId;
    }

    public void setArrivalAirportId(Long arrivalAirportId) {
        this.arrivalAirportId = arrivalAirportId;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getArrivalTimeatArrival() {
        return arrivalTimeatArrival;
    }

    public void setArrivalTimeatArrival(String arrivalTimeatArrival) {
        this.arrivalTimeatArrival = arrivalTimeatArrival;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(LocalDate flightDate) {
        this.flightDate = flightDate;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getCheckInCounters() {
        return checkInCounters;
    }

    public void setCheckInCounters(String checkInCounters) {
        this.checkInCounters = checkInCounters;
    }

    public Integer getGate() {
        return gate;
    }

    public void setGate(Integer gate) {
        this.gate = gate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}