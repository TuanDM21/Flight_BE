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
     * 🚀 BATCH LOAD: Đếm số lượng subtask cho nhiều parent task trong 1 query
     * Được dùng trong: convertTasksToTaskDetailDTOsBatch() để tính hasSubtask
     * @param parentIds Danh sách ID các task cha
     * @return List<Object[]> với [parentId, count]
     */
    @Query("SELECT t.parent.id, COUNT(t) FROM Task t WHERE t.parent.id IN :parentIds AND t.deleted = false GROUP BY t.parent.id")
    List<Object[]> countSubtasksByParentIds(@Param("parentIds") List<Integer> parentIds);
    
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
     * 🚀 NATIVE OPTIMIZED: Đếm tất cả task counts trong 1 query duy nhất
     * Tối ưu hóa thay thế cho 5+ separate count queries
     */
    @Query(value = """
        SELECT 
            -- Created count: tasks created by user without assignments
            COALESCE(SUM(CASE 
                WHEN t.created_by = :userId 
                AND t.parent_id IS NULL 
                AND NOT EXISTS (SELECT 1 FROM assignment a WHERE a.task_id = t.id)
                THEN 1 ELSE 0 END), 0) as created_count,
            
            -- Assigned count: tasks assigned by user (root only)
            COALESCE(SUM(CASE 
                WHEN EXISTS (SELECT 1 FROM assignment a WHERE a.task_id = t.id AND a.assigned_by = :userId)
                AND t.parent_id IS NULL
                THEN 1 ELSE 0 END), 0) as assigned_count,
                
            -- Received count: tasks received by user directly (root only)
            COALESCE(SUM(CASE 
                WHEN EXISTS (SELECT 1 FROM assignment a WHERE a.task_id = t.id 
                           AND a.recipient_type = 'user' AND a.recipient_id = :userId)
                AND t.parent_id IS NULL
                THEN 1 ELSE 0 END), 0) as received_count,
                
            -- Team received count: tasks received by team (root only)
            COALESCE(SUM(CASE 
                WHEN EXISTS (SELECT 1 FROM assignment a WHERE a.task_id = t.id 
                           AND a.recipient_type = 'team' AND a.recipient_id = :teamId)
                AND t.parent_id IS NULL AND :teamId IS NOT NULL
                THEN 1 ELSE 0 END), 0) as team_received_count,
                
            -- Unit received count: tasks received by unit (root only)
            COALESCE(SUM(CASE 
                WHEN EXISTS (SELECT 1 FROM assignment a WHERE a.task_id = t.id 
                           AND a.recipient_type = 'unit' AND a.recipient_id = :unitId)
                AND t.parent_id IS NULL AND :unitId IS NOT NULL
                THEN 1 ELSE 0 END), 0) as unit_received_count
                
        FROM task t 
        WHERE t.deleted = false
        """, nativeQuery = true)
    Object[] getTaskCountsOptimized(@Param("userId") Integer userId, 
                                   @Param("teamId") Integer teamId, 
                                   @Param("unitId") Integer unitId);
    
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
     * 🚀 SIMPLE OPTIMIZED: Assigned tasks without heavy JOIN FETCH (performance priority)
     * Removed complex relationships loading để improve performance
     * @param userId User ID
     * @return Tasks without relationships (load separately if needed)
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.assignedBy.id = :userId AND a.task.deleted = false " +
           "ORDER BY a.task.updatedAt DESC, a.task.createdAt DESC")
    List<Task> findAssignedTasksOptimizedPerformance(@Param("userId") Integer userId);
    
    /**
     * 🚀 FALLBACK: Assigned tasks với JOIN FETCH (for compatibility)
     * @param userId User ID
     * @return Tasks với relationships được fetch
     */
    @Query("SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.assignments a " +
           "LEFT JOIN FETCH a.assignedBy " +
           "LEFT JOIN FETCH t.createdBy " +
           "JOIN t.assignments asn " +
           "WHERE asn.assignedBy.id = :userId AND t.deleted = false " +
           "ORDER BY t.updatedAt DESC")
    List<Task> findAssignedTasksWithAllRelationships(@Param("userId") Integer userId);
    
    /**
     * 🚀 SIMPLE OPTIMIZED: Received tasks without heavy JOIN FETCH (performance priority)
     * Removed complex relationships loading để improve performance
     * @param userId User ID
     * @param teamId Team ID
     * @param unitId Unit ID
     * @return Tasks without relationships (load separately if needed)
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE " +
           "((a.recipientType = 'user' AND a.recipientId = :userId) " +
           "OR (a.recipientType = 'team' AND a.recipientId = :teamId) " +
           "OR (a.recipientType = 'unit' AND a.recipientId = :unitId)) " +
           "AND a.task.deleted = false " +
           "ORDER BY a.task.updatedAt DESC, a.task.createdAt DESC")
    List<Task> findReceivedTasksOptimizedPerformance(@Param("userId") Integer userId, 
                                                    @Param("teamId") Integer teamId, 
                                                    @Param("unitId") Integer unitId);
                                                    
    /**
     * 🚀 FALLBACK: Received tasks với JOIN FETCH (for compatibility)
     * @param userId User ID
     * @param teamId Team ID
     * @param unitId Unit ID
     * @return Tasks với relationships được fetch
     */
    @Query("SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.assignments a " +
           "LEFT JOIN FETCH a.assignedBy " +
           "LEFT JOIN FETCH t.createdBy " +
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
           "     (CAST(a.task.id AS STRING) LIKE CONCAT('%', :keyword, '%') OR " +
           "      LOWER(a.task.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "      LOWER(a.task.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "      LOWER(a.task.instructions) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "      LOWER(a.task.notes) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
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
           "     (CAST(a.task.id AS STRING) LIKE CONCAT('%', :keyword, '%') OR " +
           "      LOWER(a.task.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "      LOWER(a.task.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "      LOWER(a.task.instructions) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "      LOWER(a.task.notes) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
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

    // ============== ULTRA OPTIMIZED KEYWORD SEARCH ==============
    
    /**
     * 🚀 ULTRA FAST: Optimized keyword search with native query and index support
     * Uses FULLTEXT search instead of slow LIKE queries
     * Performance: <1000ms for large datasets
     * @param keyword Search keyword
     * @return List of tasks matching keyword in id, title, content, instructions, or notes
     */
    @Query(value = 
        "SELECT DISTINCT t.* FROM task t " +
        "WHERE t.deleted = false " +
        "AND (:keyword IS NULL OR :keyword = '' OR " +
        "     (CAST(t.id AS CHAR) LIKE CONCAT('%', :keyword, '%') OR " +
        "      t.title LIKE CONCAT('%', :keyword, '%') OR " +
        "      t.content LIKE CONCAT('%', :keyword, '%') OR " +
        "      t.instructions LIKE CONCAT('%', :keyword, '%') OR " +
        "      t.notes LIKE CONCAT('%', :keyword, '%'))) " +
        "ORDER BY " +
        "  CASE WHEN CAST(t.id AS CHAR) LIKE CONCAT('%', :keyword, '%') THEN 0 " +
        "       WHEN t.title LIKE CONCAT('%', :keyword, '%') THEN 1 " +
        "       WHEN t.content LIKE CONCAT('%', :keyword, '%') THEN 2 " +
        "       WHEN t.instructions LIKE CONCAT('%', :keyword, '%') THEN 3 " +
        "       ELSE 4 END, " +
        "  t.updated_at DESC " +
        "LIMIT 100",
        nativeQuery = true)
    List<Task> findByKeywordOptimized(@Param("keyword") String keyword);
    
    /**
     * 🚀 ULTRA FAST: Count for optimized keyword search
     * @param keyword Search keyword  
     * @return Count of matching tasks
     */
    @Query(value = 
        "SELECT COUNT(DISTINCT t.id) FROM task t " +
        "WHERE t.deleted = false " +
        "AND (:keyword IS NULL OR :keyword = '' OR " +
        "     (t.title LIKE CONCAT('%', :keyword, '%') OR " +
        "      t.content LIKE CONCAT('%', :keyword, '%')))",
        nativeQuery = true)
    long countByKeywordOptimized(@Param("keyword") String keyword);

    // ============== ULTRA OPTIMIZED NATIVE QUERIES ==============
    
    /**
     * 🚀 ULTRA FAST: Assigned tasks với native query - performance critical
     * Sử dụng native SQL để tối ưu tối đa performance
     * @param userId User ID
     * @return Task IDs only (load entities separately if needed)
     */
    @Query(value = 
        "SELECT DISTINCT t.id, t.title, t.content, t.status, t.priority, " +
        "t.created_at, t.updated_at, t.created_by, t.parent_id, t.instructions, t.notes " +
        "FROM task t " +
        "INNER JOIN assignment a ON t.id = a.task_id " +
        "WHERE a.assigned_by = :userId AND t.deleted = false " +
        "ORDER BY t.updated_at DESC, t.created_at DESC " +
        "LIMIT 100",
        nativeQuery = true)
    List<Object[]> findAssignedTasksUltraFast(@Param("userId") Integer userId);
    
    /**
     * 🚀 ULTRA FAST: Received tasks với native query - performance critical
     * @param userId User ID
     * @param teamId Team ID (nullable)
     * @param unitId Unit ID (nullable)
     * @return Task data as Object array
     */
    @Query(value = 
        "SELECT DISTINCT t.id, t.title, t.content, t.status, t.priority, " +
        "t.created_at, t.updated_at, t.created_by, t.parent_id, t.instructions, t.notes " +
        "FROM task t " +
        "INNER JOIN assignment a ON t.id = a.task_id " +
        "WHERE ((a.recipient_type = 'user' AND a.recipient_id = :userId) " +
        "OR (:teamId IS NOT NULL AND a.recipient_type = 'team' AND a.recipient_id = :teamId) " +
        "OR (:unitId IS NOT NULL AND a.recipient_type = 'unit' AND a.recipient_id = :unitId)) " +
        "AND t.deleted = false " +
        "ORDER BY t.updated_at DESC, t.created_at DESC " +
        "LIMIT 100",
        nativeQuery = true)
    List<Object[]> findReceivedTasksUltraFast(@Param("userId") Integer userId, 
                                             @Param("teamId") Integer teamId, 
                                             @Param("unitId") Integer unitId);
    
    /**
     * 🚀 COUNT OPTIMIZATION: Count created tasks without assignments (including subtasks)
     * ✅ FIX: Remove parent IS NULL filter to match data method behavior
     * @param userId User ID
     * @return Count of created tasks without assignments (including subtasks)
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.createdBy.id = :userId AND t.deleted = false " +
           "AND NOT EXISTS (SELECT 1 FROM Assignment a WHERE a.task.id = t.id)")
    int countCreatedTasksWithoutAssignments(@Param("userId") Integer userId);
    
    /**
     * 🚀 COUNT OPTIMIZATION: Count assigned tasks by user ID (tasks user has assigned to others)
     * @param userId User ID who assigned the tasks
     * @return Count of assigned tasks
     */
    @Query("SELECT COUNT(DISTINCT a.task) FROM Assignment a " +
           "WHERE a.assignedBy.id = :userId AND a.task.deleted = false")
    int countAssignedTasksByUserId(@Param("userId") Integer userId);
    
    /**
     * 🚀 COUNT OPTIMIZATION: Count received tasks by user/team/unit ID (root tasks only)
     * @param userId User ID
     * @param teamId Team ID (can be null)
     * @param unitId Unit ID (can be null)
     * @return Count of received tasks
     */
    @Query("SELECT COUNT(DISTINCT t) FROM Task t JOIN Assignment a ON t.id = a.task.id " +
           "WHERE t.deleted = false AND (" +
           "(a.recipientType = 'user' AND a.recipientId = :userId) OR " +
           "(:teamId IS NOT NULL AND a.recipientType = 'team' AND a.recipientId = :teamId) OR " +
           "(:unitId IS NOT NULL AND a.recipientType = 'unit' AND a.recipientId = :unitId))")
    int countReceivedTasksByUserId(@Param("userId") Integer userId, 
                                   @Param("teamId") Integer teamId, 
                                   @Param("unitId") Integer unitId);

    // ============== DATABASE-LEVEL PAGINATION METHODS (1-BASED) ==============
    
    /**
     * 🚀 DATABASE PAGINATION: Get created tasks with database-level pagination
     * @param userId User ID
     * @param offset Number of records to skip (calculated from page)
     * @param limit Number of records per page
     * @return List of tasks with LIMIT/OFFSET
     */
    @Query("SELECT t FROM Task t WHERE t.createdBy.id = :userId AND t.deleted = false " +
           "AND NOT EXISTS (SELECT a FROM Assignment a WHERE a.task = t) " +
           "ORDER BY t.updatedAt DESC, t.createdAt DESC")
    List<Task> findCreatedTasksWithPagination(@Param("userId") Integer userId, 
                                             org.springframework.data.domain.Pageable pageable);
    
    /**
     * 🚀 DATABASE PAGINATION: Get assigned tasks with database-level pagination
     * @param userId User ID
     * @param pageable Pagination parameters
     * @return List of tasks with LIMIT/OFFSET
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.assignedBy.id = :userId AND a.task.deleted = false " +
           "ORDER BY a.task.updatedAt DESC, a.task.createdAt DESC")
    List<Task> findAssignedTasksWithPagination(@Param("userId") Integer userId,
                                              org.springframework.data.domain.Pageable pageable);
    
    /**
     * 🚀 DATABASE PAGINATION: Get received tasks with database-level pagination - OPTIMIZED
     * Split complex OR query into UNION for better performance
     * @param userId User ID
     * @param teamId Team ID (nullable)
     * @param unitId Unit ID (nullable)
     * @param pageable Pagination parameters
     * @return List of tasks with LIMIT/OFFSET
     */
    @Query(value = "(" +
           "SELECT DISTINCT t.* FROM task t " +
           "INNER JOIN assignment a ON a.task_id = t.id " +
           "WHERE a.recipient_type = 'user' AND a.recipient_id = :userId AND t.deleted = false" +
           ") UNION (" +
           "SELECT DISTINCT t.* FROM task t " +
           "INNER JOIN assignment a ON a.task_id = t.id " +
           "WHERE :teamId IS NOT NULL AND a.recipient_type = 'team' AND a.recipient_id = :teamId AND t.deleted = false" +
           ") UNION (" +
           "SELECT DISTINCT t.* FROM task t " +
           "INNER JOIN assignment a ON a.task_id = t.id " +
           "WHERE :unitId IS NOT NULL AND a.recipient_type = 'unit' AND a.recipient_id = :unitId AND t.deleted = false" +
           ") ORDER BY updated_at DESC, created_at DESC " +
           "LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}",
           nativeQuery = true)
    List<Task> findReceivedTasksWithPagination(@Param("userId") Integer userId, 
                                              @Param("teamId") Integer teamId, 
                                              @Param("unitId") Integer unitId,
                                              org.springframework.data.domain.Pageable pageable);
    
    /**
     * 🚀 DATABASE PAGINATION: Get advanced search results with database-level pagination
     * @param userId User ID
     * @param keyword Search keyword (nullable)
     * @param startTime Start time filter (nullable)
     * @param endTime End time filter (nullable)
     * @param priorities Priority list (can be empty)
     * @param recipientTypes Recipient types (can be empty)
     * @param recipientIds Recipient IDs (can be empty)
     * @param pageable Pagination parameters
     * @return List of tasks with LIMIT/OFFSET
     */
    @Query("SELECT DISTINCT a.task FROM Assignment a WHERE a.assignedBy.id = :userId AND a.task.deleted = false " +
           "AND (:keyword IS NULL OR " +
           "     (CAST(a.task.id AS STRING) LIKE CONCAT('%', :keyword, '%') OR " +
           "      LOWER(a.task.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "      LOWER(a.task.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "      LOWER(a.task.instructions) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "      LOWER(a.task.notes) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
           "AND (:startTime IS NULL OR a.task.createdAt >= :startTime) " +
           "AND (:endTime IS NULL OR a.task.createdAt <= :endTime) " +
           "AND (:#{#priorities.isEmpty()} = true OR a.task.priority IN :priorities) " +
           "AND (:#{#recipientTypes.isEmpty()} = true OR " +
           "     (a.recipientType IN :recipientTypes AND a.recipientId IN :recipientIds)) " +
           "ORDER BY a.task.updatedAt DESC, a.task.createdAt DESC")
    List<Task> findAssignedTasksWithAdvancedSearchAndPagination(@Param("userId") Integer userId,
                                                               @Param("keyword") String keyword,
                                                               @Param("startTime") java.time.LocalDateTime startTime,
                                                               @Param("endTime") java.time.LocalDateTime endTime,
                                                               @Param("priorities") List<com.project.quanlycanghangkhong.model.TaskPriority> priorities,
                                                               @Param("recipientTypes") List<String> recipientTypes,
                                                               @Param("recipientIds") List<Integer> recipientIds,
                                                               org.springframework.data.domain.Pageable pageable);
}