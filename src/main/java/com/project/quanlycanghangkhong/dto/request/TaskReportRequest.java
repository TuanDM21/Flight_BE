package com.project.quanlycanghangkhong.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request để tạo báo cáo Task/Assignment")
public class TaskReportRequest {

    @Schema(description = "Ngày bắt đầu", example = "2024-01-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "Ngày kết thúc", example = "2024-12-31T23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "Danh sách ID của Task cần export")
    private List<Integer> taskIds;

    @Schema(description = "Danh sách ID của User được assign")
    private List<Integer> userIds;

    @Schema(description = "Danh sách ID của Team")
    private List<Integer> teamIds;

    @Schema(description = "Danh sách trạng thái Task")
    private List<String> taskStatuses;

    @Schema(description = "Danh sách trạng thái Assignment")
    private List<String> assignmentStatuses;

    @Schema(description = "Độ ưu tiên Task")
    private List<String> priorities;

    @Schema(description = "Loại Task")
    private List<Integer> taskTypeIds;
}
