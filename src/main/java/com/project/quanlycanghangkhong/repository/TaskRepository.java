package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    
    // ============== CORE TASK OPERATIONS (ĐANG SỬ DỤNG) ==============
    
    /**
     * 🟢 ĐANG SỬ DỤNG: Lấy tất cả task chưa bị xóa mềm
     * Được dùng trong: getAllTaskDetails()
     */
    List<Task> findAllByDeletedFalse();
    
    /**
     * 🟢 ĐANG SỬ DỤNG: Lấy task theo ID và chưa bị xóa mềm  
     * Được dùng trong: getTaskById(), getTaskDetailById(), createSubtask(), assignAttachmentsToTask()
     */
    Optional<Task> findByIdAndDeletedFalse(Integer id);
    
    // ============== ADJACENCY LIST MODEL (ĐANG SỬ DỤNG) ==============
    
    /**
     * 🟢 ĐANG SỬ DỤNG: Tìm tất cả subtask theo ID cha trong mô hình Adjacency List
     * Được dùng trong: getTaskDetailById() (recursive), getSubtasks()
     * @param parentId ID task cha
     * @return Danh sách task con
     */
    List<Task> findByParentIdAndDeletedFalse(Integer parentId);
    
    /**
     * 🟢 ĐANG SỬ DỤNG: Tìm tất cả task gốc (task không có cha) trong mô hình Adjacency List
     * Được dùng trong: getRootTasks()
     * @return Danh sách task gốc
     */
    List<Task> findByParentIsNullAndDeletedFalse();
    
    // ============== ALTERNATIVE SYNTAX (KHÔNG SỬ DỤNG) ==============
    
    // ============== SEARCH & FILTER (HỮU ÍCH CHO FRONTEND) ==============
    
    /**
     * 🟢 HỮU ÍCH: Tìm kiếm task theo title (case-insensitive)
     * Dùng cho: Search functionality trong frontend
     * @param title Từ khóa tìm kiếm trong title
     * @return Danh sách task có title chứa từ khóa
     */
    List<Task> findByTitleContainingIgnoreCaseAndDeletedFalse(String title);
    
    /**
     * 🟢 HỮU ÍCH: Tìm kiếm task theo title hoặc content
     * Dùng cho: Advanced search trong frontend  
     * @param title Từ khóa tìm trong title
     * @param content Từ khóa tìm trong content
     * @return Danh sách task match
     */
    List<Task> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDeletedFalse(String title, String content);
    
    /**
     * 🟢 HỮU ÍCH: Lọc task theo priority
     * Dùng cho: Filter by priority trong frontend
     * @param priority Priority level
     * @return Danh sách task có priority cụ thể
     */
    List<Task> findByPriorityAndDeletedFalse(com.project.quanlycanghangkhong.model.TaskPriority priority);
    
    // ============== OPTIMIZED METHODS FOR MY TASKS API ==============
    
    /**
     * 🟢 OPTIMIZED: Lấy tasks đã tạo nhưng chưa có assignment (type=created)
     * Thay thế: findAllByDeletedFalse() + filter stream
     * @param userId ID của user
     * @return Danh sách task đã tạo nhưng chưa giao việc
     */
    @Query("SELECT t FROM Task t WHERE t.createdBy.id = :userId AND t.deleted = false " +
           "AND NOT EXISTS (SELECT a FROM Assignment a WHERE a.task = t)")
    List<Task> findCreatedTasksWithoutAssignments(@Param("userId") Integer userId);
    
    /**
     * 🟢 OPTIMIZED: Lấy tasks đã giao việc (type=assigned)  
     * Thay thế: assignmentRepository.findAll() + filter stream
     * @param userId ID của user đã giao việc
     * @return Danh sách task đã giao việc
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.assignedBy.id = :userId AND a.task.deleted = false")
    List<Task> findAssignedTasksByUserId(@Param("userId") Integer userId);
    
    /**
     * 🟢 OPTIMIZED: Lấy tasks được giao cho user trực tiếp (type=received, recipientType=user)
     * @param userId ID của user nhận việc
     * @return Danh sách task được giao trực tiếp
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.recipientType = 'user' " +
           "AND a.recipientId = :userId AND a.task.deleted = false")
    List<Task> findReceivedTasksByUserId(@Param("userId") Integer userId);
    
    /**
     * 🟢 OPTIMIZED: Lấy tasks được giao cho team mà user làm team lead (type=received, recipientType=team)
     * @param userId ID của team lead
     * @param teamId ID của team
     * @return Danh sách task được giao cho team
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.recipientType = 'team' " +
           "AND a.recipientId = :teamId AND a.task.deleted = false")
    List<Task> findReceivedTasksByTeamId(@Param("teamId") Integer teamId);
    
    /**
     * 🟢 OPTIMIZED: Lấy tasks được giao cho unit mà user làm unit lead (type=received, recipientType=unit)
     * @param unitId ID của unit
     * @return Danh sách task được giao cho unit
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.recipientType = 'unit' " +
           "AND a.recipientId = :unitId AND a.task.deleted = false")
    List<Task> findReceivedTasksByUnitId(@Param("unitId") Integer unitId);
}