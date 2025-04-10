package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.ApplyFlightShiftRequest;
import com.project.quanlycanghangkhong.service.UserFlightShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-flight-shifts")
@CrossOrigin(origins = "*")
public class UserFlightShiftController {

    @Autowired
    private UserFlightShiftService userFlightShiftService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyFlightShift(@RequestBody ApplyFlightShiftRequest request) {
        try {
            userFlightShiftService.applyFlightShift(request);
            return ResponseEntity.ok("Ca trực theo chuyến bay đã được áp dụng thành công.");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
