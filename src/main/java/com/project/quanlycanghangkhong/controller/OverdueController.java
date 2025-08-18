package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.service.OverdueTaskService;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller để quản lý các tác vụ liên quan đến task/assignment overdue
 */
@RestController
@RequestMapping("/api/overdue")
@CrossOrigin(origins = "*")
public class OverdueController {

    @Autowired
    private OverdueTaskService overdueTaskService;

    /**
     * Cập nhật trạng thái overdue cho tất cả task/assignment
     * Manual trigger cho scheduled job
     */
    @PostMapping("/update")
    @Operation(summary = "Cập nhật trạng thái overdue", 
               description = "Manual trigger để cập nhật trạng thái overdue cho tasks và assignments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<ApiResponseCustom<String>> updateOverdueStatus() {
        try {
            overdueTaskService.forceUpdateOverdueStatus();
            return ResponseEntity.ok(ApiResponseCustom.success(
                "Đã cập nhật trạng thái overdue thành công", 
                "Overdue status updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseCustom.error(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Lỗi khi cập nhật trạng thái overdue: " + e.getMessage()
            ));
        }
    }

    /**
     * Kiểm tra một task có overdue không
     */
    @GetMapping("/check-task/{taskId}")
    @Operation(summary = "Kiểm tra task overdue", 
               description = "Kiểm tra một task cụ thể có overdue hay không")
    public ResponseEntity<ApiResponseCustom<Map<String, Boolean>>> checkTaskOverdue(@PathVariable Integer taskId) {
        try {
            boolean isOverdue = overdueTaskService.isTaskOverdue(taskId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("isOverdue", isOverdue);
            
            return ResponseEntity.ok(ApiResponseCustom.success(
                isOverdue ? "Task đang overdue" : "Task không overdue",
                result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseCustom.error(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Lỗi khi kiểm tra task overdue: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy số lượng task overdue của user hiện tại
     */
    @GetMapping("/count/{userId}")
    @Operation(summary = "Đếm task overdue của user", 
               description = "Lấy số lượng task overdue của một user cụ thể")
    public ResponseEntity<ApiResponseCustom<Map<String, Long>>> getOverdueTaskCount(@PathVariable Integer userId) {
        try {
            long overdueCount = overdueTaskService.getOverdueTaskCountForUser(userId);
            Map<String, Long> result = new HashMap<>();
            result.put("overdueCount", overdueCount);
            
            return ResponseEntity.ok(ApiResponseCustom.success(
                "Lấy số lượng task overdue thành công",
                result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseCustom.error(
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                "Lỗi khi đếm task overdue: " + e.getMessage()
            ));
        }
    }
}
