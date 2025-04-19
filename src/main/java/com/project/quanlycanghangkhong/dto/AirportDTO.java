package com.project.quanlycanghangkhong.dto;

public class AirportDTO {
    private String airportCode;
    private String airportName;

    public AirportDTO() {}

    public AirportDTO(String airportCode, String airportName) {
        this.airportCode = airportCode;
        this.airportName = airportName;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "AirportDTO{" +
               "airportCode='" + airportCode + '\'' +
               ", airportName='" + airportName + '\'' +
               '}';
    }
}
