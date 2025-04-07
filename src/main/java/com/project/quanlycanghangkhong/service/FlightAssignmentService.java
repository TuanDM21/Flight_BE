package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.model.FlightAssignment;
import java.util.List;
import java.util.Optional;

public interface FlightAssignmentService {
    FlightAssignment assignFlight(Integer userShiftId, Long flightId);
    Optional<FlightAssignment> getFlightAssignmentById(Long id);
    List<FlightAssignment> getAllFlightAssignments();
    void deleteFlightAssignment(Long id);
}
