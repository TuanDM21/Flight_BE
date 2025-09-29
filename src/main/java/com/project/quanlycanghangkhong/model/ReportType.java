package com.project.quanlycanghangkhong.model;

/**
 * Enum định nghĩa các loại báo cáo hỗ trợ trong hệ thống
 */
public enum ReportType {
    TASK_REPORT("Báo cáo công việc theo TaskType"),
    RECIPIENT_PERFORMANCE_REPORT("Báo cáo hiệu suất người thực hiện"),
    ASSIGNMENT_TRACKING_REPORT("Báo cáo theo dõi phân công"),
    TASK_STATUS_REPORT("Báo cáo trạng thái công việc"),
    TEAM_WORKLOAD_REPORT("Báo cáo khối lượng công việc team"),
    OVERDUE_ANALYSIS_REPORT("Báo cáo phân tích quá hạn");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
