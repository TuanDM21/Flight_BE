package com.project.quanlycanghangkhong.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.quanlycanghangkhong.model.FlightAssignment;

public interface FlightAssignmentRepository extends JpaRepository<FlightAssignment, Long> {
    // Nếu cần truy vấn theo userShift, flight,... có thể thêm phương thức custom
    List<FlightAssignment> findByFlight_Id(Long flightId);

}
