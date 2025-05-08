package com.project.quanlycanghangkhong.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Flight number is required")
    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

   @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_airport_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Airport arrivalAirport;
    
    @NotNull(message = "Departure time is required")
    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @NotNull(message = "Arrival time is required")
    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @NotNull(message = "Flight date is required")
    @Column(name = "flight_date", nullable = false)
    private LocalDate flightDate;

    // --- Các trường thêm vào ---

    // Giờ cất cánh thực tế tại sân bay đi
    @Column(name = "actual_departure_time")
    private LocalTime actualDepartureTime;

    // Giờ hạ cánh thực tế tại sân bay đến
    @Column(name = "actual_arrival_time")
    private LocalTime actualArrivalTime;

    // Giờ cất cánh thực tế tại sân bay đến (nếu có, ví dụ phục vụ trường hợp
    // turnaround)
    @Column(name = "actual_departure_time_at_arrival")
    private LocalTime actualDepartureTimeAtArrival;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructor không tham số
    public Flight() {
    }

    // Constructor có tham số
    public Flight(String flightNumber, Airport departureAirport, Airport arrivalAirport,
            LocalTime departureTime, LocalTime arrivalTime, LocalDate flightDate) {
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightDate = flightDate;
    }

    // Constructor có tham số (bổ sung note)
    public Flight(String flightNumber, Airport departureAirport, Airport arrivalAirport,
            LocalTime departureTime, LocalTime arrivalTime, LocalDate flightDate, String note) {
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightDate = flightDate;
        this.note = note;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
