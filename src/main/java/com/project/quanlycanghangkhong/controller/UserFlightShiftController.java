package com.project.quanlycanghangkhong.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.quanlycanghangkhong.dto.ApplyFlightShiftRequest;
import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseDTO;
import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseSearchDTO;
import com.project.quanlycanghangkhong.model.UserFlightShift;
import com.project.quanlycanghangkhong.service.UserFlightShiftService;

@RestController
@RequestMapping("/api/user-flight-shifts")
@CrossOrigin(origins = "*")
public class UserFlightShiftController {

    @Autowired
    private UserFlightShiftService userFlightShiftService;

    // Lấy ca trực theo ngày
    @GetMapping("/by-date")
    public ResponseEntity<List<UserFlightShift>> getShiftsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserFlightShift> shifts = userFlightShiftService.getShiftsByDate(date);
        return ResponseEntity.ok(shifts);
    }

    // Lấy ca trực theo user
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<UserFlightShift>> getShiftsByUser(@PathVariable Integer userId) {
        List<UserFlightShift> shifts = userFlightShiftService.getShiftsByUser(userId);
        return ResponseEntity.ok(shifts);
    }

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

    // GET: lấy danh sách user-flight-shifts theo flightId và shiftDate
    @GetMapping("/shifts")
    public ResponseEntity<List<UserFlightShiftResponseDTO>> getShiftsByFlightAndDate(
            @RequestParam("flightId") Long flightId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserFlightShiftResponseDTO> shifts = userFlightShiftService.getShiftsByFlightAndDate(flightId, date);
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/shifts/available")
    public ResponseEntity<List<UserFlightShiftResponseDTO>> getAvailableShifts(
            @RequestParam("flightId") Long flightId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserFlightShiftResponseDTO> shifts = userFlightShiftService.getAvailableShifts(flightId, date);
        return ResponseEntity.ok(shifts);
    }

    @GetMapping("/shifts/assigned")
    public ResponseEntity<List<UserFlightShiftResponseDTO>> getAssignedShifts(
            @RequestParam(value = "flightId", required = false) Long flightId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserFlightShiftResponseDTO> shifts = userFlightShiftService.getAssignedShifts(flightId, date);
        return ResponseEntity.ok(shifts);
    }

    @DeleteMapping
    public ResponseEntity<?> removeFlightAssignment(
            @RequestParam("flightId") Long flightId,
            @RequestParam("shiftDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDate,
            @RequestParam("userId") Integer userId) {
        userFlightShiftService.removeFlightAssignment(flightId, shiftDate, userId);
        return ResponseEntity.ok().build();
    }

    // Endpoint GET: Kiểm tra nếu một user đã phục vụ chuyến bay vào ngày shiftDate
    @GetMapping("/isAssigned")
    public ResponseEntity<Map<String, Boolean>> isUserAssigned(
            @RequestParam("shiftDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDate,
            @RequestParam("userId") Integer userId) {
        boolean assigned = userFlightShiftService.isUserAssignedToFlight(shiftDate, userId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("assigned", assigned);
        return ResponseEntity.ok(result);
    }

    // Endpoint GET: Lọc lịch phục vụ chuyến bay theo shiftDate, teamId, unitId,
    // flightId (flightId là tùy chọn)
    @GetMapping("/filter")
    public ResponseEntity<List<UserFlightShiftResponseSearchDTO>> getFlightSchedules(
            @RequestParam("shiftDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDate,
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "unitId", required = false) Integer unitId,
            @RequestParam(value = "flightId", required = false) Long flightId) {
        List<UserFlightShiftResponseSearchDTO> schedules = userFlightShiftService
                .getFlightSchedulesByCriteria(shiftDate, teamId, unitId, flightId);
        return ResponseEntity.ok(schedules);
    }
}
