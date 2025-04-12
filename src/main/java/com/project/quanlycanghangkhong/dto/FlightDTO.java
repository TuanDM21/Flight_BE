package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.project.quanlycanghangkhong.model.Flight;

public class FlightDTO {

    private Long id;
    private String flightNumber;
    private String departureAirport;
    private String arrivalAirport;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private LocalDate flightDate;

    // Các trường thêm vào
    private LocalTime actualDepartureTime;
    private LocalTime actualArrivalTime;
    private LocalTime actualDepartureTimeAtArrival;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FlightDTO() {
    }

    // Constructor chuyển từ entity sang DTO
    public FlightDTO(Flight flight) {
        this.id = flight.getId();
        this.flightNumber = flight.getFlightNumber();
        this.departureTime = flight.getDepartureTime();
        this.arrivalTime = flight.getArrivalTime();
        this.flightDate = flight.getFlightDate();
        this.actualDepartureTime = flight.getActualDepartureTime();
        this.actualArrivalTime = flight.getActualArrivalTime();
        this.actualDepartureTimeAtArrival = flight.getActualDepartureTimeAtArrival();
        this.createdAt = flight.getCreatedAt();
        this.updatedAt = flight.getUpdatedAt();
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(LocalDate flightDate) {
        this.flightDate = flightDate;
    }

    public LocalTime getActualDepartureTime() {
        return actualDepartureTime;
    }

    public void setActualDepartureTime(LocalTime actualDepartureTime) {
        this.actualDepartureTime = actualDepartureTime;
    }

    public LocalTime getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(LocalTime actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public LocalTime getActualDepartureTimeAtArrival() {
        return actualDepartureTimeAtArrival;
    }

    public void setActualDepartureTimeAtArrival(LocalTime actualDepartureTimeAtArrival) {
        this.actualDepartureTimeAtArrival = actualDepartureTimeAtArrival;
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
