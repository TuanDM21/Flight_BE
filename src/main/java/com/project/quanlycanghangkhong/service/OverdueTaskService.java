package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.model.Assignment;
import com.project.quanlycanghangkhong.model.AssignmentStatus;
import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.model.TaskStatus;
import com.project.quanlycanghangkhong.repository.AssignmentRepository;
import com.project.quanlycanghangkhong.repository.TaskRepository;
import com.project.quanlycanghangkhong.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service để xử lý logic cập nhật trạng thái quá hạn cho Task và Assignment
 * Chạy định kỳ để kiểm tra và cập nhật status
 */
@Service
public class OverdueTaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private AssignmentRepository assignmentRepository;
    
    @Autowired
    private TaskService taskService;

    /**
     * Scheduled job chạy mỗi giờ để cập nhật trạng thái overdue
     * Cron expression: 0 0 * * * * = chạy mỗi giờ đúng phút 0
     */
    @Scheduled(cron = "0 0 * * * *") // Chạy mỗi giờ
    @Transactional
    public void updateOverdueStatus() {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. Cập nhật Assignment status từ WORKING -> OVERDUE
        updateOverdueAssignments(now);
        
        // 2. Cập nhật Task status dựa trên Assignment status mới
        updateOverdueTasks();
    }

    /**
     * Cập nhật assignments quá hạn từ WORKING -> OVERDUE
     */
    private void updateOverdueAssignments(LocalDateTime now) {
        List<Assignment> overdueAssignments = assignmentRepository
                .findByStatusAndDueAtBefore(AssignmentStatus.WORKING, now);
        
        for (Assignment assignment : overdueAssignments) {
            assignment.setStatus(AssignmentStatus.OVERDUE);
        }
        
        if (!overdueAssignments.isEmpty()) {
            assignmentRepository.saveAll(overdueAssignments);
        }
    }

    /**
     * Cập nhật task status dựa trên assignment status mới
     */
    private void updateOverdueTasks() {
        // Lấy tất cả tasks có assignments overdue nhưng status chưa phải OVERDUE hoặc COMPLETED
        List<Task> tasksNeedUpdate = taskRepository.findTasksWithOverdueAssignments();
        
        for (Task task : tasksNeedUpdate) {
            taskService.updateTaskStatus(task);
        }
    }

    /**
     * Manual trigger để cập nhật overdue status ngay lập tức
     * Có thể gọi từ API endpoint khi cần
     */
    @Transactional
    public void forceUpdateOverdueStatus() {
        updateOverdueStatus();
    }

    /**
     * Kiểm tra một task cụ thể có overdue không
     */
    public boolean isTaskOverdue(Integer taskId) {
        LocalDateTime now = LocalDateTime.now();
        List<Assignment> assignments = assignmentRepository.findByTaskId(taskId);
        
        return assignments.stream()
                .anyMatch(a -> a.getDueAt() != null && 
                              a.getDueAt().isBefore(now) && 
                              a.getStatus() != AssignmentStatus.DONE);
    }

    /**
     * Lấy số lượng task overdue của user
     */
    public long getOverdueTaskCountForUser(Integer userId) {
        return taskRepository.countOverdueTasksForUser(userId);
    }
}
