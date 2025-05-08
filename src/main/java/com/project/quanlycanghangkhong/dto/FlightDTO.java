package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class FlightDTO {

    private Long id;
    private String flightNumber;
    private AirportDTO departureAirport;
    private AirportDTO arrivalAirport;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private LocalDate flightDate;

    // Các trường thêm vào
    private LocalTime actualDepartureTime;
    private LocalTime actualArrivalTime;    
    private LocalTime actualDepartureTimeAtArrival;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String note;

    public FlightDTO() {}

    // Constructor từ entity Flight (phiên bản cũ)
    public FlightDTO(com.project.quanlycanghangkhong.model.Flight flight) {
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
        this.note = flight.getNote();
        
        if (flight.getDepartureAirport() != null) {
            this.departureAirport = new AirportDTO(
                flight.getDepartureAirport().getAirportCode(),
                flight.getDepartureAirport().getAirportName());
        }
        if (flight.getArrivalAirport() != null) {
            this.arrivalAirport = new AirportDTO(
                flight.getArrivalAirport().getAirportCode(),
                flight.getArrivalAirport().getAirportName());
        }
    }

    // Constructor hỗ trợ projection từ query trong FlightRepository
    public FlightDTO(Long id, String flightNumber, 
                     String departureAirportCode, String arrivalAirportCode,
                     LocalTime departureTime, LocalTime arrivalTime, LocalDate flightDate,
                     LocalTime actualDepartureTime, LocalTime actualArrivalTime, LocalTime actualDepartureTimeAtArrival,
                     LocalDateTime createdAt, LocalDateTime updatedAt, String note) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightDate = flightDate;
        this.actualDepartureTime = actualDepartureTime;
        this.actualArrivalTime = actualArrivalTime;
        this.actualDepartureTimeAtArrival = actualDepartureTimeAtArrival;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.note = note;

        // Chuyển đổi mã sân bay thành AirportDTO; nếu cần bổ sung thêm tên sân bay thì điều chỉnh lại
        if (departureAirportCode != null) {
            this.departureAirport = new AirportDTO(departureAirportCode, null);
        }
        if (arrivalAirportCode != null) {
            this.arrivalAirport = new AirportDTO(arrivalAirportCode, null);
        }
    }

    // Constructor projection không có trường note (backward compatible)
    public FlightDTO(Long id, String flightNumber, 
                     String departureAirportCode, String arrivalAirportCode,
                     LocalTime departureTime, LocalTime arrivalTime, LocalDate flightDate,
                     LocalTime actualDepartureTime, LocalTime actualArrivalTime, LocalTime actualDepartureTimeAtArrival,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, flightNumber, departureAirportCode, arrivalAirportCode, departureTime, arrivalTime, flightDate,
            actualDepartureTime, actualArrivalTime, actualDepartureTimeAtArrival, createdAt, updatedAt, null);
    }

    // Getters & Setters ...

    public Long getId() {
        return id;
    }
    public String getFlightNumber() {
        return flightNumber;
    }
    public AirportDTO getDepartureAirport() {
        return departureAirport;
    }
    public AirportDTO getArrivalAirport() {
        return arrivalAirport;
    }
    public LocalTime getDepartureTime() {
        return departureTime;
    }
    public LocalTime getArrivalTime() {
        return arrivalTime;
    }
    public LocalDate getFlightDate() {
        return flightDate;
    }
    public LocalTime getActualDepartureTime() {
        return actualDepartureTime;
    }
    public LocalTime getActualArrivalTime() {
        return actualArrivalTime;
    }
    public LocalTime getActualDepartureTimeAtArrival() {
        return actualDepartureTimeAtArrival;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    // Setters nếu cần...
}
