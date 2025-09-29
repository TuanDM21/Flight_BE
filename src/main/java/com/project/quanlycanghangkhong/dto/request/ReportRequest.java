package com.project.quanlycanghangkhong.dto.request;

import com.project.quanlycanghangkhong.model.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO cho việc tạo báo cáo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request để tạo báo cáo với các fields động")
public class ReportRequest {

    @Schema(description = "Loại báo cáo", required = true, example = "TASK_REPORT")
    @NotNull(message = "Report type is required")
    private ReportType reportType;

    @Schema(description = "Ngày bắt đầu", required = true, example = "2025-09-01T00:00:00")
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @Schema(description = "Ngày kết thúc", required = true, example = "2025-09-30T23:59:59")
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @Schema(description = "Danh sách fields được chọn để hiển thị", required = true)
    @NotEmpty(message = "Selected fields cannot be empty")
    private List<String> selectedFields;

    @Schema(description = "Filters áp dụng cho báo cáo")
    private ReportFilters filters;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Bộ lọc cho báo cáo")
    public static class ReportFilters {
        
        @Schema(description = "Lọc theo trạng thái task")
        private List<String> statuses;

        @Schema(description = "Lọc theo độ ưu tiên")
        private List<String> priorities;

        @Schema(description = "Lọc theo TaskType IDs")
        private List<Integer> taskTypeIds;

        @Schema(description = "Lọc theo Team IDs")
        private List<Integer> teamIds;

        @Schema(description = "Lọc theo Unit IDs")
        private List<Integer> unitIds;

        @Schema(description = "Lọc theo User IDs")
        private List<Integer> userIds;

        @Schema(description = "Lọc theo loại recipient")
        private List<String> recipientTypes;

        @Schema(description = "Chỉ lấy công việc quá hạn")
        private Boolean overdueOnly;

        @Schema(description = "Chỉ lấy công việc có attachment")
        private Boolean hasAttachmentOnly;
    }
}
