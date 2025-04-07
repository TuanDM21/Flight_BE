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

    @PostMapping("/assign")
    public ResponseEntity<UserShiftDTO> assignShiftToUser(@RequestBody AssignShiftRequest request) {
        // Giả sử AssignShiftRequest có các trường: userId, shiftDate (String), shiftId
        LocalDate shiftDate = LocalDate.parse(request.getShiftDate());
        UserShiftDTO dto = userShiftService.assignShiftToUser(request.getUserId(), shiftDate, request.getShiftId());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserShiftDTO> updateUserShift(
            @PathVariable Integer id,
            @RequestBody AssignShiftRequest request) {
        UserShiftDTO dto = userShiftService.updateUserShift(id, request.getShiftId());
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
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

    @PostMapping("/apply-multi")
    public ResponseEntity<List<UserShiftDTO>> applyShiftToUsers(@RequestBody ApplyShiftMultiDTO dto) {
        List<UserShiftDTO> result = userShiftService.applyShiftToUsers(dto);
        return ResponseEntity.ok(result);
    }
    
    

}
