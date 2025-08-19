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
    
    // ============== COUNT ROOT TASKS ONLY (FOR MY TASKS API) ==============
    
    /**
     * 🟢 COUNT: Đếm tasks đã tạo nhưng chưa có assignment (chỉ root tasks)
     * @param userId ID của user
     * @return Số lượng root task đã tạo nhưng chưa giao việc
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.createdBy.id = :userId AND t.deleted = false " +
           "AND t.parent IS NULL " +
           "AND NOT EXISTS (SELECT a FROM Assignment a WHERE a.task = t)")
    long countCreatedRootTasksWithoutAssignments(@Param("userId") Integer userId);
    
    /**
     * 🟢 COUNT: Đếm tasks đã giao việc (chỉ root tasks)
     * @param userId ID của user đã giao việc
     * @return Số lượng root task đã giao việc
     */
    @Query("SELECT COUNT(DISTINCT a.task) FROM Assignment a WHERE a.assignedBy.id = :userId " +
           "AND a.task.deleted = false AND a.task.parent IS NULL")
    long countAssignedRootTasksByUserId(@Param("userId") Integer userId);
    
    /**
     * 🟢 COUNT: Đếm tasks được giao cho user trực tiếp (chỉ root tasks)
     * @param userId ID của user nhận việc
     * @return Số lượng root task được giao trực tiếp
     */
    @Query("SELECT COUNT(DISTINCT a.task) FROM Assignment a WHERE a.recipientType = 'user' " +
           "AND a.recipientId = :userId AND a.task.deleted = false AND a.task.parent IS NULL")
    long countReceivedRootTasksByUserId(@Param("userId") Integer userId);
    
    /**
     * 🟢 COUNT: Đếm tasks được giao cho team (chỉ root tasks)
     * @param teamId ID của team
     * @return Số lượng root task được giao cho team
     */
    @Query("SELECT COUNT(DISTINCT a.task) FROM Assignment a WHERE a.recipientType = 'team' " +
           "AND a.recipientId = :teamId AND a.task.deleted = false AND a.task.parent IS NULL")
    long countReceivedRootTasksByTeamId(@Param("teamId") Integer teamId);
    
    /**
     * 🟢 COUNT: Đếm tasks được giao cho unit (chỉ root tasks)
     * @param unitId ID của unit
     * @return Số lượng root task được giao cho unit
     */
    @Query("SELECT COUNT(DISTINCT a.task) FROM Assignment a WHERE a.recipientType = 'unit' " +
           "AND a.recipientId = :unitId AND a.task.deleted = false AND a.task.parent IS NULL")
    long countReceivedRootTasksByUnitId(@Param("unitId") Integer unitId);
    
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
           "AND NOT EXISTS (SELECT a FROM Assignment a WHERE a.task = t) " +
           "ORDER BY t.updatedAt DESC, t.createdAt DESC")
    List<Task> findCreatedTasksWithoutAssignments(@Param("userId") Integer userId);
    
    /**
     * 🟢 OPTIMIZED: Lấy tasks đã giao việc (type=assigned)  
     * Thay thế: assignmentRepository.findAll() + filter stream
     * @param userId ID của user đã giao việc
     * @return Danh sách task đã giao việc (sort mới nhất)
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.assignedBy.id = :userId AND a.task.deleted = false " +
           "ORDER BY a.task.updatedAt DESC, a.task.createdAt DESC")
    List<Task> findAssignedTasksByUserId(@Param("userId") Integer userId);
    
    /**
     * 🟢 OPTIMIZED: Lấy tasks được giao cho user trực tiếp (type=received, recipientType=user)
     * @param userId ID của user nhận việc
     * @return Danh sách task được giao trực tiếp (sort mới nhất)
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.recipientType = 'user' " +
           "AND a.recipientId = :userId AND a.task.deleted = false " +
           "ORDER BY a.task.updatedAt DESC, a.task.createdAt DESC")
    List<Task> findReceivedTasksByUserId(@Param("userId") Integer userId);
    
    /**
     * 🟢 OPTIMIZED: Lấy tasks được giao cho team mà user làm team lead (type=received, recipientType=team)
     * @param userId ID của team lead
     * @param teamId ID của team
     * @return Danh sách task được giao cho team (sort mới nhất)
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.recipientType = 'team' " +
           "AND a.recipientId = :teamId AND a.task.deleted = false " +
           "ORDER BY a.task.updatedAt DESC, a.task.createdAt DESC")
    List<Task> findReceivedTasksByTeamId(@Param("teamId") Integer teamId);
    
    /**
     * 🟢 OPTIMIZED: Lấy tasks được giao cho unit mà user làm unit lead (type=received, recipientType=unit)
     * @param unitId ID của unit
     * @return Danh sách task được giao cho unit (sort mới nhất)
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.recipientType = 'unit' " +
           "AND a.recipientId = :unitId AND a.task.deleted = false " +
           "ORDER BY a.task.updatedAt DESC, a.task.createdAt DESC")
    List<Task> findReceivedTasksByUnitId(@Param("unitId") Integer unitId);
    
    // ============== OPTIMIZED JOIN FETCH METHODS TO FIX N+1 QUERY PROBLEM ==============
    
    /**
     * 🚀 OPTIMIZED: Load task với tất cả relationships trong 1 query (fix N+1 problem)
     * FIX MultipleBagFetchException: Chỉ fetch assignments, attachments sẽ load riêng
     * @param id Task ID
     * @return Task với assignments, createdBy, parent được fetch
     */
    @Query("SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.assignments a " +
           "LEFT JOIN FETCH a.assignedBy " +
           "LEFT JOIN FETCH a.completedBy " +
           "LEFT JOIN FETCH t.createdBy " +
           "LEFT JOIN FETCH t.parent " +
           "WHERE t.id = :id AND t.deleted = false")
    Optional<Task> findTaskWithAllRelationships(@Param("id") Integer id);
    
    /**
     * 🚀 OPTIMIZED: Created tasks với JOIN FETCH (fix N+1 problem)
     * FIX MultipleBagFetchException: Chỉ fetch assignments
     * @param userId User ID
     * @return Tasks với relationships được fetch
     */
    @Query("SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.createdBy " +
           "WHERE t.createdBy.id = :userId AND t.deleted = false " +
           "AND NOT EXISTS (SELECT 1 FROM Assignment asn WHERE asn.task = t) " +
           "ORDER BY t.updatedAt DESC")
    List<Task> findCreatedTasksWithAllRelationships(@Param("userId") Integer userId);
    
    /**
     * 🚀 OPTIMIZED: Assigned tasks với JOIN FETCH (fix N+1 problem)
     * FIX MultipleBagFetchException: Chỉ fetch assignments
     * @param userId User ID
     * @return Tasks với relationships được fetch
     */
    @Query("SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.assignments a " +
           "LEFT JOIN FETCH a.assignedBy " +
           "LEFT JOIN FETCH a.completedBy " +
           "LEFT JOIN FETCH t.createdBy " +
           "LEFT JOIN FETCH t.parent " +
           "JOIN t.assignments asn " +
           "WHERE asn.assignedBy.id = :userId AND t.deleted = false " +
           "ORDER BY t.updatedAt DESC")
    List<Task> findAssignedTasksWithAllRelationships(@Param("userId") Integer userId);
    
    /**
     * 🚀 OPTIMIZED: Received tasks với JOIN FETCH (fix N+1 problem)
     * FIX MultipleBagFetchException: Chỉ fetch assignments
     * @param userId User ID
     * @param teamId Team ID
     * @param unitId Unit ID
     * @return Tasks với relationships được fetch
     */
    @Query("SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.assignments a " +
           "LEFT JOIN FETCH a.assignedBy " +
           "LEFT JOIN FETCH a.completedBy " +
           "LEFT JOIN FETCH t.createdBy " +
           "LEFT JOIN FETCH t.parent " +
           "JOIN t.assignments asn " +
           "WHERE ((asn.recipientType = 'user' AND asn.recipientId = :userId) " +
           "OR (asn.recipientType = 'team' AND asn.recipientId = :teamId) " +
           "OR (asn.recipientType = 'unit' AND asn.recipientId = :unitId)) " +
           "AND t.deleted = false " +
           "ORDER BY t.updatedAt DESC")
    List<Task> findReceivedTasksWithAllRelationships(@Param("userId") Integer userId, 
                                                    @Param("teamId") Integer teamId, 
                                                    @Param("unitId") Integer unitId);
    
    // ============== OVERDUE SUPPORT METHODS ==============
    
    /**
     * 🟢 OVERDUE: Tìm tasks có assignments overdue nhưng task status chưa phải OVERDUE hoặc COMPLETED
     * @return Danh sách task cần cập nhật status
     */
    @Query("SELECT DISTINCT t FROM Task t JOIN t.assignments a WHERE " +
           "a.dueAt IS NOT NULL AND a.dueAt < CURRENT_TIMESTAMP " +
           "AND a.status != 'DONE' AND t.status NOT IN ('OVERDUE', 'COMPLETED') " +
           "AND t.deleted = false")
    List<Task> findTasksWithOverdueAssignments();
    
    /**
     * 🟢 OVERDUE: Đếm số task overdue của user
     * @param userId User ID
     * @return Số lượng task overdue
     */
    @Query("SELECT COUNT(DISTINCT t) FROM Task t JOIN t.assignments a WHERE " +
           "((a.recipientType = 'user' AND a.recipientId = :userId) OR " +
           " (a.recipientType = 'team' AND a.recipientId IN " +
           "  (SELECT u.team.id FROM User u WHERE u.id = :userId)) OR " +
           " (a.recipientType = 'unit' AND a.recipientId IN " +
           "  (SELECT u.unit.id FROM User u WHERE u.id = :userId))) " +
           "AND t.status = 'OVERDUE' AND t.deleted = false")
    long countOverdueTasksForUser(@Param("userId") Integer userId);
    
    // ============== ADVANCED SEARCH METHODS FOR MY TASKS API ==============
    
    /**
     * 🔍 ADVANCED SEARCH: Tìm kiếm tasks đã giao việc với nhiều tiêu chí (Multi-select support)
     * @param userId ID của user đã giao việc
     * @param keyword Từ khóa tìm trong title hoặc content (có thể null)
     * @param startTime Thời gian bắt đầu (có thể null)
     * @param endTime Thời gian kết thúc (có thể null) 
     * @param priorities Danh sách priority để filter (có thể empty)
     * @param recipientTypes Danh sách recipient types (có thể empty)
     * @param recipientIds Danh sách recipient IDs tương ứng (có thể empty)
     * @return Danh sách task đã giao việc thỏa mãn điều kiện
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.assignedBy.id = :userId AND a.task.deleted = false " +
           "AND (:keyword IS NULL OR " +
           "     (LOWER(a.task.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "      LOWER(a.task.content) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
           "AND (:startTime IS NULL OR a.task.createdAt >= :startTime) " +
           "AND (:endTime IS NULL OR a.task.createdAt <= :endTime) " +
           "AND (:#{#priorities.isEmpty()} = true OR a.task.priority IN :priorities) " +
           "AND (:#{#recipientTypes.isEmpty()} = true OR " +
           "     (a.recipientType IN :recipientTypes AND a.recipientId IN :recipientIds)) " +
           "ORDER BY a.task.updatedAt DESC, a.task.createdAt DESC")
    List<Task> findAssignedTasksWithAdvancedSearchMulti(@Param("userId") Integer userId,
                                                        @Param("keyword") String keyword,
                                                        @Param("startTime") java.time.LocalDateTime startTime,
                                                        @Param("endTime") java.time.LocalDateTime endTime,
                                                        @Param("priorities") List<com.project.quanlycanghangkhong.model.TaskPriority> priorities,
                                                        @Param("recipientTypes") List<String> recipientTypes,
                                                        @Param("recipientIds") List<Integer> recipientIds);
    
    /**
     * 🔍 COUNT: Đếm số lượng tasks đã giao việc thỏa mãn điều kiện tìm kiếm (Multi-select support)
     * @param userId ID của user đã giao việc
     * @param keyword Từ khóa tìm kiếm (có thể null)
     * @param startTime Thời gian bắt đầu (có thể null)
     * @param endTime Thời gian kết thúc (có thể null)
     * @param priorities Danh sách priority để filter (có thể empty)
     * @param recipientTypes Danh sách recipient types (có thể empty)
     * @param recipientIds Danh sách recipient IDs tương ứng (có thể empty)
     * @return Số lượng task thỏa mãn điều kiện
     */
    @Query("SELECT COUNT(DISTINCT a.task) FROM Assignment a WHERE a.assignedBy.id = :userId AND a.task.deleted = false " +
           "AND (:keyword IS NULL OR " +
           "     (LOWER(a.task.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "      LOWER(a.task.content) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
           "AND (:startTime IS NULL OR a.task.createdAt >= :startTime) " +
           "AND (:endTime IS NULL OR a.task.createdAt <= :endTime) " +
           "AND (:#{#priorities.isEmpty()} = true OR a.task.priority IN :priorities) " +
           "AND (:#{#recipientTypes.isEmpty()} = true OR " +
           "     (a.recipientType IN :recipientTypes AND a.recipientId IN :recipientIds))")
    long countAssignedTasksWithAdvancedSearchMulti(@Param("userId") Integer userId,
                                                   @Param("keyword") String keyword,
                                                   @Param("startTime") java.time.LocalDateTime startTime,
                                                   @Param("endTime") java.time.LocalDateTime endTime,
                                                   @Param("priorities") List<com.project.quanlycanghangkhong.model.TaskPriority> priorities,
                                                   @Param("recipientTypes") List<String> recipientTypes,
                                                   @Param("recipientIds") List<Integer> recipientIds);
}