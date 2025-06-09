package com.project.quanlycanghangkhong.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import com.project.quanlycanghangkhong.config.VietnamTimestampListener;

@Entity
@Table(name = "user_flight_shifts", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "flight_id",
        "shift_date" }))
@EntityListeners(VietnamTimestampListener.class)
public class UserFlightShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Liên kết đến bảng User (nhân viên)
    @ManyToOne(fetch = FetchType.LAZY)
    // Sửa kiểu dữ liệu để phù hợp với User.id (Integer)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Liên kết đến bảng Flight (chuyến bay)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    // Ngày ca trực (shiftDate) – dùng để phân biệt ca trực của cùng một chuyến bay
    // qua các ngày khác nhau
    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructor không tham số
    public UserFlightShift() {
    }

    // Constructor có tham số
    public UserFlightShift(User user, Flight flight, LocalDate shiftDate) {
        this.user = user;
        this.flight = flight;
        this.shiftDate = shiftDate;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
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
