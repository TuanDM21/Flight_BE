package com.project.quanlycanghangkhong.request;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for updating flight information")
public class UpdateFlightRequest {
    
    @NotBlank(message = "Flight number is required")
    @Schema(description = "Flight number", example = "VN123", required = true)
    private String flightNumber;
    
    @NotNull(message = "Departure airport ID is required")
    @Schema(description = "Departure airport ID", example = "1", required = true)
    private Long departureAirportId;
    
    @NotNull(message = "Arrival airport ID is required")
    @Schema(description = "Arrival airport ID", example = "2", required = true)
    private Long arrivalAirportId;
    
    @NotBlank(message = "Departure time is required")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", 
             message = "Departure time must be in HH:mm:ss format")
    @Schema(description = "Scheduled departure time", example = "08:30:00", required = true)
    private String departureTime;
    
    @NotBlank(message = "Arrival time is required")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", 
             message = "Arrival time must be in HH:mm:ss format")
    @Schema(description = "Scheduled arrival time", example = "10:45:00", required = true)
    private String arrivalTime;
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", 
             message = "Arrival time at arrival must be in HH:mm:ss format")
    @Schema(description = "Actual arrival time at destination", example = "10:50:00")
    private String arrivalTimeatArrival;
    
    @Schema(description = "Flight status", example = "ON_TIME")
    private String status;
    
    @NotNull(message = "Flight date is required")
    @Schema(description = "Flight date", example = "2025-06-22", required = true)
    private LocalDate flightDate;
    
    @Schema(description = "Airline name", example = "Vietnam Airlines")
    private String airline;
    
    @Schema(description = "Check-in counters", example = "A1-A5")
    private String checkInCounters;
    
    @Schema(description = "Gate number", example = "12")
    private Integer gate;
    
    @Schema(description = "Additional notes", example = "Weather delay expected")
    private String note;

    // Constructors
    public UpdateFlightRequest() {}

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