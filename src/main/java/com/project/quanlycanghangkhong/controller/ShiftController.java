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
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponseCustom<List<ShiftDTO>>> getAllShifts() {
        List<ShiftDTO> shifts = shiftService.getAllShiftsForCurrentUser();
        return ResponseEntity.ok(ApiResponseCustom.success(shifts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<ShiftDTO>> getShiftById(@PathVariable Integer id) {
        Optional<Shift> shift = shiftService.getShiftById(id);
        if (shift.isPresent()) {
            return ResponseEntity.ok(ApiResponseCustom.success(shiftService.toDTO(shift.get())));
        } else {
            return ResponseEntity.ok(ApiResponseCustom.notFound("Shift not found"));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponseCustom<ShiftDTO>> createShift(@RequestBody Shift shift) {
        // Kiểm tra xem mã ca trực đã tồn tại chưa
        if (shiftService.findByShiftCode(shift.getShiftCode()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseCustom.error("Mã lịch trực đã tồn tại. Vui lòng chọn mã khác."));
        }
        try {
            // Lấy user hiện tại và set team cho shift
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            if (user.getTeam() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseCustom.error("User không thuộc team nào!"));
            }
            shift.setTeam(user.getTeam());
            Shift newShift = shiftService.createShift(shift);
            return ResponseEntity.ok(ApiResponseCustom.created(shiftService.toDTO(newShift)));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseCustom.internalError("Đã xảy ra lỗi khi tạo lịch trực."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<ShiftDTO>> updateShift(@PathVariable Integer id, @RequestBody Shift shiftData) {
        // Kiểm tra xem mã ca trực mới đã tồn tại và không thuộc về bản ghi hiện tại
        Optional<Shift> existingShift = shiftService.findByShiftCode(shiftData.getShiftCode());
        if (existingShift.isPresent() && !existingShift.get().getId().equals(id)) {
             return ResponseEntity.badRequest()
                    .body(ApiResponseCustom.error("Mã lịch trực đã tồn tại. Vui lòng chọn mã khác."));
        }
        Shift updatedShift = shiftService.updateShift(id, shiftData);
        if (updatedShift != null) {
            return ResponseEntity.ok(ApiResponseCustom.updated(shiftService.toDTO(updatedShift)));
        } else {
            return ResponseEntity.ok(ApiResponseCustom.notFound("Shift not found"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<Object>> deleteShift(@PathVariable Integer id) {
        shiftService.deleteShift(id);
        return ResponseEntity.ok(ApiResponseCustom.deleted());
    }
}
