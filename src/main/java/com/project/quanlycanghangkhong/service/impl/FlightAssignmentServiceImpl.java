//package com.project.quanlycanghangkhong.service.impl;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.project.quanlycanghangkhong.request.ApplyFlightShiftRequest;
//import com.project.quanlycanghangkhong.dto.FlightAssignmentDTO;
//import com.project.quanlycanghangkhong.model.Flight;
//import com.project.quanlycanghangkhong.model.FlightAssignment;
//import com.project.quanlycanghangkhong.model.User;
//import com.project.quanlycanghangkhong.model.UserShift;
//import com.project.quanlycanghangkhong.repository.FlightAssignmentRepository;
//import com.project.quanlycanghangkhong.repository.FlightRepository;
//import com.project.quanlycanghangkhong.repository.UserRepository;
//import com.project.quanlycanghangkhong.repository.UserShiftRepository;
//import com.project.quanlycanghangkhong.service.FlightAssignmentService;
//
//@Service
//public class FlightAssignmentServiceImpl implements FlightAssignmentService {
//	   @Autowired
//	    private FlightAssignmentRepository flightAssignmentRepository;
//
//	    @Autowired
//	    private UserShiftRepository userShiftRepository;
//
//	    @Autowired
//	    private FlightRepository flightRepository;
//	    
//	    @Autowired
//	    private UserRepository userRepository;
//
//
//	    @Override
//	    public FlightAssignmentDTO getFlightAssignmentById(Long id) {
//	        FlightAssignment assignment = flightAssignmentRepository.findById(id)
//	                .orElseThrow(() -> new RuntimeException("Flight assignment not found with id: " + id));
//	        return new FlightAssignmentDTO(assignment);
//	    }
//
//	    @Override
//	    public List<FlightAssignmentDTO> getAllFlightAssignments() {
//	        List<FlightAssignment> assignments = flightAssignmentRepository.findAll();
//	        return assignments.stream()
//	                .map(FlightAssignmentDTO::new)
//	                .collect(Collectors.toList());
//	    }
//
//	    @Override
//	    public FlightAssignmentDTO createFlightAssignment(Long userShiftId, Long flightId) {
//	        // Convert userShiftId from Long to Integer since UserShift IDs are of type Integer.
//	        Integer usId = userShiftId.intValue();
//	        UserShift userShift = userShiftRepository.findById(usId)
//	                .orElseThrow(() -> new RuntimeException("UserShift not found with id: " + userShiftId));
//	        Flight flight = flightRepository.findById(flightId)
//	                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
//
//	        FlightAssignment assignment = new FlightAssignment(userShift, flight);
//	        FlightAssignment saved = flightAssignmentRepository.save(assignment);
//	        return new FlightAssignmentDTO(saved);
//	    }
//
//	    @Override
//	    public FlightAssignmentDTO updateFlightAssignment(Long id, Long newFlightId) {
//	        FlightAssignment assignment = flightAssignmentRepository.findById(id)
//	                .orElseThrow(() -> new RuntimeException("Flight assignment not found with id: " + id));
//	        Flight newFlight = flightRepository.findById(newFlightId)
//	                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + newFlightId));
//	        assignment.setFlight(newFlight);
//	        FlightAssignment updated = flightAssignmentRepository.save(assignment);
//	        return new FlightAssignmentDTO(updated);
//	    }
//
//	    @Override
//	    public void deleteFlightAssignment(Long id) {
//	        flightAssignmentRepository.deleteById(id);
//	    }
//	    @Override
//	    @Transactional
//	    public void applyFlightShift(ApplyFlightShiftRequest request) {
//	    	 Flight flight = flightRepository.findById(request.getFlightId())
//	    	            .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến bay với id: " + request.getFlightId()));
//	    	    
//	    	    // Nếu flightDate là null, sử dụng ngày hiện tại làm fallback
//	    	    LocalDate shiftDate = flight.getFlightDate() != null ? flight.getFlightDate() : LocalDate.now();
//
//	    	    for (I userId : request.getUserIds()) {
//	    	        User user = userRepository.findById(userId)
//	    	                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với id: " + userId));
//	    	        
//	    	        UserShift userShift = new UserShift();
//	    	        userShift.setUser(user);
//	    	        userShift.setFlight(flight);
//	    	        userShift.setShiftDate(shiftDate);
//	    	        userShift = userShiftRepository.save(userShift);
//
//	    	        FlightAssignment flightAssignment = new FlightAssignment();
//	    	        flightAssignment.setUserShift(userShift);
//	    	        flightAssignment.setFlight(flight);
//	    	        flightAssignmentRepository.save(flightAssignment);
//	    	    }
//	    }
//}
