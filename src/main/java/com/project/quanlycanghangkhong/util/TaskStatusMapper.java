package com.project.quanlycanghangkhong.util;

import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.model.TaskStatus;
import com.project.quanlycanghangkhong.model.TaskPriority;

import java.util.function.Predicate;

/**
 * 🎯 TASK STATUS MAPPER: Map business status categories to TaskStatus enum và other conditions
 */
public class TaskStatusMapper {
    
    /**
     * ✅ Map business status string to task filtering logic
     * @param status Business status: "completed", "pending", "urgent", "overdue"
     * @return Predicate để filter tasks
     */
    public static Predicate<Task> getStatusFilter(String status) {
        if (status == null || status.trim().isEmpty()) {
            return task -> true; // No filter
        }
        
        switch (status.toLowerCase().trim()) {
            case "completed":
                // ✅ COMPLETED: Task có status = COMPLETED
                return task -> TaskStatus.COMPLETED.equals(task.getStatus());
                
            case "pending":
                // ✅ PENDING: Task có status = OPEN hoặc IN_PROGRESS (chưa hoàn thành)
                return task -> TaskStatus.OPEN.equals(task.getStatus()) || 
                              TaskStatus.IN_PROGRESS.equals(task.getStatus());
                
            case "urgent":
                // ✅ URGENT: Task có priority = HIGH hoặc URGENT (bất kể status)
                return task -> TaskPriority.HIGH.equals(task.getPriority()) || 
                              TaskPriority.URGENT.equals(task.getPriority());
                
            case "overdue":
                // ✅ OVERDUE: Task có status = OVERDUE
                return task -> TaskStatus.OVERDUE.equals(task.getStatus());
                
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
        
        switch (status.toLowerCase().trim()) {
            case "completed":
                return TaskStatus.COMPLETED;
            case "overdue":
                return TaskStatus.OVERDUE;
            case "pending":
            case "urgent":
                // Không map trực tiếp được vì cần logic phức tạp hơn
                return null;
            default:
                return null;
        }
    }
    
    /**
     * ✅ Check if status requires complex filtering (không thể dùng simple TaskStatus query)
     * @param status Business status string
     * @return true nếu cần filter phức tạp ở application level
     */
    public static boolean requiresComplexFiltering(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        switch (status.toLowerCase().trim()) {
            case "pending":  // Cần check multiple TaskStatus values
            case "urgent":   // Cần check TaskPriority thay vì TaskStatus
                return true;
            case "completed":
            case "overdue":
                return false; // Có thể dùng direct TaskStatus query
            default:
                return false;
        }
    }
    
    /**
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
            case "pending":
                return "Tasks đang chờ xử lý (status = OPEN hoặc IN_PROGRESS)";
            case "urgent":
                return "Tasks có độ ưu tiên cao (priority = HIGH hoặc URGENT)";
            case "overdue":
                return "Tasks quá hạn (status = OVERDUE)";
            default:
                return "Status không hợp lệ";
        }
    }
}
