//package com.project.quanlycanghangkhong.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.project.quanlycanghangkhong.request.ApplyFlightShiftRequest;
//import com.project.quanlycanghangkhong.dto.FlightAssignmentDTO;
//import com.project.quanlycanghangkhong.service.FlightAssignmentService;
//
//@CrossOrigin(origins = "*")
//@RestController
//@RequestMapping("/api/flight-assignments")
//public class FlightAssignmentController {
//
//    @Autowired
//    private FlightAssignmentService flightAssignmentService;
//    
//    @GetMapping("/{id}")
//    public ResponseEntity<FlightAssignmentDTO> getFlightAssignmentById(@PathVariable Long id) {
//        FlightAssignmentDTO dto = flightAssignmentService.getFlightAssignmentById(id);
//        return ResponseEntity.ok(dto);
//    }
//    
//    @GetMapping
//    public ResponseEntity<List<FlightAssignmentDTO>> getAllFlightAssignments() {
//        List<FlightAssignmentDTO> dtos = flightAssignmentService.getAllFlightAssignments();
//        return ResponseEntity.ok(dtos);
//    }
//    
//    // Create Flight Assignment (assign a flight to a user shift)
//    @PostMapping
//    public ResponseEntity<FlightAssignmentDTO> createFlightAssignment(
//            @RequestParam("userShiftId") Long userShiftId,
//            @RequestParam("flightId") Long flightId) {
//        FlightAssignmentDTO dto = flightAssignmentService.createFlightAssignment(userShiftId, flightId);
//        return ResponseEntity.ok(dto);
//    }
//    
//    // Update Flight Assignment: update the flight for an existing assignment
//    @PutMapping("/{id}")
//    public ResponseEntity<FlightAssignmentDTO> updateFlightAssignment(
//            @PathVariable Long id,
//            @RequestParam("flightId") Long newFlightId) {
//        FlightAssignmentDTO dto = flightAssignmentService.updateFlightAssignment(id, newFlightId);
//        return ResponseEntity.ok(dto);
//    }
//    
//    // Delete Flight Assignment
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteFlightAssignment(@PathVariable Long id) {
//        flightAssignmentService.deleteFlightAssignment(id);
//        return ResponseEntity.noContent().build();
//    }
//  
//    @PostMapping("/apply-by-flight")
//    public ResponseEntity<?> applyFlightShift(@RequestBody ApplyFlightShiftRequest request) {
//        try {
//            flightAssignmentService.applyFlightShift(request);
//            return ResponseEntity.ok("Ca trực theo chuyến bay đã được áp dụng thành công.");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }
//    }
//}
