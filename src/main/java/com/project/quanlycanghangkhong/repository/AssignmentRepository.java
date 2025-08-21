package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Assignment;
import com.project.quanlycanghangkhong.model.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByTask_Id(Integer taskId);
    
    /**
     * 🚀 OPTIMIZED: Get assignments by task ID with all relationships fetched
     * @param taskId Task ID
     * @return List of assignments for the task with full data
     */
    @Query("SELECT a FROM Assignment a " +
           "LEFT JOIN FETCH a.assignedBy " +
           "LEFT JOIN FETCH a.completedBy " +
           "WHERE a.task.id = :taskId")
    List<Assignment> findByTaskId(@Param("taskId") Integer taskId);
    
    /**
     * 🚀 BATCH OPTIMIZED: Get assignments by multiple task IDs with all relationships fetched
     * @param taskIds List of task IDs
     * @return List of assignments for all tasks with full data
     */
    @Query("SELECT a FROM Assignment a " +
           "LEFT JOIN FETCH a.assignedBy " +
           "LEFT JOIN FETCH a.completedBy " +
           "WHERE a.task.id IN :taskIds")
    List<Assignment> findByTaskIdIn(@Param("taskIds") List<Integer> taskIds);
    
    // ============== OVERDUE SUPPORT METHODS ==============
    
    /**
     * 🟢 OVERDUE: Tìm assignments có status cụ thể và quá dueAt
     * @param status Assignment status cần tìm
     * @param currentTime Thời điểm hiện tại
     * @return Danh sách assignment quá hạn
     */
    List<Assignment> findByStatusAndDueAtBefore(AssignmentStatus status, LocalDateTime currentTime);
    
    /**
     * 🟢 OVERDUE: Đếm số assignment overdue của user
     * @param userId User ID
     * @param currentTime Thời điểm hiện tại
     * @return Số lượng assignment overdue
     */
    @Query("SELECT COUNT(a) FROM Assignment a WHERE " +
           "((a.recipientType = 'user' AND a.recipientId = :userId) OR " +
           " (a.recipientType = 'team' AND a.recipientId IN " +
           "  (SELECT u.team.id FROM User u WHERE u.id = :userId)) OR " +
           " (a.recipientType = 'unit' AND a.recipientId IN " +
           "  (SELECT u.unit.id FROM User u WHERE u.id = :userId))) " +
           "AND a.dueAt IS NOT NULL AND a.dueAt < :currentTime " +
           "AND a.status != 'DONE'")
    long countOverdueAssignmentsByUser(@Param("userId") Integer userId, @Param("currentTime") LocalDateTime currentTime);
}