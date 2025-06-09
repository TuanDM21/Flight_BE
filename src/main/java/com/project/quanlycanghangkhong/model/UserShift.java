package com.project.quanlycanghangkhong.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import com.project.quanlycanghangkhong.config.VietnamTimestampListener;

@Entity
@Table(name = "user_shifts")
@EntityListeners(VietnamTimestampListener.class)
public class UserShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;
    
    @ManyToOne
    @JoinColumn(name = "shift_id", nullable = true)
    private Shift shift;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserShift() {}
    
    public UserShift(User user, LocalDate shiftDate, Shift shift) {
        this.user = user;
        this.shiftDate = shiftDate;
        this.shift = shift;
    }
    
    // Getters and Setters
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
    public LocalDate getShiftDate() {
        return shiftDate;
    }
    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }
    public Shift getShift() {
        return shift;
    }
    public void setShift(Shift shift) {
        this.shift = shift;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}