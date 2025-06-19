package com.project.quanlycanghangkhong.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AirportDTO {
    private Long id;
    private String airportCode;
    private String airportName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String city;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AirportDTO() {}

    // Constructor cũ để tương thích ngược với FlightDTO
    public AirportDTO(String airportCode, String airportName) {
        this.airportCode = airportCode;
        this.airportName = airportName;
    }

    // Constructor đầy đủ cho Airport entities
    public AirportDTO(Long id, String airportCode, String airportName, BigDecimal latitude, 
                     BigDecimal longitude, String city, String country, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.airportCode = airportCode;
        this.airportName = airportName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    @Override
    public String toString() {
        return "AirportDTO{" +
               "id=" + id +
               ", airportCode='" + airportCode + '\'' +
               ", airportName='" + airportName + '\'' +
               ", latitude=" + latitude +
               ", longitude=" + longitude +
               ", city='" + city + '\'' +
               ", country='" + country + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
