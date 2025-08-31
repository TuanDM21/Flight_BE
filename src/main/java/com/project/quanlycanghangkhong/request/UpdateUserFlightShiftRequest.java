package com.project.quanlycanghangkhong.request;

import java.time.LocalDate;

public class UpdateUserFlightShiftRequest {
    private LocalDate shiftDate;
    private Long flightId;

    public LocalDate getShiftDate() {
        return shiftDate;
    }
    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }
    public Long getFlightId() {
        return flightId;
    }
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
}
