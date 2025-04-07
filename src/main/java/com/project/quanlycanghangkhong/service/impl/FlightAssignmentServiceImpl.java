package com.project.quanlycanghangkhong.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.quanlycanghangkhong.model.Flight;
import com.project.quanlycanghangkhong.model.FlightAssignment;
import com.project.quanlycanghangkhong.model.UserShift;
import com.project.quanlycanghangkhong.repository.FlightAssignmentRepository;
import com.project.quanlycanghangkhong.repository.FlightRepository;
import com.project.quanlycanghangkhong.repository.UserShiftRepository;
import com.project.quanlycanghangkhong.service.FlightAssignmentService;

@Service
public class FlightAssignmentServiceImpl implements FlightAssignmentService {

    @Autowired
    private FlightAssignmentRepository flightAssignmentRepository;

    @Autowired
    private UserShiftRepository userShiftRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Override
    public FlightAssignment assignFlight(Integer userShiftId, Long flightId) {
        UserShift userShift = userShiftRepository.findById(userShiftId)
                .orElseThrow(() -> new RuntimeException("UserShift not found"));
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        FlightAssignment assignment = new FlightAssignment(userShift, flight);
        return flightAssignmentRepository.save(assignment);
    }

    @Override
    public Optional<FlightAssignment> getFlightAssignmentById(Long id) {
        return flightAssignmentRepository.findById(id);
    }

    @Override
    public List<FlightAssignment> getAllFlightAssignments() {
        return flightAssignmentRepository.findAll();
    }

    @Override
    public void deleteFlightAssignment(Long id) {
        flightAssignmentRepository.deleteById(id);
    }
}
