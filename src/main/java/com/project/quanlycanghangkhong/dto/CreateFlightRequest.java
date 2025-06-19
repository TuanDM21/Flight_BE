package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import com.project.quanlycanghangkhong.model.Airport;

public class CreateFlightRequest {
    
    @NotBlank(message = "Flight number is required")
    private String flightNumber;
    
    @NotNull(message = "Departure airport is required")
    private Airport departureAirport;
    
    @NotNull(message = "Arrival airport is required") 
    private Airport arrivalAirport;
    
    @NotNull(message = "Departure time is required")
    private LocalTime departureTime;
    
    @NotNull(message = "Arrival time is required")
    private LocalTime arrivalTime;
    
    private LocalTime arrivalTimeatArrival;
    
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

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
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

    public LocalTime getArrivalTimeatArrival() {
        return arrivalTimeatArrival;
    }

    public void setArrivalTimeatArrival(LocalTime arrivalTimeatArrival) {
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