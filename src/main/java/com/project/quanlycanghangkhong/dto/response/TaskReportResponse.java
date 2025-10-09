package com.project.quanlycanghangkhong.dto.response;

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
@Schema(description = "Response cho báo cáo Task/Assignment")
public class TaskReportResponse {

    @Schema(description = "Danh sách Task và Assignment")
    private List<TaskReportItem> tasks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Thông tin chi tiết Task trong báo cáo")
    public static class TaskReportItem {
        @Schema(description = "ID Task")
        private Integer taskId;

        @Schema(description = "Tiêu đề Task")
        private String title;

        @Schema(description = "Nội dung Task")
        private String content;

        @Schema(description = "Trạng thái Task")
        private String status;

        @Schema(description = "Độ ưu tiên")
        private String priority;

        @Schema(description = "Loại Task")
        private String taskType;

        @Schema(description = "Người tạo")
        private String createdBy;

        @Schema(description = "Ngày tạo")
        private LocalDateTime createdAt;

        @Schema(description = "Ngày cập nhật")
        private LocalDateTime updatedAt;

        @Schema(description = "Danh sách Assignment của Task")
        private List<AssignmentDetail> assignments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Chi tiết Assignment")
    public static class AssignmentDetail {
        @Schema(description = "ID Assignment")
        private Integer assignmentId;

        @Schema(description = "Loại người nhận (USER/TEAM)")
        private String recipientType;

        @Schema(description = "ID người nhận")
        private Integer recipientId;

        @Schema(description = "Tên người nhận")
        private String recipientName;

        @Schema(description = "Người giao việc")
        private String assignedBy;

        @Schema(description = "Ngày giao")
        private LocalDateTime assignedAt;

        @Schema(description = "Hạn hoàn thành")
        private LocalDateTime dueAt;

        @Schema(description = "Ngày hoàn thành")
        private LocalDateTime completedAt;

        @Schema(description = "Người hoàn thành")
        private String completedBy;

        @Schema(description = "Trạng thái Assignment")
        private String status;

        @Schema(description = "Ghi chú")
        private String note;
    }
}
