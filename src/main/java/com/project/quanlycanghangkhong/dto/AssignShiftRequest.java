package com.project.quanlycanghangkhong.dto;

public class AssignShiftRequest {
    private Integer userId;
    private String shiftDate; // định dạng YYYY-MM-DD
    private Integer shiftId;

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getShiftDate() {
        return shiftDate;
    }
    public void setShiftDate(String shiftDate) {
        this.shiftDate = shiftDate;
    }
    public Integer getShiftId() {
        return shiftId;
    }
    public void setShiftId(Integer shiftId) {
        this.shiftId = shiftId;
    }
}
