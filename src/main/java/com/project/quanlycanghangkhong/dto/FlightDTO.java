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
    private LocalTime arrivalTimeatArrival;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String note;

    // Thêm các field mới
    private String airline;
    private String checkInCounters;
    private Integer gate;

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
        this.arrivalTimeatArrival = flight.getArrivalTimeatArrival();
        this.status = flight.getStatus();
        this.createdAt = flight.getCreatedAt();
        this.updatedAt = flight.getUpdatedAt();
        this.note = flight.getNote();
        this.airline = flight.getAirline();
        this.checkInCounters = flight.getCheckInCounters();
        this.gate = flight.getGate();
        
        // ✅ Mapping đầy đủ thông tin Airport với tất cả các field
        if (flight.getDepartureAirport() != null) {
            this.departureAirport = new AirportDTO(
                flight.getDepartureAirport().getId(),
                flight.getDepartureAirport().getAirportCode(),
                flight.getDepartureAirport().getAirportName(),
                flight.getDepartureAirport().getLatitude(),
                flight.getDepartureAirport().getLongitude(),
                flight.getDepartureAirport().getCity(),
                flight.getDepartureAirport().getCountry(),
                flight.getDepartureAirport().getCreatedAt(),
                flight.getDepartureAirport().getUpdatedAt()
            );
        }
        if (flight.getArrivalAirport() != null) {
            this.arrivalAirport = new AirportDTO(
                flight.getArrivalAirport().getId(),
                flight.getArrivalAirport().getAirportCode(),
                flight.getArrivalAirport().getAirportName(),
                flight.getArrivalAirport().getLatitude(),
                flight.getArrivalAirport().getLongitude(),
                flight.getArrivalAirport().getCity(),
                flight.getArrivalAirport().getCountry(),
                flight.getArrivalAirport().getCreatedAt(),
                flight.getArrivalAirport().getUpdatedAt()
            );
        }
    }

    // Constructor hỗ trợ projection từ query trong FlightRepository
    public FlightDTO(Long id, String flightNumber, 
                     String departureAirportCode, String arrivalAirportCode,
                     LocalTime departureTime, LocalTime arrivalTime, LocalDate flightDate,
                     LocalTime actualDepartureTime, LocalTime actualArrivalTime, LocalTime actualDepartureTimeAtArrival,
                     LocalTime arrivalTimeatArrival, String status,
                     LocalDateTime createdAt, LocalDateTime updatedAt, String note,
                     String airline, String checkInCounters, Integer gate) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightDate = flightDate;
        this.actualDepartureTime = actualDepartureTime;
        this.actualArrivalTime = actualArrivalTime;
        this.actualDepartureTimeAtArrival = actualDepartureTimeAtArrival;
        this.arrivalTimeatArrival = arrivalTimeatArrival;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.note = note;
        this.airline = airline;
        this.checkInCounters = checkInCounters;
        this.gate = gate;

        // Chuyển đổi mã sân bay thành AirportDTO; nếu cần bổ sung thêm tên sân bay thì điều chỉnh lại
        if (departureAirportCode != null) {
            this.departureAirport = new AirportDTO(departureAirportCode, null);
        }
        if (arrivalAirportCode != null) {
            this.arrivalAirport = new AirportDTO(arrivalAirportCode, null);
        }
    }

    // Constructor projection không có trường mới (backward compatible)
    public FlightDTO(Long id, String flightNumber, 
                     String departureAirportCode, String arrivalAirportCode,
                     LocalTime departureTime, LocalTime arrivalTime, LocalDate flightDate,
                     LocalTime actualDepartureTime, LocalTime actualArrivalTime, LocalTime actualDepartureTimeAtArrival,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, flightNumber, departureAirportCode, arrivalAirportCode, departureTime, arrivalTime, flightDate,
            actualDepartureTime, actualArrivalTime, actualDepartureTimeAtArrival, null, null, createdAt, updatedAt, null, null, null, null);
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
    public LocalTime getArrivalTimeatArrival() {
        return arrivalTimeatArrival;
    }
    public String getStatus() {
        return status;
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
    public void setArrivalTimeatArrival(LocalTime arrivalTimeatArrival) {
        this.arrivalTimeatArrival = arrivalTimeatArrival;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    // Setters nếu cần...
}
