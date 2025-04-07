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

import com.project.quanlycanghangkhong.model.Shift;
import com.project.quanlycanghangkhong.service.ShiftService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @GetMapping
    public List<Shift> getAllShifts() {
        return shiftService.getAllShifts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shift> getShiftById(@PathVariable Integer id) {
        return shiftService.getShiftById(id)
                .map(ResponseEntity::ok)
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
            Shift newShift = shiftService.createShift(shift);
            return ResponseEntity.ok(newShift);
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
                ? ResponseEntity.ok(updatedShift) 
                : ResponseEntity.notFound().build();
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Integer id) {
        shiftService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }
}
