package com.project.quanlycanghangkhong.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightTimeUpdateRequest {
    private String actualDepartureTime;
    private String actualArrivalTime;
    private String actualDepartureTimeAtArrival;
    private String eventType;

    // 1) Default constructor bắt buộc cho Jackson
    public FlightTimeUpdateRequest() {}

    // 2) (Optional) constructor có args
    public FlightTimeUpdateRequest(String actualDepartureTime,
                                   String actualArrivalTime,
                                   String actualDepartureTimeAtArrival) {
        this.actualDepartureTime = actualDepartureTime;
        this.actualArrivalTime = actualArrivalTime;
        this.actualDepartureTimeAtArrival = actualDepartureTimeAtArrival;
    }

    // getters & setters
    public String getActualDepartureTime() {
        return actualDepartureTime;
    }
    public void setActualDepartureTime(String actualDepartureTime) {
        this.actualDepartureTime = actualDepartureTime;
    }
    public String getActualArrivalTime() {
        return actualArrivalTime;
    }
    public void setActualArrivalTime(String actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }
    public String getActualDepartureTimeAtArrival() {
        return actualDepartureTimeAtArrival;
    }
    public void setActualDepartureTimeAtArrival(String actualDepartureTimeAtArrival) {
        this.actualDepartureTimeAtArrival = actualDepartureTimeAtArrival;
    }
    public String getEventType() {
        return eventType;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
