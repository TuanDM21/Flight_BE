package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.request.ReportRequest;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.response.ReportFieldsResponse;
import com.project.quanlycanghangkhong.dto.response.ReportResponse;
import com.project.quanlycanghangkhong.model.ReportType;
import com.project.quanlycanghangkhong.model.*;
import com.project.quanlycanghangkhong.repository.*;
import com.project.quanlycanghangkhong.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// Apache POI imports for Office documents
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import java.io.ByteArrayOutputStream;

/**
 * Implementation của ReportService với 6 loại báo cáo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final TaskRepository taskRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UnitRepository unitRepository;
    // Note: taskTypeRepository will be used for TaskType-based filtering in reports

    @Override
    public ApiResponseCustom<List<ReportType>> getAvailableReportTypes() {
        try {
            List<ReportType> reportTypes = Arrays.asList(ReportType.values());
            return ApiResponseCustom.success("Lấy danh sách loại báo cáo thành công", reportTypes);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách loại báo cáo: {}", e.getMessage());
            return ApiResponseCustom.error("Không thể lấy danh sách loại báo cáo");
        }
    }

    @Override
    public ApiResponseCustom<ReportFieldsResponse> getReportFields(ReportType reportType) {
        try {
            ReportFieldsResponse response = buildFieldsResponse(reportType);
            return ApiResponseCustom.success("Lấy danh sách fields thành công", response);
        } catch (Exception e) {
            log.error("Lỗi khi lấy fields cho báo cáo {}: {}", reportType, e.getMessage());
            return ApiResponseCustom.error("Không thể lấy danh sách fields");
        }
    }

    @Override
    public ApiResponseCustom<ReportResponse> generateReport(ReportRequest request) {
        try {
            long startTime = System.currentTimeMillis();
            
            ReportResponse response = switch (request.getReportType()) {
                case TASK_REPORT -> generateTaskReport(request);
                case RECIPIENT_PERFORMANCE_REPORT -> generateRecipientPerformanceReport(request);
                case ASSIGNMENT_TRACKING_REPORT -> generateAssignmentTrackingReport(request);
                case TASK_STATUS_REPORT -> generateTaskStatusReport(request);
                case TEAM_WORKLOAD_REPORT -> generateTeamWorkloadReport(request);
                case OVERDUE_ANALYSIS_REPORT -> generateOverdueAnalysisReport(request);
            };

            long processingTime = System.currentTimeMillis() - startTime;
            response.getSummary().setProcessingTime(processingTime);
            
            return ApiResponseCustom.success("Tạo báo cáo thành công", response);
        } catch (Exception e) {
            log.error("Lỗi khi tạo báo cáo {}: {}", request.getReportType(), e.getMessage());
            return ApiResponseCustom.error("Không thể tạo báo cáo: " + e.getMessage());
        }
    }

    @Override
    public ApiResponseCustom<byte[]> exportReportToExcel(ReportRequest request) {
        try {
            // Tạo báo cáo trước
            ApiResponseCustom<ReportResponse> reportResponse = generateReport(request);
            if (reportResponse.getStatusCode() != 200) {
                return ApiResponseCustom.error("Không thể tạo báo cáo: " + reportResponse.getMessage());
            }
            
            ReportResponse report = reportResponse.getData();
            byte[] excelData = generateExcelFile(report);
            
            return ApiResponseCustom.success("Export Excel thành công", excelData);
        } catch (Exception e) {
            log.error("Lỗi khi export Excel: {}", e.getMessage());
            return ApiResponseCustom.error("Không thể export Excel: " + e.getMessage());
        }
    }

    @Override
    public ApiResponseCustom<byte[]> exportReportToPdf(ReportRequest request) {
        try {
            // Tạo báo cáo trước
            ApiResponseCustom<ReportResponse> reportResponse = generateReport(request);
            if (reportResponse.getStatusCode() != 200) {
                return ApiResponseCustom.error("Không thể tạo báo cáo: " + reportResponse.getMessage());
            }
            
            ReportResponse report = reportResponse.getData();
            byte[] pdfData = generatePdfFile(report);
            
            return ApiResponseCustom.success("Export PDF thành công", pdfData);
        } catch (Exception e) {
            log.error("Lỗi khi export PDF: {}", e.getMessage());
            return ApiResponseCustom.error("Không thể export PDF: " + e.getMessage());
        }
    }

    @Override
    public ApiResponseCustom<byte[]> exportReportToCsv(ReportRequest request) {
        try {
            // Tạo báo cáo trước
            ApiResponseCustom<ReportResponse> reportResponse = generateReport(request);
            if (reportResponse.getStatusCode() != 200) {
                return ApiResponseCustom.error("Không thể tạo báo cáo: " + reportResponse.getMessage());
            }
            
            ReportResponse report = reportResponse.getData();
            byte[] csvData = generateCsvFile(report);
            
            return ApiResponseCustom.success("Export CSV thành công", csvData);
        } catch (Exception e) {
            log.error("Lỗi khi export CSV: {}", e.getMessage());
            return ApiResponseCustom.error("Không thể export CSV: " + e.getMessage());
        }
    }

    // ===================== PRIVATE METHODS =====================

    private ReportFieldsResponse buildFieldsResponse(ReportType reportType) {
        return switch (reportType) {
            case TASK_REPORT -> buildTaskReportFields();
            case RECIPIENT_PERFORMANCE_REPORT -> buildRecipientPerformanceFields();
            case ASSIGNMENT_TRACKING_REPORT -> buildAssignmentTrackingFields();
            case TASK_STATUS_REPORT -> buildTaskStatusFields();
            case TEAM_WORKLOAD_REPORT -> buildTeamWorkloadFields(); 
            case OVERDUE_ANALYSIS_REPORT -> buildOverdueAnalysisFields();
        };
    }

    // ===================== TASK REPORT =====================
    
    private ReportFieldsResponse buildTaskReportFields() {
        List<ReportFieldsResponse.FieldDefinition> defaultFields = Arrays.asList(
            ReportFieldsResponse.FieldDefinition.builder()
                .field("task.title").label("Tiêu đề").table("Task")
                .dataType("STRING").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("task.content").label("Nội dung").table("Task")
                .dataType("TEXT").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("task.instructions").label("Hướng dẫn").table("Task")
                .dataType("TEXT").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("task.createdAt").label("Ngày tạo").table("Task")
                .dataType("DATETIME").required(true).defaultSelected(true).build()
        );

        List<ReportFieldsResponse.FieldCategory> dynamicFields = Arrays.asList(
            ReportFieldsResponse.FieldCategory.builder()
                .category("Thông tin Task bổ sung")
                .description("Các thông tin khác của task")
                .fields(Arrays.asList(
                    ReportFieldsResponse.FieldDefinition.builder()
                        .field("task.status").label("Trạng thái").table("Task")
                        .dataType("ENUM").required(false).defaultSelected(true).build(),
                    ReportFieldsResponse.FieldDefinition.builder()
                        .field("task.priority").label("Độ ưu tiên").table("Task")  
                        .dataType("ENUM").required(false).defaultSelected(true).build()
                )).build(),
            ReportFieldsResponse.FieldCategory.builder()
                .category("Thông tin phân công")
                .description("Thông tin về việc phân công task")
                .fields(Arrays.asList(
                    ReportFieldsResponse.FieldDefinition.builder()
                        .field("assignment.recipientType").label("Loại người nhận").table("Assignment")
                        .dataType("STRING").required(false).defaultSelected(true).build(),
                    ReportFieldsResponse.FieldDefinition.builder()
                        .field("assignedTo").label("Được giao cho").table("Calculated")
                        .dataType("STRING").required(false).defaultSelected(true).build()
                )).build()
        );

        return ReportFieldsResponse.builder()
            .reportType("TASK_REPORT")
            .reportName("Báo cáo công việc theo TaskType")
            .defaultFields(defaultFields)
            .dynamicFields(dynamicFields)
            .filterOptions(buildFilterOptions())
            .build();
    }

    private ReportResponse generateTaskReport(ReportRequest request) {
        // Query tasks với filters
        List<Task> tasks = getFilteredTasks(request);
        
        // Group by TaskType
        Map<TaskType, List<Task>> groupedTasks = tasks.stream()
            .filter(task -> task.getTaskType() != null)
            .collect(Collectors.groupingBy(Task::getTaskType));

        List<ReportResponse.ReportGroup> groups = new ArrayList<>();
        Map<String, BigDecimal> percentageStats = new HashMap<>();
        int totalTasks = tasks.size();

        for (Map.Entry<TaskType, List<Task>> entry : groupedTasks.entrySet()) {
            TaskType taskType = entry.getKey();
            List<Task> typeTasks = entry.getValue();
            
            BigDecimal percentage = BigDecimal.valueOf(typeTasks.size())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalTasks), 2, RoundingMode.HALF_UP);

            // Thống kê chi tiết cho nhóm
            Map<String, Object> groupStats = new HashMap<>();
            groupStats.put("totalTasks", typeTasks.size());
            groupStats.put("completedTasks", typeTasks.stream().mapToInt(t -> 
                "COMPLETED".equals(t.getStatus().name()) ? 1 : 0).sum());
            groupStats.put("inProgressTasks", typeTasks.stream().mapToInt(t -> 
                "IN_PROGRESS".equals(t.getStatus().name()) ? 1 : 0).sum());
            groupStats.put("openTasks", typeTasks.stream().mapToInt(t -> 
                "OPEN".equals(t.getStatus().name()) ? 1 : 0).sum());

            // Convert tasks to items theo selected fields
            List<Map<String, Object>> items = typeTasks.stream()
                .map(task -> convertTaskToItem(task, request.getSelectedFields()))
                .collect(Collectors.toList());

            groups.add(ReportResponse.ReportGroup.builder()
                .groupName(taskType.getName())
                .groupDescription("Các công việc thuộc loại: " + taskType.getName())
                .itemCount(typeTasks.size())
                .percentage(percentage)
                .groupStats(groupStats)
                .items(items)
                .build());

            percentageStats.put(taskType.getName() + "_percentage", percentage);
        }

        // Build columns
        List<ReportResponse.ReportColumn> columns = buildColumnsFromFields(request.getSelectedFields());

        return ReportResponse.builder()
            .reportType("TASK_REPORT")
            .reportName("Báo cáo công việc theo TaskType")
            .summary(ReportResponse.ReportSummary.builder()
                .totalRecords(totalTasks)
                .totalGroups(groups.size())
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .build())
            .columns(columns)
            .groups(groups)
            .percentageStats(percentageStats)
            .build();
    }

    // ===================== RECIPIENT PERFORMANCE REPORT =====================
    
    private ReportFieldsResponse buildRecipientPerformanceFields() {
        List<ReportFieldsResponse.FieldDefinition> defaultFields = Arrays.asList(
            ReportFieldsResponse.FieldDefinition.builder()
                .field("recipient.name").label("Tên người thực hiện").table("Calculated")
                .dataType("STRING").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("recipient.type").label("Loại người thực hiện").table("Assignment")
                .dataType("STRING").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("performance.totalTasks").label("Tổng số công việc").table("Calculated")
                .dataType("INTEGER").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("performance.completionRate").label("Tỷ lệ hoàn thành (%)").table("Calculated")
                .dataType("DECIMAL").required(true).defaultSelected(true).build()
        );

        List<ReportFieldsResponse.FieldCategory> dynamicFields = Arrays.asList(
            ReportFieldsResponse.FieldCategory.builder()
                .category("Chi tiết hiệu suất")
                .description("Thông tin chi tiết về hiệu suất")
                .fields(Arrays.asList(
                    ReportFieldsResponse.FieldDefinition.builder()
                        .field("performance.completedTasks").label("Số công việc hoàn thành").table("Calculated")
                        .dataType("INTEGER").required(false).defaultSelected(true).build(),
                    ReportFieldsResponse.FieldDefinition.builder()
                        .field("performance.overdueTasks").label("Số công việc quá hạn").table("Calculated")
                        .dataType("INTEGER").required(false).defaultSelected(true).build(),
                    ReportFieldsResponse.FieldDefinition.builder()
                        .field("performance.averageCompletionTime").label("Thời gian hoàn thành TB (giờ)").table("Calculated")
                        .dataType("DECIMAL").required(false).defaultSelected(false).build()
                )).build()
        );

        return ReportFieldsResponse.builder()
            .reportType("RECIPIENT_PERFORMANCE_REPORT")
            .reportName("Báo cáo hiệu suất người thực hiện")
            .defaultFields(defaultFields)
            .dynamicFields(dynamicFields)
            .filterOptions(buildFilterOptions())
            .build();
    }

    private ReportResponse generateRecipientPerformanceReport(ReportRequest request) {
        // Query assignments với filters
        List<Assignment> assignments = getFilteredAssignments(request);
        
        // Group by recipient
        Map<String, List<Assignment>> groupedAssignments = assignments.stream()
            .collect(Collectors.groupingBy(this::getRecipientKey));

        List<ReportResponse.ReportGroup> groups = new ArrayList<>();
        Map<String, BigDecimal> percentageStats = new HashMap<>();
        int totalAssignments = assignments.size();

        for (Map.Entry<String, List<Assignment>> entry : groupedAssignments.entrySet()) {
            String recipientKey = entry.getKey();
            List<Assignment> recipientAssignments = entry.getValue();
            
            BigDecimal percentage = BigDecimal.valueOf(recipientAssignments.size())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalAssignments), 2, RoundingMode.HALF_UP);

            // Tính toán performance metrics
            long completedTasks = recipientAssignments.stream()
                .filter(a -> a.getCompletedAt() != null).count();
            long overdueTasks = recipientAssignments.stream()
                .filter(this::isOverdue).count();
            
            BigDecimal completionRate = totalAssignments > 0 ? 
                BigDecimal.valueOf(completedTasks)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(recipientAssignments.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

            Map<String, Object> groupStats = new HashMap<>();
            groupStats.put("totalTasks", recipientAssignments.size());
            groupStats.put("completedTasks", completedTasks);
            groupStats.put("overdueTasks", overdueTasks);
            groupStats.put("completionRate", completionRate);
            groupStats.put("inProgressTasks", recipientAssignments.size() - completedTasks);

            // Convert assignments to items
            List<Map<String, Object>> items = recipientAssignments.stream()
                .map(assignment -> convertAssignmentToPerformanceItem(assignment, request.getSelectedFields()))
                .collect(Collectors.toList());

            groups.add(ReportResponse.ReportGroup.builder()
                .groupName(recipientKey)
                .groupDescription("Hiệu suất của: " + recipientKey)
                .itemCount(recipientAssignments.size())
                .percentage(percentage)
                .groupStats(groupStats)
                .items(items)
                .build());

            percentageStats.put(recipientKey + "_completion_rate", completionRate);
            percentageStats.put(recipientKey + "_workload_percentage", percentage);
        }

        List<ReportResponse.ReportColumn> columns = buildColumnsFromFields(request.getSelectedFields());

        return ReportResponse.builder()
            .reportType("RECIPIENT_PERFORMANCE_REPORT")
            .reportName("Báo cáo hiệu suất người thực hiện")
            .summary(ReportResponse.ReportSummary.builder()
                .totalRecords(totalAssignments)
                .totalGroups(groups.size())
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .build())
            .columns(columns)
            .groups(groups)
            .percentageStats(percentageStats)
            .build();
    }

    // ===================== ASSIGNMENT TRACKING REPORT =====================
    
    private ReportFieldsResponse buildAssignmentTrackingFields() {
        List<ReportFieldsResponse.FieldDefinition> defaultFields = Arrays.asList(
            ReportFieldsResponse.FieldDefinition.builder()
                .field("task.title").label("Tên công việc").table("Task")
                .dataType("STRING").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("assignedBy.name").label("Người phân công").table("User")
                .dataType("STRING").required(true).defaultSelected(true).build(),  
            ReportFieldsResponse.FieldDefinition.builder()
                .field("recipient.name").label("Người nhận").table("Calculated")
                .dataType("STRING").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("assignment.assignedAt").label("Ngày phân công").table("Assignment")
                .dataType("DATETIME").required(true).defaultSelected(true).build()
        );

        List<ReportFieldsResponse.FieldCategory> dynamicFields = Arrays.asList(
            ReportFieldsResponse.FieldCategory.builder()
                .category("Chi tiết phân công")
                .description("Thông tin chi tiết về việc phân công")
                .fields(Arrays.asList(
                    ReportFieldsResponse.FieldDefinition.builder()
                        .field("assignment.dueAt").label("Hạn hoàn thành").table("Assignment")
                        .dataType("DATETIME").required(false).defaultSelected(true).build(),
                    ReportFieldsResponse.FieldDefinition.builder()
                        .field("assignment.completedAt").label("Ngày hoàn thành").table("Assignment")
                        .dataType("DATETIME").required(false).defaultSelected(true).build(),
                    ReportFieldsResponse.FieldDefinition.builder()
                        .field("assignment.status").label("Trạng thái phân công").table("Assignment")
                        .dataType("ENUM").required(false).defaultSelected(true).build(),
                    ReportFieldsResponse.FieldDefinition.builder()
                        .field("assignment.note").label("Ghi chú").table("Assignment")
                        .dataType("TEXT").required(false).defaultSelected(false).build()
                )).build()
        );

        return ReportFieldsResponse.builder()
            .reportType("ASSIGNMENT_TRACKING_REPORT")
            .reportName("Báo cáo theo dõi phân công")
            .defaultFields(defaultFields)
            .dynamicFields(dynamicFields)
            .filterOptions(buildFilterOptions())
            .build();
    }

    private ReportResponse generateAssignmentTrackingReport(ReportRequest request) {
        List<Assignment> assignments = getFilteredAssignments(request);
        
        // Group by AssignedBy
        Map<User, List<Assignment>> groupedAssignments = assignments.stream()
            .collect(Collectors.groupingBy(Assignment::getAssignedBy));

        List<ReportResponse.ReportGroup> groups = new ArrayList<>();
        Map<String, BigDecimal> percentageStats = new HashMap<>();
        int totalAssignments = assignments.size();

        for (Map.Entry<User, List<Assignment>> entry : groupedAssignments.entrySet()) {
            User assignedBy = entry.getKey();
            List<Assignment> userAssignments = entry.getValue();
            
            BigDecimal percentage = BigDecimal.valueOf(userAssignments.size())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalAssignments), 2, RoundingMode.HALF_UP);

            // Thống kê chi tiết
            long completedAssignments = userAssignments.stream()
                .filter(a -> a.getCompletedAt() != null).count();
            long overdueAssignments = userAssignments.stream()
                .filter(this::isOverdue).count();

            Map<String, Object> groupStats = new HashMap<>();
            groupStats.put("totalAssignments", userAssignments.size());
            groupStats.put("completedAssignments", completedAssignments);
            groupStats.put("overdueAssignments", overdueAssignments);
            groupStats.put("pendingAssignments", userAssignments.size() - completedAssignments);
            
            BigDecimal completionRate = BigDecimal.valueOf(completedAssignments)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(userAssignments.size()), 2, RoundingMode.HALF_UP);
            groupStats.put("completionRate", completionRate);

            List<Map<String, Object>> items = userAssignments.stream()
                .map(assignment -> convertAssignmentToTrackingItem(assignment, request.getSelectedFields()))
                .collect(Collectors.toList());

            groups.add(ReportResponse.ReportGroup.builder()
                .groupName(assignedBy.getName())
                .groupDescription("Phân công bởi: " + assignedBy.getName())
                .itemCount(userAssignments.size())
                .percentage(percentage)
                .groupStats(groupStats)
                .items(items)
                .build());

            percentageStats.put(assignedBy.getName() + "_assignment_percentage", percentage);
            percentageStats.put(assignedBy.getName() + "_completion_rate", completionRate);
        }

        List<ReportResponse.ReportColumn> columns = buildColumnsFromFields(request.getSelectedFields());

        return ReportResponse.builder()
            .reportType("ASSIGNMENT_TRACKING_REPORT")
            .reportName("Báo cáo theo dõi phân công")
            .summary(ReportResponse.ReportSummary.builder()
                .totalRecords(totalAssignments)
                .totalGroups(groups.size())
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .build())
            .columns(columns)
            .groups(groups)
            .percentageStats(percentageStats)
            .build();
    }

    // ===================== TASK STATUS REPORT =====================
    
    private ReportFieldsResponse buildTaskStatusFields() {
        // Similar structure - abbreviated for space
        List<ReportFieldsResponse.FieldDefinition> defaultFields = Arrays.asList(
            ReportFieldsResponse.FieldDefinition.builder()
                .field("task.title").label("Tiêu đề").table("Task")
                .dataType("STRING").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("task.status").label("Trạng thái").table("Task")  
                .dataType("ENUM").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("task.priority").label("Độ ưu tiên").table("Task")
                .dataType("ENUM").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("task.createdAt").label("Ngày tạo").table("Task")
                .dataType("DATETIME").required(true).defaultSelected(true).build()
        );

        return ReportFieldsResponse.builder()
            .reportType("TASK_STATUS_REPORT")
            .reportName("Báo cáo trạng thái công việc")
            .defaultFields(defaultFields)
            .dynamicFields(Arrays.asList()) // Simplified
            .filterOptions(buildFilterOptions())
            .build();
    }

    private ReportResponse generateTaskStatusReport(ReportRequest request) {
        List<Task> tasks = getFilteredTasks(request);
        
        // Group by Status
        Map<TaskStatus, List<Task>> groupedTasks = tasks.stream()
            .collect(Collectors.groupingBy(Task::getStatus));

        List<ReportResponse.ReportGroup> groups = new ArrayList<>();
        Map<String, BigDecimal> percentageStats = new HashMap<>();
        int totalTasks = tasks.size();

        for (Map.Entry<TaskStatus, List<Task>> entry : groupedTasks.entrySet()) {
            TaskStatus status = entry.getKey();
            List<Task> statusTasks = entry.getValue();
            
            BigDecimal percentage = BigDecimal.valueOf(statusTasks.size())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalTasks), 2, RoundingMode.HALF_UP);

            // Thống kê theo Priority trong Status
            Map<String, Object> groupStats = new HashMap<>();
            groupStats.put("totalTasks", statusTasks.size());
            Map<TaskPriority, Long> priorityCount = statusTasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));
            groupStats.put("priorityBreakdown", priorityCount);

            List<Map<String, Object>> items = statusTasks.stream()
                .map(task -> convertTaskToItem(task, request.getSelectedFields()))
                .collect(Collectors.toList());

            groups.add(ReportResponse.ReportGroup.builder()
                .groupName(status.name())
                .groupDescription("Công việc có trạng thái: " + status.name())
                .itemCount(statusTasks.size())
                .percentage(percentage)
                .groupStats(groupStats)
                .items(items)
                .build());

            percentageStats.put(status.name() + "_percentage", percentage);
        }

        List<ReportResponse.ReportColumn> columns = buildColumnsFromFields(request.getSelectedFields());

        return ReportResponse.builder()
            .reportType("TASK_STATUS_REPORT")
            .reportName("Báo cáo trạng thái công việc")
            .summary(ReportResponse.ReportSummary.builder()
                .totalRecords(totalTasks)
                .totalGroups(groups.size())
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .build())
            .columns(columns)
            .groups(groups)
            .percentageStats(percentageStats)
            .build();
    }

    // ===================== TEAM WORKLOAD REPORT =====================
    
    private ReportFieldsResponse buildTeamWorkloadFields() {
        List<ReportFieldsResponse.FieldDefinition> defaultFields = Arrays.asList(
            ReportFieldsResponse.FieldDefinition.builder()
                .field("team.teamName").label("Tên team").table("Team")
                .dataType("STRING").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("workload.totalTasks").label("Tổng công việc").table("Calculated")
                .dataType("INTEGER").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("workload.completionRate").label("Tỷ lệ hoàn thành (%)").table("Calculated")
                .dataType("DECIMAL").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("workload.workloadPercentage").label("% Khối lượng công việc").table("Calculated")
                .dataType("DECIMAL").required(true).defaultSelected(true).build()
        );

        return ReportFieldsResponse.builder()
            .reportType("TEAM_WORKLOAD_REPORT")
            .reportName("Báo cáo khối lượng công việc team")
            .defaultFields(defaultFields)
            .dynamicFields(Arrays.asList()) // Simplified
            .filterOptions(buildFilterOptions())
            .build();
    }

    private ReportResponse generateTeamWorkloadReport(ReportRequest request) {
        List<Assignment> assignments = getFilteredAssignments(request);
        
        // Group by Team (from recipients)
        Map<String, List<Assignment>> groupedAssignments = assignments.stream()
            .filter(a -> "TEAM".equals(a.getRecipientType()))
            .collect(Collectors.groupingBy(a -> getTeamNameByRecipientId(a.getRecipientId())));

        List<ReportResponse.ReportGroup> groups = new ArrayList<>();
        Map<String, BigDecimal> percentageStats = new HashMap<>();
        int totalAssignments = assignments.size();

        for (Map.Entry<String, List<Assignment>> entry : groupedAssignments.entrySet()) {
            String teamName = entry.getKey();
            List<Assignment> teamAssignments = entry.getValue();
            
            BigDecimal workloadPercentage = BigDecimal.valueOf(teamAssignments.size())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalAssignments), 2, RoundingMode.HALF_UP);

            long completedTasks = teamAssignments.stream()
                .filter(a -> a.getCompletedAt() != null).count();
            
            BigDecimal completionRate = BigDecimal.valueOf(completedTasks)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(teamAssignments.size()), 2, RoundingMode.HALF_UP);

            Map<String, Object> groupStats = new HashMap<>();
            groupStats.put("totalTasks", teamAssignments.size());
            groupStats.put("completedTasks", completedTasks);
            groupStats.put("completionRate", completionRate);
            groupStats.put("workloadPercentage", workloadPercentage);
            groupStats.put("pendingTasks", teamAssignments.size() - completedTasks);

            List<Map<String, Object>> items = teamAssignments.stream()
                .map(assignment -> convertAssignmentToWorkloadItem(assignment, request.getSelectedFields()))
                .collect(Collectors.toList());

            groups.add(ReportResponse.ReportGroup.builder()
                .groupName(teamName)
                .groupDescription("Khối lượng công việc của team: " + teamName)
                .itemCount(teamAssignments.size())
                .percentage(workloadPercentage)
                .groupStats(groupStats)
                .items(items)
                .build());

            percentageStats.put(teamName + "_workload_percentage", workloadPercentage);
            percentageStats.put(teamName + "_completion_rate", completionRate);
        }

        List<ReportResponse.ReportColumn> columns = buildColumnsFromFields(request.getSelectedFields());

        return ReportResponse.builder()
            .reportType("TEAM_WORKLOAD_REPORT")
            .reportName("Báo cáo khối lượng công việc team")
            .summary(ReportResponse.ReportSummary.builder()
                .totalRecords(totalAssignments)
                .totalGroups(groups.size())
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .build())
            .columns(columns)
            .groups(groups)
            .percentageStats(percentageStats)
            .build();
    }

    // ===================== OVERDUE ANALYSIS REPORT =====================
    
    private ReportFieldsResponse buildOverdueAnalysisFields() {
        List<ReportFieldsResponse.FieldDefinition> defaultFields = Arrays.asList(
            ReportFieldsResponse.FieldDefinition.builder()
                .field("task.title").label("Tên công việc").table("Task")
                .dataType("STRING").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("task.priority").label("Độ ưu tiên").table("Task")
                .dataType("ENUM").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("assignment.dueAt").label("Hạn hoàn thành").table("Assignment")
                .dataType("DATETIME").required(true).defaultSelected(true).build(),
            ReportFieldsResponse.FieldDefinition.builder()
                .field("overdue.days").label("Số ngày quá hạn").table("Calculated")
                .dataType("INTEGER").required(true).defaultSelected(true).build()
        );

        return ReportFieldsResponse.builder()
            .reportType("OVERDUE_ANALYSIS_REPORT")
            .reportName("Báo cáo phân tích quá hạn")
            .defaultFields(defaultFields)
            .dynamicFields(Arrays.asList()) // Simplified
            .filterOptions(buildFilterOptions())
            .build();
    }

    private ReportResponse generateOverdueAnalysisReport(ReportRequest request) {
        List<Assignment> assignments = getFilteredAssignments(request);
        
        // Filter only overdue assignments
        List<Assignment> overdueAssignments = assignments.stream()
            .filter(this::isOverdue)
            .collect(Collectors.toList());

        // Group by overdue severity (days)
        Map<String, List<Assignment>> groupedAssignments = overdueAssignments.stream()
            .collect(Collectors.groupingBy(this::getOverdueSeverity));

        List<ReportResponse.ReportGroup> groups = new ArrayList<>();
        Map<String, BigDecimal> percentageStats = new HashMap<>();
        int totalOverdue = overdueAssignments.size();

        for (Map.Entry<String, List<Assignment>> entry : groupedAssignments.entrySet()) {
            String severity = entry.getKey();
            List<Assignment> severityAssignments = entry.getValue();
            
            BigDecimal percentage = totalOverdue > 0 ? 
                BigDecimal.valueOf(severityAssignments.size())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalOverdue), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

            Map<String, Object> groupStats = new HashMap<>();
            groupStats.put("totalOverdue", severityAssignments.size());
            groupStats.put("averageOverdueDays", calculateAverageOverdueDays(severityAssignments));
            
            // Priority breakdown
            Map<TaskPriority, Long> priorityBreakdown = severityAssignments.stream()
                .map(a -> a.getTask().getPriority())
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
            groupStats.put("priorityBreakdown", priorityBreakdown);

            List<Map<String, Object>> items = severityAssignments.stream()
                .map(assignment -> convertAssignmentToOverdueItem(assignment, request.getSelectedFields()))
                .collect(Collectors.toList());

            groups.add(ReportResponse.ReportGroup.builder()
                .groupName(severity)
                .groupDescription("Công việc quá hạn mức độ: " + severity)
                .itemCount(severityAssignments.size())
                .percentage(percentage)
                .groupStats(groupStats)
                .items(items)
                .build());

            percentageStats.put(severity + "_percentage", percentage);
        }

        List<ReportResponse.ReportColumn> columns = buildColumnsFromFields(request.getSelectedFields());

        return ReportResponse.builder()
            .reportType("OVERDUE_ANALYSIS_REPORT")
            .reportName("Báo cáo phân tích quá hạn")
            .summary(ReportResponse.ReportSummary.builder()
                .totalRecords(totalOverdue)
                .totalGroups(groups.size())
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .build())
            .columns(columns)
            .groups(groups)
            .percentageStats(percentageStats)
            .build();
    }

    // ===================== HELPER METHODS =====================

    private List<Task> getFilteredTasks(ReportRequest request) {
        // TODO: Implement actual filtering based on request.getFilters()
        // For now, return all tasks in date range
        return taskRepository.findByCreatedAtBetween(
            request.getStartDate(), 
            request.getEndDate()
        );
    }

    private List<Assignment> getFilteredAssignments(ReportRequest request) {
        // TODO: Implement actual filtering
        return assignmentRepository.findByAssignedAtBetween(
            request.getStartDate(),
            request.getEndDate()
        );
    }

    private ReportFieldsResponse.FilterOptions buildFilterOptions() {
        // TODO: Build actual filter options from database
        return ReportFieldsResponse.FilterOptions.builder()
            .statuses(Arrays.asList(
                ReportFieldsResponse.FilterOption.builder().value("OPEN").label("Mở").count(0).build(),
                ReportFieldsResponse.FilterOption.builder().value("IN_PROGRESS").label("Đang thực hiện").count(0).build(),
                ReportFieldsResponse.FilterOption.builder().value("COMPLETED").label("Hoàn thành").count(0).build()
            ))
            .priorities(Arrays.asList(
                ReportFieldsResponse.FilterOption.builder().value("LOW").label("Thấp").count(0).build(),
                ReportFieldsResponse.FilterOption.builder().value("NORMAL").label("Bình thường").count(0).build(),
                ReportFieldsResponse.FilterOption.builder().value("HIGH").label("Cao").count(0).build(),
                ReportFieldsResponse.FilterOption.builder().value("URGENT").label("Khẩn cấp").count(0).build()
            ))
            .recipientTypes(Arrays.asList(
                ReportFieldsResponse.FilterOption.builder().value("USER").label("Cá nhân").count(0).build(),
                ReportFieldsResponse.FilterOption.builder().value("TEAM").label("Team").count(0).build(),
                ReportFieldsResponse.FilterOption.builder().value("UNIT").label("Đơn vị").count(0).build()
            ))
            .build();
    }

    private List<ReportResponse.ReportColumn> buildColumnsFromFields(List<String> selectedFields) {
        return selectedFields.stream()
            .map(field -> ReportResponse.ReportColumn.builder()
                .field(field)
                .label(getFieldLabel(field))
                .dataType(getFieldDataType(field))
                .sortable(true)
                .width("auto")
                .build())
            .collect(Collectors.toList());
    }

    private Map<String, Object> convertTaskToItem(Task task, List<String> selectedFields) {
        Map<String, Object> item = new HashMap<>();
        
        for (String field : selectedFields) {
            switch (field) {
                case "task.title" -> item.put("title", task.getTitle());
                case "task.content" -> item.put("content", task.getContent());
                case "task.instructions" -> item.put("instructions", task.getInstructions());
                case "task.createdAt" -> item.put("createdAt", task.getCreatedAt());
                case "task.status" -> item.put("status", task.getStatus().name());
                case "task.priority" -> item.put("priority", task.getPriority().name());
                case "assignedTo" -> item.put("assignedTo", getAssignedToString(task));
                // Add more field mappings as needed
            }
        }
        
        return item;
    }

    private Map<String, Object> convertAssignmentToPerformanceItem(Assignment assignment, List<String> selectedFields) {
        Map<String, Object> item = new HashMap<>();
        
        for (String field : selectedFields) {
            switch (field) {
                case "recipient.name" -> item.put("recipientName", getRecipientName(assignment));
                case "recipient.type" -> item.put("recipientType", assignment.getRecipientType());
                case "performance.totalTasks" -> item.put("totalTasks", 1); // This would be calculated per recipient
                case "task.title" -> item.put("taskTitle", assignment.getTask().getTitle());
                // Add more mappings
            }
        }
        
        return item;
    }

    private Map<String, Object> convertAssignmentToTrackingItem(Assignment assignment, List<String> selectedFields) {
        Map<String, Object> item = new HashMap<>();
        
        for (String field : selectedFields) {
            switch (field) {
                case "task.title" -> item.put("taskTitle", assignment.getTask().getTitle());
                case "assignedBy.name" -> item.put("assignedByName", assignment.getAssignedBy().getName());
                case "recipient.name" -> item.put("recipientName", getRecipientName(assignment));
                case "assignment.assignedAt" -> item.put("assignedAt", assignment.getAssignedAt());
                case "assignment.dueAt" -> item.put("dueAt", assignment.getDueAt());
                case "assignment.completedAt" -> item.put("completedAt", assignment.getCompletedAt());
                case "assignment.status" -> item.put("status", assignment.getStatus().name());
                case "assignment.note" -> item.put("note", assignment.getNote());
            }
        }
        
        return item;
    }

    private Map<String, Object> convertAssignmentToWorkloadItem(Assignment assignment, List<String> selectedFields) {
        // Similar to tracking but focused on workload metrics
        return convertAssignmentToTrackingItem(assignment, selectedFields);
    }

    private Map<String, Object> convertAssignmentToOverdueItem(Assignment assignment, List<String> selectedFields) {
        Map<String, Object> item = convertAssignmentToTrackingItem(assignment, selectedFields);
        
        // Add overdue-specific fields
        if (selectedFields.contains("overdue.days")) {
            item.put("overdueDays", calculateOverdueDays(assignment));
        }
        
        return item;
    }

    // Utility methods
    private String getRecipientKey(Assignment assignment) {
        return assignment.getRecipientType() + ":" + assignment.getRecipientId();
    }

    private String getRecipientName(Assignment assignment) {
        return switch (assignment.getRecipientType()) {
            case "USER" -> getUserNameById(assignment.getRecipientId());
            case "TEAM" -> getTeamNameByRecipientId(assignment.getRecipientId());
            case "UNIT" -> getUnitNameByRecipientId(assignment.getRecipientId());
            default -> "Unknown";
        };
    }

    private String getUserNameById(Integer userId) {
        return userRepository.findById(userId)
            .map(User::getName)
            .orElse("Unknown User");
    }

    private String getTeamNameByRecipientId(Integer teamId) {
        return teamRepository.findById(teamId)
            .map(Team::getTeamName)
            .orElse("Unknown Team");
    }

    private String getUnitNameByRecipientId(Integer unitId) {
        return unitRepository.findById(unitId)
            .map(Unit::getUnitName)
            .orElse("Unknown Unit");
    }

    private String getAssignedToString(Task task) {
        return task.getAssignments().stream()
            .map(this::getRecipientName)
            .collect(Collectors.joining(", "));
    }

    private boolean isOverdue(Assignment assignment) {
        return assignment.getDueAt() != null && 
               assignment.getCompletedAt() == null && 
               assignment.getDueAt().isBefore(LocalDateTime.now());
    }

    private String getOverdueSeverity(Assignment assignment) {
        int days = calculateOverdueDays(assignment);
        if (days <= 1) return "1 ngày";
        if (days <= 3) return "2-3 ngày";
        if (days <= 7) return "4-7 ngày";
        return "Trên 7 ngày";
    }

    private int calculateOverdueDays(Assignment assignment) {
        if (assignment.getDueAt() == null) return 0;
        return (int) java.time.Duration.between(assignment.getDueAt(), LocalDateTime.now()).toDays();
    }

    private double calculateAverageOverdueDays(List<Assignment> assignments) {
        return assignments.stream()
            .mapToInt(this::calculateOverdueDays)
            .average()
            .orElse(0.0);
    }

    private String getFieldLabel(String field) {
        // Map field names to display labels
        return switch (field) {
            case "task.title" -> "Tiêu đề";
            case "task.content" -> "Nội dung";
            case "task.instructions" -> "Hướng dẫn";
            case "task.createdAt" -> "Ngày tạo";
            case "task.status" -> "Trạng thái";
            case "task.priority" -> "Độ ưu tiên";
            default -> field;
        };
    }

    private String getFieldDataType(String field) {
        // Map field names to data types
        return switch (field) {
            case "task.title", "task.content", "task.instructions" -> "STRING";
            case "task.createdAt" -> "DATETIME";
            case "task.status", "task.priority" -> "ENUM";
            default -> "STRING";
        };
    }

    // ===================== EXPORT HELPER METHODS =====================

    @Override
    public byte[] generateExcelFile(ReportResponse report) throws Exception {
        try {
            log.info("Generating Excel file for report: {}", report.getReportType());
            
            // Tạo workbook Excel thực sự bằng Apache POI
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Report");
            
            int rowNum = 0;
            
            // Header information
            Row headerRow1 = sheet.createRow(rowNum++);
            headerRow1.createCell(0).setCellValue("Report Name:");
            headerRow1.createCell(1).setCellValue(report.getReportName());
            
            Row headerRow2 = sheet.createRow(rowNum++);
            headerRow2.createCell(0).setCellValue("Total Records:");
            headerRow2.createCell(1).setCellValue(report.getSummary().getTotalRecords());
            
            Row headerRow3 = sheet.createRow(rowNum++);
            headerRow3.createCell(0).setCellValue("Generated At:");
            headerRow3.createCell(1).setCellValue(report.getSummary().getGeneratedAt());
            
            // Empty row
            rowNum++;
            
            // Column headers
            Row columnHeaderRow = sheet.createRow(rowNum++);
            int colNum = 0;
            for (ReportResponse.ReportColumn column : report.getColumns()) {
                columnHeaderRow.createCell(colNum++).setCellValue(column.getLabel());
            }
            
            // Data rows
            if (report.getGroups().isEmpty()) {
                Row noDataRow = sheet.createRow(rowNum++);
                noDataRow.createCell(0).setCellValue("No data available");
            } else {
                for (ReportResponse.ReportGroup group : report.getGroups()) {
                    // Group header
                    Row groupRow = sheet.createRow(rowNum++);
                    groupRow.createCell(0).setCellValue("Group: " + group.getGroupName() + " (" + group.getPercentage() + "%)");
                    
                    // Group data
                    for (Map<String, Object> item : group.getItems()) {
                        Row dataRow = sheet.createRow(rowNum++);
                        colNum = 0;
                        for (ReportResponse.ReportColumn column : report.getColumns()) {
                            String field = column.getField();
                            String fieldKey = field.contains(".") ? field.split("\\.")[1] : field;
                            Object value = item.get(fieldKey);
                            dataRow.createCell(colNum++).setCellValue(value != null ? value.toString() : "N/A");
                        }
                    }
                }
            }
            
            // Auto-size columns
            for (int i = 0; i < report.getColumns().size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            byte[] result = outputStream.toByteArray();
            log.info("Excel file generated successfully, size: {} bytes", result.length);
            return result;
            
        } catch (Exception e) {
            log.error("Error generating Excel file: ", e);
            throw new Exception("Lỗi khi tạo Excel file: " + e.getMessage());
        }
    }

    @Override
    public byte[] generatePdfFile(ReportResponse report) {
        try {
            log.info("Generating PDF file for report: {}", report.getReportType());
            
            // TODO: Implement PDF generation when iText dependencies are properly configured
            // For now, return Excel format as fallback
            log.warn("PDF generation not available, returning Excel format");
            return generateExcelFile(report);
            
        } catch (Exception e) {
            log.error("Error generating PDF file: ", e);
            throw new RuntimeException("Lỗi khi tạo PDF file: " + e.getMessage());
        }
    }

    @Override
    public byte[] generateCsvFile(ReportResponse report) {
        try {
            log.info("Generating CSV file for report: {}", report.getReportType());
            
            StringBuilder csvContent = new StringBuilder();

            // Add header
            csvContent.append("Report Name: ").append(report.getReportName()).append("\n");
            csvContent.append("Total Records: ").append(report.getSummary().getTotalRecords()).append("\n");
            csvContent.append("Total Groups: ").append(report.getSummary().getTotalGroups()).append("\n");
            csvContent.append("Generated At: ").append(report.getSummary().getGeneratedAt()).append("\n");
            csvContent.append("\n");

            if (report.getGroups() == null || report.getGroups().isEmpty()) {
                csvContent.append("No data available for this report.\n");
            } else {
                // Add column headers
                for (int i = 0; i < report.getColumns().size(); i++) {
                    if (i > 0) csvContent.append(",");
                    csvContent.append("\"").append(report.getColumns().get(i).getLabel()).append("\"");
                }
                csvContent.append("\n");

                // Add data rows grouped
                for (ReportResponse.ReportGroup group : report.getGroups()) {
                    // Add group header row
                    csvContent.append("\"Group: ").append(group.getGroupName())
                            .append(" (").append(group.getPercentage()).append("%)\"\n");

                    // Add group data rows
                    for (Map<String, Object> item : group.getItems()) {
                        for (int i = 0; i < report.getColumns().size(); i++) {
                            if (i > 0) csvContent.append(",");
                            ReportResponse.ReportColumn column = report.getColumns().get(i);
                            String field = column.getField();
                            String fieldKey = field.contains(".") ? field.split("\\.")[1] : field;
                            Object value = item.get(fieldKey);
                            csvContent.append("\"").append(value != null ? value.toString().replace("\"", "\"\"") : "").append("\"");
                        }
                        csvContent.append("\n");
                    }
                    csvContent.append("\n"); // Empty line between groups
                }
            }

            byte[] result = csvContent.toString().getBytes("UTF-8");
            log.info("CSV file generated successfully, size: {} bytes", result.length);
            return result;

        } catch (Exception e) {
            log.error("Error generating CSV file: ", e);
            throw new RuntimeException("Lỗi khi tạo CSV file: " + e.getMessage());
        }
    }

    @Override
    public byte[] generateWordFile(ReportResponse report) {
        try {
            log.info("Generating Word file for report: {}", report.getReportType());
            
            XWPFDocument document = new XWPFDocument();
            
            // Title
            XWPFParagraph titlePara = document.createParagraph();
            titlePara.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText(report.getReportName());
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            
            // Report info
            XWPFParagraph infoPara = document.createParagraph();
            XWPFRun infoRun = infoPara.createRun();
            infoRun.setText("Total Records: " + report.getSummary().getTotalRecords());
            infoRun.addBreak();
            infoRun.setText("Total Groups: " + report.getSummary().getTotalGroups());
            infoRun.addBreak();
            infoRun.setText("Generated At: " + report.getSummary().getGeneratedAt());
            infoRun.addBreak();
            infoRun.addBreak();
            
            if (report.getGroups() == null || report.getGroups().isEmpty()) {
                XWPFParagraph noDataPara = document.createParagraph();
                noDataPara.createRun().setText("No data available for this report.");
            } else {
                // Create table
                XWPFTable table = document.createTable();
                
                // Header row
                XWPFTableRow headerRow = table.getRow(0);
                for (int i = 0; i < report.getColumns().size(); i++) {
                    if (i == 0) {
                        headerRow.getCell(0).setText(report.getColumns().get(i).getLabel());
                    } else {
                        headerRow.addNewTableCell().setText(report.getColumns().get(i).getLabel());
                    }
                }
                
                // Data rows
                for (ReportResponse.ReportGroup group : report.getGroups()) {
                    // Group header
                    XWPFTableRow groupRow = table.createRow();
                    XWPFTableCell groupCell = groupRow.getCell(0);
                    groupCell.setText("Group: " + group.getGroupName() + " (" + group.getPercentage() + "%)");
                    
                    // Merge cells for group header
                    for (int i = 1; i < report.getColumns().size(); i++) {
                        groupRow.getCell(i).setText("");
                    }
                    
                    // Group data
                    for (Map<String, Object> item : group.getItems()) {
                        XWPFTableRow dataRow = table.createRow();
                        for (int i = 0; i < report.getColumns().size(); i++) {
                            ReportResponse.ReportColumn column = report.getColumns().get(i);
                            String field = column.getField();
                            String fieldKey = field.contains(".") ? field.split("\\.")[1] : field;
                            Object value = item.get(fieldKey);
                            dataRow.getCell(i).setText(value != null ? value.toString() : "N/A");
                        }
                    }
                }
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.write(outputStream);
            document.close();
            
            byte[] result = outputStream.toByteArray();
            log.info("Word file generated successfully, size: {} bytes", result.length);
            return result;
            
        } catch (Exception e) {
            log.error("Error generating Word file: ", e);
            throw new RuntimeException("Lỗi khi tạo Word file: " + e.getMessage());
        }
    }

    @Override
    public byte[] generatePowerPointFile(ReportResponse report) {
        try {
            log.info("Generating PowerPoint file for report: {}", report.getReportType());
            
            // Tạo presentation đơn giản với text-based slides
            XMLSlideShow ppt = new XMLSlideShow();
            
            // Slide 1: Title slide
            XSLFSlide titleSlide = ppt.createSlide();
            XSLFTextBox titleBox = titleSlide.createTextBox();
            titleBox.setAnchor(new java.awt.Rectangle(50, 50, 600, 100));
            
            XSLFTextParagraph titlePara = titleBox.addNewTextParagraph();
            XSLFTextRun titleRun = titlePara.addNewTextRun();
            titleRun.setText(report.getReportName());
            titleRun.setFontSize(24.0);
            titleRun.setBold(true);
            
            // Slide 2: Summary slide  
            XSLFSlide summarySlide = ppt.createSlide();
            XSLFTextBox summaryBox = summarySlide.createTextBox();
            summaryBox.setAnchor(new java.awt.Rectangle(50, 50, 600, 300));
            
            XSLFTextParagraph summaryTitle = summaryBox.addNewTextParagraph();
            XSLFTextRun summaryTitleRun = summaryTitle.addNewTextRun();
            summaryTitleRun.setText("SUMMARY");
            summaryTitleRun.setFontSize(18.0);
            summaryTitleRun.setBold(true);
            
            XSLFTextParagraph summaryPara1 = summaryBox.addNewTextParagraph();
            summaryPara1.addNewTextRun().setText("Total Records: " + report.getSummary().getTotalRecords());
            
            XSLFTextParagraph summaryPara2 = summaryBox.addNewTextParagraph();
            summaryPara2.addNewTextRun().setText("Total Groups: " + report.getSummary().getTotalGroups());
            
            XSLFTextParagraph summaryPara3 = summaryBox.addNewTextParagraph();
            summaryPara3.addNewTextRun().setText("Generated At: " + report.getSummary().getGeneratedAt());
            
            if (report.getGroups() == null || report.getGroups().isEmpty()) {
                XSLFSlide noDataSlide = ppt.createSlide();
                XSLFTextBox noDataBox = noDataSlide.createTextBox();
                noDataBox.setAnchor(new java.awt.Rectangle(50, 50, 600, 100));
                
                XSLFTextParagraph noDataPara = noDataBox.addNewTextParagraph();
                noDataPara.addNewTextRun().setText("No data available for this report.");
            } else {
                // Data slides - one slide per group
                for (ReportResponse.ReportGroup group : report.getGroups()) {
                    XSLFSlide dataSlide = ppt.createSlide();
                    XSLFTextBox dataBox = dataSlide.createTextBox();
                    dataBox.setAnchor(new java.awt.Rectangle(50, 50, 600, 400));
                    
                    // Group title
                    XSLFTextParagraph groupTitle = dataBox.addNewTextParagraph();
                    XSLFTextRun groupTitleRun = groupTitle.addNewTextRun();
                    groupTitleRun.setText("Group: " + group.getGroupName() + " (" + group.getPercentage() + "%)");
                    groupTitleRun.setFontSize(16.0);
                    groupTitleRun.setBold(true);
                    
                    // Group data
                    for (Map<String, Object> item : group.getItems()) {
                        XSLFTextParagraph itemPara = dataBox.addNewTextParagraph();
                        StringBuilder itemText = new StringBuilder();
                        
                        for (ReportResponse.ReportColumn column : report.getColumns()) {
                            String field = column.getField();
                            String fieldKey = field.contains(".") ? field.split("\\.")[1] : field;
                            Object value = item.get(fieldKey);
                            itemText.append(column.getLabel()).append(": ")
                                   .append(value != null ? value.toString() : "N/A").append(" | ");
                        }
                        
                        itemPara.addNewTextRun().setText(itemText.toString());
                    }
                }
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ppt.write(outputStream);
            ppt.close();
            
            byte[] result = outputStream.toByteArray();
            log.info("PowerPoint file generated successfully, size: {} bytes", result.length);
            return result;
            
        } catch (Exception e) {
            log.error("Error generating PowerPoint file: ", e);
            throw new RuntimeException("Lỗi khi tạo PowerPoint file: " + e.getMessage());
        }
    }
}
