package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserShiftDTO {
    private Integer id;
    private UserDTO user;
    private LocalDate shiftDate;
    private Integer shiftId; // Nếu có ca trực, chỉ lưu id của ca
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public UserShiftDTO() {}

    public UserShiftDTO(Integer id, UserDTO user, LocalDate shiftDate, Integer shiftId,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.shiftDate = shiftDate;
        this.shiftId = shiftId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public UserDTO getUser() {
        return user;
    }
    public void setUser(UserDTO user) {
        this.user = user;
    }
    public LocalDate getShiftDate() {
        return shiftDate;
    }
    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }
    public Integer getShiftId() {
        return shiftId;
    }
    public void setShiftId(Integer shiftId) {
        this.shiftId = shiftId;
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
