package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.request.TaskReportRequest;
import com.project.quanlycanghangkhong.dto.response.TaskReportResponse;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.service.TaskReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report Management", description = "APIs for generating reports")
public class TaskReportController {

    private final TaskReportService taskReportService;

    @PostMapping("/tasks")
    @Operation(summary = "Tạo báo cáo Task/Assignment", 
               description = "Tạo báo cáo chi tiết về Task và Assignment theo điều kiện lọc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo báo cáo thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "500", description = "Lỗi hệ thống",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<TaskReportResponse>> generateTaskReport(
            @Valid @RequestBody TaskReportRequest request) {
        
        log.info("Request to generate task report: {}", request);
        
        ApiResponseCustom<TaskReportResponse> response = taskReportService.generateReport(request);
        
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
