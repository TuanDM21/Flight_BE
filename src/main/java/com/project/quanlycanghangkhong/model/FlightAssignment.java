package com.project.quanlycanghangkhong.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import com.project.quanlycanghangkhong.config.VietnamTimestampListener;

@Entity
@Table(name = "flight_assignments")
@EntityListeners(VietnamTimestampListener.class)
public class FlightAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết đến bảng user_shifts - Đảm bảo là Integer
    @ManyToOne
    @JoinColumn(name = "user_shift_id", nullable = false)
    private UserShift userShift;

    // Liên kết đến bảng flights
    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public FlightAssignment() {}

    public FlightAssignment(UserShift userShift, Flight flight) {
        this.userShift = userShift;
        this.flight = flight;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserShift getUserShift() {
		return userShift;
	}

	public void setUserShift(UserShift userShift) {
		this.userShift = userShift;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
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
