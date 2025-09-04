package com.project.quanlycanghangkhong.util;

import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.model.TaskStatus;

import java.util.function.Predicate;

/**
 * 🎯 TASK STATUS MAPPER: Map business status categories to TaskStatus enum và other conditions
 */
public class TaskStatusMapper {
    
    /**
     * ✅ Map business status string to task filtering logic
     * @param status TaskStatus enum values: "IN_PROGRESS", "COMPLETED", "OVERDUE" (case-insensitive)
     * @return Predicate để filter tasks
     */
    public static Predicate<Task> getStatusFilter(String status) {
        if (status == null || status.trim().isEmpty()) {
            return task -> true; // No filter
        }
        
        switch (status.toUpperCase().trim()) {
            case "IN_PROGRESS":
                // ✅ IN_PROGRESS: Task có status = IN_PROGRESS
                return task -> TaskStatus.IN_PROGRESS.equals(task.getStatus());
                
            case "COMPLETED":
                // ✅ COMPLETED: Task có status = COMPLETED
                return task -> TaskStatus.COMPLETED.equals(task.getStatus());
                
            case "OVERDUE":
                // ✅ OVERDUE: Task có status = OVERDUE
                return task -> TaskStatus.OVERDUE.equals(task.getStatus());
                
            case "OPEN":
                // ✅ OPEN: Task có status = OPEN
                return task -> TaskStatus.OPEN.equals(task.getStatus());
                
            default:
                return task -> true; // Invalid status, no filter
        }
    }
    
    /**
     * ✅ Get TaskStatus enum for database query optimization (chỉ cho completed và overdue)
     * @param status Business status string
     * @return TaskStatus enum hoặc null nếu không map trực tiếp được
     */
    public static TaskStatus getDirectTaskStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        
        switch (status.toUpperCase().trim()) {
            case "IN_PROGRESS":
                return TaskStatus.IN_PROGRESS;
            case "COMPLETED":
                return TaskStatus.COMPLETED;
            case "OVERDUE":
                return TaskStatus.OVERDUE;
            case "OPEN":
                return TaskStatus.OPEN;
            default:
                return null; // Invalid status
        }
    }
    
    /**
     * ✅ Check if status requires complex filtering (luôn false vì tất cả status đều map trực tiếp)
     * @param status TaskStatus enum string
     * @return false vì tất cả status đều có thể dùng simple TaskStatus query
     */
    public static boolean requiresComplexFiltering(String status) {
        // Tất cả status hiện tại (IN_PROGRESS, COMPLETED, OVERDUE) đều map trực tiếp với TaskStatus enum
        return false;
    }    /**
     * ✅ Get description for status category
     * @param status Business status string
     * @return Mô tả về logic filtering
     */
    public static String getStatusDescription(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "Tất cả tasks";
        }
        
        switch (status.toLowerCase().trim()) {
            case "completed":
                return "Tasks đã hoàn thành (status = COMPLETED)";
            case "in_progress":
                return "Tasks đang thực hiện (status = IN_PROGRESS)";
            case "open":
                return "Tasks mới tạo chưa bắt đầu (status = OPEN)";
            case "overdue":
                return "Tasks quá hạn (status = OVERDUE)";
            case "pending":
                return "Tasks đang chờ xử lý (status = OPEN hoặc IN_PROGRESS)";
            case "urgent":
                return "Tasks có độ ưu tiên cao (priority = HIGH hoặc URGENT)";
            default:
                return "Status không hợp lệ";
        }
    }
}
