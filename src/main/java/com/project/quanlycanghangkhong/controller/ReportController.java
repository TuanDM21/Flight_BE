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
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Export báo cáo ra file Excel từ data có sẵn
     */
    @PostMapping("/export/excel")
    @Operation(summary = "Export báo cáo ra Excel", 
               description = "Export data báo cáo có sẵn ra file Excel (.xlsx)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export thành công",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi export")
    })
    public ResponseEntity<byte[]> exportReportToExcel(
            @Parameter(description = "Dữ liệu báo cáo cần export", required = true)
            @Valid @RequestBody ReportResponse reportData) {
        
        log.info("Exporting report data to Excel: {}", reportData.getReportType());
        
        try {
            byte[] excelFile = reportService.generateExcelFile(reportData);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"report_" + reportData.getReportType() + ".xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelFile);
        } catch (Exception e) {
            log.error("Error exporting to Excel: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Export báo cáo ra file PDF từ data có sẵn
     */
    @PostMapping("/export/pdf")
    @Operation(summary = "Export báo cáo ra PDF", 
               description = "Export data báo cáo có sẵn ra file PDF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export thành công",
            content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi export")
    })
    public ResponseEntity<byte[]> exportReportToPdf(
            @Parameter(description = "Dữ liệu báo cáo cần export", required = true)
            @Valid @RequestBody ReportResponse reportData) {
        
        log.info("Exporting report data to PDF: {}", reportData.getReportType());
        
        try {
            byte[] pdfFile = reportService.generatePdfFile(reportData);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"report_" + reportData.getReportType() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfFile);
        } catch (Exception e) {
            log.error("Error exporting to PDF: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Export báo cáo ra file CSV từ data có sẵn
     */
    @PostMapping("/export/csv")
    @Operation(summary = "Export báo cáo ra CSV", 
               description = "Export data báo cáo có sẵn ra file CSV")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export thành công",
            content = @Content(mediaType = "text/csv")),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi export")
    })
    public ResponseEntity<byte[]> exportReportToCsv(
            @Parameter(description = "Dữ liệu báo cáo cần export", required = true)
            @Valid @RequestBody ReportResponse reportData) {
        
        log.info("Exporting report data to CSV: {}", reportData.getReportType());
        
        try {
            byte[] csvFile = reportService.generateCsvFile(reportData);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"report_" + reportData.getReportType() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvFile);
        } catch (Exception e) {
            log.error("Error exporting to CSV: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Export báo cáo ra file Word từ data có sẵn
     */
    @PostMapping("/export/word")
    @Operation(summary = "Export báo cáo ra Word", 
               description = "Export data báo cáo có sẵn ra file Word (.docx)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export thành công",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi export")
    })
    public ResponseEntity<byte[]> exportReportToWord(
            @Parameter(description = "Dữ liệu báo cáo cần export", required = true)
            @Valid @RequestBody ReportResponse reportData) {
        
        log.info("Exporting report data to Word: {}", reportData.getReportType());
        
        try {
            byte[] wordFile = reportService.generateWordFile(reportData);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"report_" + reportData.getReportType() + ".docx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(wordFile);
        } catch (Exception e) {
            log.error("Error exporting to Word: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Export báo cáo ra file PowerPoint từ data có sẵn
     */
    @PostMapping("/export/powerpoint")
    @Operation(summary = "Export báo cáo ra PowerPoint", 
               description = "Export data báo cáo có sẵn ra file PowerPoint (.pptx)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export thành công",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.presentationml.presentation")),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi export")
    })
    public ResponseEntity<byte[]> exportReportToPowerPoint(
            @Parameter(description = "Dữ liệu báo cáo cần export", required = true)
            @Valid @RequestBody ReportResponse reportData) {
        
        log.info("Exporting report data to PowerPoint: {}", reportData.getReportType());
        
        try {
            byte[] pptFile = reportService.generatePowerPointFile(reportData);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"report_" + reportData.getReportType() + ".pptx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
                .body(pptFile);
        } catch (Exception e) {
            log.error("Error exporting to PowerPoint: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ===================== QUICK REPORT ENDPOINTS =====================

    /**
     * Tạo nhanh báo cáo công việc theo TaskType (7 ngày gần nhất)
     */
    @GetMapping("/quick/task-report")
    @Operation(summary = "Báo cáo nhanh công việc theo TaskType", 
               description = "Tạo báo cáo công việc theo TaskType cho 7 ngày gần nhất với các trường mặc định")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo báo cáo thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GeneratedReportApiResponse.class))),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<ReportResponse>> getQuickTaskReport() {
        log.info("Generating quick task report for last 7 days");
        
        ReportRequest request = ReportRequest.builder()
            .reportType(ReportType.TASK_REPORT)
            .startDate(java.time.LocalDateTime.now().minusDays(7))
            .endDate(java.time.LocalDateTime.now())
            .selectedFields(List.of("task.title", "task.content", "task.status", "task.createdAt"))
            .build();
        
        ApiResponseCustom<ReportResponse> response = reportService.generateReport(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * Tạo nhanh báo cáo hiệu suất người thực hiện (30 ngày gần nhất)
     */
    @GetMapping("/quick/performance-report")
    @Operation(summary = "Báo cáo nhanh hiệu suất người thực hiện", 
               description = "Tạo báo cáo hiệu suất cho 30 ngày gần nhất với các trường mặc định")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo báo cáo thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GeneratedReportApiResponse.class))),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<ReportResponse>> getQuickPerformanceReport() {
        log.info("Generating quick performance report for last 30 days");
        
        ReportRequest request = ReportRequest.builder()
            .reportType(ReportType.RECIPIENT_PERFORMANCE_REPORT)
            .startDate(java.time.LocalDateTime.now().minusDays(30))
            .endDate(java.time.LocalDateTime.now())
            .selectedFields(List.of("recipient.name", "recipient.type", "performance.totalTasks", "performance.completionRate"))
            .build();
        
        ApiResponseCustom<ReportResponse> response = reportService.generateReport(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * Tạo nhanh báo cáo công việc quá hạn
     */
    @GetMapping("/quick/overdue-report")
    @Operation(summary = "Báo cáo nhanh công việc quá hạn", 
               description = "Tạo báo cáo các công việc đang quá hạn với các trường mặc định")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo báo cáo thành công",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = GeneratedReportApiResponse.class))),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<ReportResponse>> getQuickOverdueReport() {
        log.info("Generating quick overdue report");
        
        ReportRequest request = ReportRequest.builder()
            .reportType(ReportType.OVERDUE_ANALYSIS_REPORT)
            .startDate(java.time.LocalDateTime.now().minusDays(90)) // 3 months back
            .endDate(java.time.LocalDateTime.now())
            .selectedFields(List.of("task.title", "task.priority", "assignment.dueAt", "overdue.days"))
            .build();
        
        ApiResponseCustom<ReportResponse> response = reportService.generateReport(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // ===================== TEST EXPORT ENDPOINT =====================

    /**
     * Test export Excel với data mẫu
     */
    @GetMapping("/test/export/excel")
    @Operation(summary = "Test Export Excel", description = "Test export functionality with sample data")
    public ResponseEntity<byte[]> testExportExcel() {
        try {
            log.info("=== STARTING TEST EXCEL EXPORT ===");
            
            // Tạo sample ReportResponse
            ReportResponse sampleReport = new ReportResponse();
            sampleReport.setReportType("TASK_REPORT");
            sampleReport.setReportName("Sample Task Report");
            
            // Tạo summary
            ReportResponse.ReportSummary summary = new ReportResponse.ReportSummary();
            summary.setTotalRecords(2);
            summary.setTotalGroups(1);
            summary.setGeneratedAt("29/09/2025 16:30:00");
            summary.setProcessingTime(10L);
            sampleReport.setSummary(summary);
            
            // Tạo columns
            List<ReportResponse.ReportColumn> columns = new ArrayList<>();
            
            ReportResponse.ReportColumn col1 = new ReportResponse.ReportColumn();
            col1.setField("task.title");
            col1.setLabel("Task Title");
            col1.setDataType("STRING");
            columns.add(col1);
            
            ReportResponse.ReportColumn col2 = new ReportResponse.ReportColumn();
            col2.setField("task.status");
            col2.setLabel("Status");
            col2.setDataType("ENUM");
            columns.add(col2);
            
            sampleReport.setColumns(columns);
            
            // Tạo groups và data
            List<ReportResponse.ReportGroup> groups = new ArrayList<>();
            ReportResponse.ReportGroup group = new ReportResponse.ReportGroup();
            group.setGroupName("Sample Group");
            group.setPercentage(BigDecimal.valueOf(100.0));
            group.setItemCount(2);
            
            List<Map<String, Object>> items = new ArrayList<>();
            
            Map<String, Object> item1 = new HashMap<>();
            item1.put("title", "Sample Task 1");
            item1.put("status", "IN_PROGRESS");
            items.add(item1);
            
            Map<String, Object> item2 = new HashMap<>();
            item2.put("title", "Sample Task 2");
            item2.put("status", "COMPLETED");
            items.add(item2);
            
            group.setItems(items);
            groups.add(group);
            sampleReport.setGroups(groups);
            
            // Export to Excel
            log.info("About to call reportService.generateExcelFile");
            byte[] excelFile = reportService.generateExcelFile(sampleReport);
            log.info("generateExcelFile completed, file size: {} bytes", excelFile.length);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test_report.xlsx");
            
            log.info("Headers set, about to return ResponseEntity");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelFile);
                    
        } catch (Exception e) {
            log.error("Error in test Excel export: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Test return raw text để debug
     */
    @GetMapping("/test/simple")
    @Operation(summary = "Test Simple Response", description = "Test simple text response")
    public ResponseEntity<String> testSimpleResponse() {
        log.info("=== TEST SIMPLE RESPONSE ===");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Hello World - This is a simple test!");
    }

    /**
     * Test return raw bytes
     */
    @GetMapping("/test/bytes")
    @Operation(summary = "Test Bytes Response", description = "Test raw bytes response")
    public ResponseEntity<byte[]> testBytesResponse() {
        log.info("=== TEST BYTES RESPONSE ===");
        try {
            String content = "CSV,Data,Test\nValue1,Value2,Value3\n";
            byte[] data = content.getBytes("UTF-8");
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(data);
        } catch (Exception e) {
            log.error("Error in bytes test: ", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
