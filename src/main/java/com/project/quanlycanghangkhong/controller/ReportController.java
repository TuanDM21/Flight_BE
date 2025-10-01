package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.model.ReportType;
import com.project.quanlycanghangkhong.dto.request.ReportRequest;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.response.ReportFieldsResponse;
import com.project.quanlycanghangkhong.dto.response.ReportResponse;
import com.project.quanlycanghangkhong.dto.response.report.ReportTypesApiResponse;
import com.project.quanlycanghangkhong.dto.response.report.ReportFieldsApiResponse;
import com.project.quanlycanghangkhong.dto.response.report.GeneratedReportApiResponse;

import com.project.quanlycanghangkhong.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import java.util.List;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ReportController - Controller quản lý các API tạo báo cáo
 * 
 * Hỗ trợ 6 loại báo cáo:
 * 1. TASK_REPORT - Báo cáo công việc theo TaskType
 * 2. RECIPIENT_PERFORMANCE_REPORT - Báo cáo hiệu suất người thực hiện
 * 3. ASSIGNMENT_TRACKING_REPORT - Báo cáo theo dõi phân công
 * 4. TASK_STATUS_REPORT - Báo cáo trạng thái công việc  
 * 5. TEAM_WORKLOAD_REPORT - Báo cáo khối lượng công việc team
 * 6. OVERDUE_ANALYSIS_REPORT - Báo cáo phân tích quá hạn
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Report Management", description = "APIs quản lý báo cáo thống kê")
public class ReportController {

    private final ReportService reportService;

    /**
     * Lấy danh sách các loại báo cáo có sẵn
     */
    @GetMapping("/types")
    @Operation(summary = "Lấy danh sách loại báo cáo", 
               description = "Trả về danh sách tất cả các loại báo cáo có thể tạo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ReportTypesApiResponse.class))),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<ReportType>>> getAvailableReportTypes() {
        log.info("Getting available report types");
        
        ApiResponseCustom<List<ReportType>> response = reportService.getAvailableReportTypes();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * Lấy danh sách fields có thể chọn cho một loại báo cáo
     */
    @GetMapping("/fields")
    @Operation(summary = "Lấy danh sách fields cho báo cáo", 
               description = "Trả về danh sách các trường dữ liệu có thể chọn cho một loại báo cáo cụ thể")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy fields thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ReportFieldsApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "Loại báo cáo không hợp lệ",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<ReportFieldsResponse>> getReportFields(
            @Parameter(description = "Loại báo cáo", example = "TASK_REPORT", required = true)
            @RequestParam ReportType reportType) {
        
        log.info("Getting report fields for type: {}", reportType);
        
        ApiResponseCustom<ReportFieldsResponse> response = reportService.getReportFields(reportType);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * Tạo báo cáo theo yêu cầu
     */
    @PostMapping("/generate")
    @Operation(summary = "Tạo báo cáo", 
               description = "Tạo báo cáo theo loại và các tham số được chỉ định. Hỗ trợ 6 loại báo cáo với thống kê phần trăm.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo báo cáo thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GeneratedReportApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi tạo báo cáo",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<ReportResponse>> generateReport(
            @Parameter(description = "Thông tin yêu cầu tạo báo cáo", required = true)
            @Valid @RequestBody ReportRequest request) {
        
        log.info("Generating report: {} for period {} to {}", 
                request.getReportType(), request.getStartDate(), request.getEndDate());
        
        ApiResponseCustom<ReportResponse> response = reportService.generateReport(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // ===================== EXPORT ENDPOINTS =====================

    /**
     * Export báo cáo ra file Excel
     */
    @PostMapping("/export/excel")
    @Operation(summary = "Export báo cáo ra Excel", 
               description = "Tạo và export báo cáo ra file Excel (.xlsx)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export thành công",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi export")
    })
    public ResponseEntity<byte[]> exportReportToExcel(
            @Parameter(description = "Thông tin yêu cầu tạo báo cáo để export", required = true)
            @Valid @RequestBody ReportRequest request) {
        
        log.info("Exporting report to Excel: {} for period {} to {}", 
                request.getReportType(), request.getStartDate(), request.getEndDate());
        
        try {
            ApiResponseCustom<byte[]> result = reportService.exportReportToExcel(request);
            if (!result.isSuccess()) {
                log.error("Export failed: {}", result.getMessage());
                return ResponseEntity.status(result.getStatusCode()).body(null);
            }
            
            byte[] excelFile = result.getData();
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"report_" + request.getReportType() + ".xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelFile);
        } catch (Exception e) {
            log.error("Error exporting to Excel: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Export báo cáo ra file CSV
     */
    @PostMapping("/export/csv")
    @Operation(summary = "Export báo cáo ra CSV", 
               description = "Tạo và export báo cáo ra file CSV")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export thành công",
            content = @Content(mediaType = "text/csv")),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi export")
    })
    public ResponseEntity<byte[]> exportReportToCsv(
            @Parameter(description = "Thông tin yêu cầu tạo báo cáo để export", required = true)
            @Valid @RequestBody ReportRequest request) {
        
        log.info("Exporting report to CSV: {} for period {} to {}", 
                request.getReportType(), request.getStartDate(), request.getEndDate());
        
        try {
            ApiResponseCustom<byte[]> result = reportService.exportReportToCsv(request);
            if (!result.isSuccess()) {
                log.error("Export failed: {}", result.getMessage());
                return ResponseEntity.status(result.getStatusCode()).body(null);
            }
            
            byte[] csvFile = result.getData();
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"report_" + request.getReportType() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvFile);
        } catch (Exception e) {
            log.error("Error exporting to CSV: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Export báo cáo ra file Word
     */
    @PostMapping("/export/word")
    @Operation(summary = "Export báo cáo ra Word", 
               description = "Tạo và export báo cáo ra file Word (.docx)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export thành công",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi export")
    })
    public ResponseEntity<byte[]> exportReportToWord(
            @Parameter(description = "Thông tin yêu cầu tạo báo cáo để export", required = true)
            @Valid @RequestBody ReportRequest request) {
        
        log.info("Exporting report to Word: {} for period {} to {}", 
                request.getReportType(), request.getStartDate(), request.getEndDate());
        
        try {
            // Sử dụng Word export thực sự
            ApiResponseCustom<byte[]> result = reportService.exportReportToWord(request);
            if (!result.isSuccess()) {
                log.error("Export failed: {}", result.getMessage());
                return ResponseEntity.status(result.getStatusCode()).body(null);
            }
            
            byte[] wordFile = result.getData();
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"report_" + request.getReportType() + ".docx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(wordFile);
        } catch (Exception e) {
            log.error("Error exporting to Word: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
