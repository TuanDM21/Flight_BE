package com.project.quanlycanghangkhong.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;

@Entity
@Table(name = "user_flight_shifts", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "flight_id", "shift_date"}))
public class UserFlightShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết đến bảng User (nhân viên)
    @ManyToOne
    // Chỉ định kiểu cột là INT để tương thích với User.id (Integer)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private User user;

    // Liên kết đến bảng Flight (chuyến bay)
    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    // Ngày ca trực (shiftDate) – dùng để phân biệt ca trực của cùng một chuyến bay qua các ngày khác nhau
    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructor không tham số
    public UserFlightShift() {}

    // Constructor có tham số
    public UserFlightShift(User user, Flight flight, LocalDate shiftDate) {
        this.user = user;
        this.flight = flight;
        this.shiftDate = shiftDate;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
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
