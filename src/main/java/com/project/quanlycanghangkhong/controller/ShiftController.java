package com.project.quanlycanghangkhong.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

import com.project.quanlycanghangkhong.model.Shift;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.service.ShiftService;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.dto.ShiftDTO;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<ShiftDTO> getAllShifts() {
        return shiftService.getAllShiftsForCurrentUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftDTO> getShiftById(@PathVariable Integer id) {
        return shiftService.getShiftById(id)
                .map(shift -> ResponseEntity.ok(shiftService.toDTO(shift)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createShift(@RequestBody Shift shift) {
        // Kiểm tra xem mã ca trực đã tồn tại chưa
        if (shiftService.findByShiftCode(shift.getShiftCode()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Mã lịch trực đã tồn tại. Vui lòng chọn mã khác.");
        }
        try {
            // Lấy user hiện tại và set team cho shift
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            if (user.getTeam() == null) {
                return ResponseEntity.badRequest().body("User không thuộc team nào!");
            }
            shift.setTeam(user.getTeam());
            Shift newShift = shiftService.createShift(shift);
            return ResponseEntity.ok(shiftService.toDTO(newShift)); // Trả về ShiftDTO thay vì entity Shift
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi tạo lịch trực.");
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateShift(@PathVariable Integer id, @RequestBody Shift shiftData) {
        // Kiểm tra xem mã ca trực mới đã tồn tại và không thuộc về bản ghi hiện tại
        Optional<Shift> existingShift = shiftService.findByShiftCode(shiftData.getShiftCode());
        if (existingShift.isPresent() && !existingShift.get().getId().equals(id)) {
             return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Mã lịch trực đã tồn tại. Vui lòng chọn mã khác.");
        }
        Shift updatedShift = shiftService.updateShift(id, shiftData);
        return updatedShift != null 
                ? ResponseEntity.ok(shiftService.toDTO(updatedShift)) // Trả về ShiftDTO thay vì entity Shift
                : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Integer id) {
        shiftService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }
}
