package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;

    // Giao công việc (tạo mới assignment)
    @PostMapping
    public ResponseEntity<ApiResponseCustom<AssignmentDTO>> createAssignment(@RequestBody AssignmentDTO dto) {
        AssignmentDTO result = assignmentService.createAssignment(dto);
        return ResponseEntity.status(201).body(ApiResponseCustom.created(result));
    }

    // Cập nhật giao công việc
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<AssignmentDTO>> updateAssignment(@PathVariable Integer id, @RequestBody AssignmentDTO dto) {
        AssignmentDTO result = assignmentService.updateAssignment(id, dto);
        if (result == null) return ResponseEntity.status(404).body(ApiResponseCustom.error(HttpStatus.NOT_FOUND, "Không tìm thấy assignment"));
        return ResponseEntity.ok(ApiResponseCustom.success("Cập nhật thành công", result));
    }

    // Xoá giao công việc
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<Void>> deleteAssignment(@PathVariable Integer id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(ApiResponseCustom.success("Xoá thành công", null));
    }

    // Xem chi tiết giao công việc
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<AssignmentDTO>> getAssignmentById(@PathVariable Integer id) {
        AssignmentDTO result = assignmentService.getAssignmentById(id);
        if (result == null) return ResponseEntity.status(404).body(ApiResponseCustom.error(HttpStatus.NOT_FOUND, "Không tìm thấy assignment"));
        return ResponseEntity.ok(ApiResponseCustom.success(result));
    }   

    // Lấy danh sách giao công việc theo task
    @GetMapping("/task/{taskId}")
    public ResponseEntity<ApiResponseCustom<List<AssignmentDTO>>> getAssignmentsByTaskId(@PathVariable Integer taskId) {
        List<AssignmentDTO> result = assignmentService.getAssignmentsByTaskId(taskId);
        return ResponseEntity.ok(ApiResponseCustom.success(result));
    }
}
