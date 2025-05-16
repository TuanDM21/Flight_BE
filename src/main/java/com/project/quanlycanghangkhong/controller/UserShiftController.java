package com.project.quanlycanghangkhong.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import com.project.quanlycanghangkhong.dto.ApplyShiftMultiDTO;
import com.project.quanlycanghangkhong.dto.AssignShiftRequest;
import com.project.quanlycanghangkhong.dto.ScheduleDTO;
import com.project.quanlycanghangkhong.dto.UserShiftDTO;
import com.project.quanlycanghangkhong.service.UserShiftService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user-shifts")
public class UserShiftController {

    @Autowired
    private UserShiftService userShiftService;

    @GetMapping
    public ResponseEntity<List<UserShiftDTO>> getAllUserShifts() {
        List<UserShiftDTO> dtos = userShiftService.getAllUserShifts();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserShiftDTO> getUserShiftById(@PathVariable Integer id) {
        Optional<UserShiftDTO> dto = userShiftService.getUserShiftById(id);
        return dto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint gán ca trực cho 1 user (assign)
    @PostMapping("/assign")
    public ResponseEntity<?> assignShiftToUser(@RequestBody AssignShiftRequest request) {
        try {
            LocalDate shiftDate = LocalDate.parse(request.getShiftDate());
            UserShiftDTO dto = userShiftService.assignShiftToUser(request.getUserId(), shiftDate, request.getShiftId());
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            // Ví dụ: nếu user đã có ca trực, ném exception với thông báo conflict
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserShift(
            @PathVariable Integer id,
            @RequestBody AssignShiftRequest request) {
        try {
            // Chuyển đổi shiftDate từ String sang LocalDate
            LocalDate newShiftDate = LocalDate.parse(request.getShiftDate());
            UserShiftDTO dto = userShiftService.updateUserShift(id, request.getShiftId(), newShiftDate);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserShift(@PathVariable Integer id) {
        userShiftService.deleteUserShift(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint lọc lịch trực theo ngày, team và unit, trả về ScheduleDTO
    @GetMapping("/filter")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByCriteria(
            @RequestParam("shiftDate") String shiftDateStr,
            @RequestParam(value = "teamId", required = false) Integer teamId,
            @RequestParam(value = "unitId", required = false) Integer unitId) {
        LocalDate shiftDate = LocalDate.parse(shiftDateStr);
        List<ScheduleDTO> dtos = userShiftService.getSchedulesByCriteria(shiftDate, teamId, unitId);
        return ResponseEntity.ok(dtos);
    }

    // Endpoint áp dụng ca cho nhiều user (apply-multi)
    @PostMapping("/apply-multi")
    public ResponseEntity<?> applyShiftToUsers(@RequestBody ApplyShiftMultiDTO dto) {
        try {
            List<UserShiftDTO> result = userShiftService.applyShiftToUsers(dto);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }

    @GetMapping("/filter-by-user-and-range")
    public ResponseEntity<List<ScheduleDTO>> filterByUserAndRange(
        @RequestParam("userId") Integer userId,
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String startDateStr,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String endDateStr
    ) {
        // Chuyển đổi string sang LocalDate
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        // Gọi service để lấy danh sách lịch trực theo user và khoảng ngày
        List<ScheduleDTO> dtos = userShiftService.getSchedulesByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(dtos);
    }

}
