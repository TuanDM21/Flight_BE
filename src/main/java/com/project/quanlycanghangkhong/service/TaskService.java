package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.request.CreateTaskRequest;
import com.project.quanlycanghangkhong.request.CreateSubtaskRequest;
import com.project.quanlycanghangkhong.dto.TaskDTO;
import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import com.project.quanlycanghangkhong.dto.UpdateTaskDTO;
import com.project.quanlycanghangkhong.dto.AttachmentDTO;

// ✅ PRIORITY 3: Simplified DTOs imports

import java.util.List;

public interface TaskService {
    /**
     * Tạo task với assignment và attachment trực tiếp
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Thay thế cách tiếp cận dựa trên document bằng việc gán attachment trực tiếp
     * @param request Yêu cầu tạo task với danh sách ID attachment
     * @return DTO task đã tạo
     */
    TaskDTO createTaskWithAssignmentsAndAttachments(CreateTaskRequest request);
    TaskDTO createTask(TaskDTO taskDTO);
    TaskDTO updateTask(Integer id, UpdateTaskDTO updateTaskDTO);
    void deleteTask(Integer id);
    void bulkDeleteTasks(List<Integer> taskIds);
    TaskDTO getTaskById(Integer id);
    List<TaskDTO> getAllTasks();
    TaskDetailDTO getTaskDetailById(Integer id);
    List<TaskDetailDTO> getAllTaskDetails();
    void updateTaskStatus(com.project.quanlycanghangkhong.model.Task task);
    
    // Method mới để lấy task theo loại
    List<TaskDetailDTO> getMyTasks(String type);
    
    /**
     * Lấy tasks với count information
     * @param type Loại task (created, assigned, received)
     * @return Response bao gồm danh sách task và count metadata
     */
    com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse getMyTasksWithCount(String type);
    
    /**
     * Lấy tasks với count information - STANDARDIZED VERSION
     * Returns data in standardized ApiResponseCustom structure
     * @param type Loại task (created, assigned, received)
     * @return MyTasksData với cấu trúc chuẩn hóa
     */
    com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithCountStandardized(String type);
    
    /**
     * Lấy tasks với count information và filter - STANDARDIZED VERSION WITH FILTER
     * Returns data in standardized ApiResponseCustom structure với filter support
     * @param type Loại task (created, assigned, received)
     * @param filter Filter áp dụng (chỉ cho type=assigned): completed, pending, urgent, overdue
     * @return MyTasksData với cấu trúc chuẩn hóa đã được filter
     */
    com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithCountStandardized(String type, String status);
    
    /**
     * Lấy tasks với count information và PAGINATION - STANDARDIZED VERSION WITH PAGINATION
     * Returns data in standardized ApiResponseCustom structure với pagination support
     * @param type Loại task (created, assigned, received)
     * @param filter Filter áp dụng (chỉ cho type=assigned): completed, pending, urgent, overdue
     * @param page Số trang (bắt đầu từ 0)
     * @param size Số lượng items per page (max 100)
     * @return MyTasksData với cấu trúc chuẩn hóa đã được phân trang
     */
    com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithCountStandardizedAndPagination(String type, String status, Integer page, Integer size);
    
    /**
     * 🚀 ULTRA OPTIMIZED: Get my tasks with batch loading - Performance target <500ms
     * Zero N+1 queries, batch loading for all relationships
     * @param type Task type (created, assigned, received)
     * @return MyTasksData with ultra-fast performance
     */
    com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithCountStandardizedUltraFast(String type);
    
    /**
     * Tìm kiếm nâng cao tasks với nhiều tiêu chí - ADVANCED SEARCH (query parameters)
     * Chỉ áp dụng cho type=assigned với các tiêu chí tìm kiếm nâng cao qua query parameters
     * @param type Loại task (chỉ "assigned" được hỗ trợ)
     * @param filter Filter type: completed, pending, urgent, overdue
     * @param keyword Từ khóa tìm kiếm trong title/content
     * @param startTime Thời gian bắt đầu (ISO format)
     * @param endTime Thời gian kết thúc (ISO format) 
    // MÔ HÌNH ADJACENCY LIST: Các method subtask cho cấu trúc phân cấp
    
    /**
     * Tạo subtask dưới một task cha trong mô hình Adjacency List
     * @param parentId ID task cha (từ path parameter)
     * @param request Yêu cầu tạo subtask (không chứa parentId)
     * @return DTO subtask đã tạo
     */
    TaskDTO createSubtask(Integer parentId, CreateSubtaskRequest request);
    
    /**
     * Lấy tất cả subtask (task con) của một task cha trong mô hình Adjacency List
     * @param parentId ID task cha
     * @return Danh sách task con
    /**
     * Lấy tất cả subtask (task con) của một task cha trong mô hình Adjacency List
     * @param parentId ID task cha
     * @return Danh sách task con
     */
    List<TaskDetailDTO> getSubtasks(Integer parentId);
    
    /**
     * Lấy toàn bộ cây con (subtree) của một task - bao gồm task đó và tất cả subtask bên dưới
     * @param taskId ID task gốc để lấy cây con
     * @return Danh sách task theo thứ tự depth-first (task cha trước, subtask sau)
     */
    List<TaskDetailDTO> getTaskSubtree(Integer taskId);
    
    /**
     * Lấy toàn bộ cây con (subtree) của một task với TaskSubtreeDTO
     * @param taskId ID task gốc để lấy cây con
     * @return Danh sách TaskSubtreeDTO theo thứ tự depth-first (task cha trước, subtask sau)
     */
    List<com.project.quanlycanghangkhong.dto.TaskSubtreeDTO> getTaskSubtreeAsSubtreeDTO(Integer taskId);
    
    /**
     * Lấy toàn bộ cây con (subtree) với cấu trúc phân cấp nested - dễ dàng cho frontend hiển thị
     * @param taskId ID task gốc để lấy cây con
     * @return TaskTreeDTO với cấu trúc nested hierarchy
     */
    com.project.quanlycanghangkhong.dto.TaskTreeDTO getTaskSubtreeHierarchical(Integer taskId);
    
    // === ATTACHMENT MANAGEMENT ===
    // Attachment chỉ được quản lý thông qua createTask và updateTask
    // Đã loại bỏ assignAttachmentsToTask và removeAttachmentsFromTask
    
    /**
     * Lấy tất cả attachment được liên kết trực tiếp với một task
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Truy vấn quan hệ task-attachment trực tiếp
     * @param taskId ID Task
     * @return Danh sách attachment được liên kết trực tiếp với task
     */
    List<AttachmentDTO> getTaskAttachments(Integer taskId);
    
    /**
     * Thêm attachments vào task cụ thể
     * @param taskId ID của task
     * @param attachmentIds Danh sách ID attachment cần thêm
     * @return Danh sách attachment đã được thêm
     */
    List<AttachmentDTO> addAttachmentsToTask(Integer taskId, List<Integer> attachmentIds);
    
    /**
     * Xóa attachments khỏi task cụ thể
     * @param taskId ID của task
     * @param attachmentIds Danh sách ID attachment cần xóa
     * @return Số lượng attachment đã được xóa
     */
    int removeAttachmentsFromTask(Integer taskId, List<Integer> attachmentIds);
    
    // ============== SEARCH & FILTER METHODS ==============
    
    // ============== DATABASE-LEVEL PAGINATION METHODS (OPTIMIZED) ==============
    
    /**
     * 🚀 DATABASE PAGINATION: Get my tasks with database-level pagination (1-based)
     * Performance: Uses LIMIT/OFFSET at database level instead of memory slicing
     * @param type Task type (created, assigned, received)
     * @param filter Filter (only for assigned type)
     * @param page Page number (1-based: 1, 2, 3...)
     * @param size Page size (max 100, default 20)
     * @return MyTasksData with pagination info
     */
    com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithCountStandardizedAndPaginationOptimized(
        String type, String status, Integer page, Integer size);
    
    /**
     * 🚀 DATABASE PAGINATION: Advanced search with database-level pagination (1-based)
     * Performance: Uses LIMIT/OFFSET at database level for advanced search
     * @param type Task type (only assigned supported)
     * @param filter Filter type
     * @param keyword Search keyword
     * @param startTime Start time filter
     * @param endTime End time filter
     * @param priorities Priority list
     * @param recipientTypes Recipient type list
     * @param recipientIds Recipient ID list
     * @param page Page number (1-based: 1, 2, 3...)
     * @param size Page size (max 100, default 20)
     * @return MyTasksData with pagination info
     */
    com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithAdvancedSearchAndPaginationOptimized(
        String type, String status, String keyword, String startTime, String endTime,
        java.util.List<String> priorities, java.util.List<String> recipientTypes, java.util.List<Integer> recipientIds,
        Integer page, Integer size);

    // ============== UNIT TASKS METHODS (ROLE-BASED PERMISSIONS) ==============
    
    /**
     * 🏢 UNIT TASKS: Get all tasks with role-based permissions
     * ADMIN/DIRECTOR/VICE_DIRECTOR: Xem tất cả tasks
     * Các role khác: Chỉ xem tasks của team mình
     * @param status Status filter (optional)
     * @return MyTasksData with role-based filtered tasks
     */
    com.project.quanlycanghangkhong.dto.MyTasksData getUnitTasks(String status);
    
    /**
     * 🏢 UNIT TASKS: Get tasks with pagination and role-based permissions
     * @param status Status filter (optional)
     * @param page Page number (1-based)
     * @param size Page size
     * @return MyTasksData with pagination and role-based filtering
     */
    com.project.quanlycanghangkhong.dto.MyTasksData getUnitTasksWithPagination(String status, Integer page, Integer size);
    
    /**
     * 🏢 UNIT TASKS: Get tasks with advanced search, pagination and role-based permissions
     * @param status Status filter (optional)
     * @param keyword Search keyword
     * @param startTime Start time filter
     * @param endTime End time filter
     * @param priorities Priority filters
     * @param page Page number (1-based)
     * @param size Page size
     * @return MyTasksData with advanced search and role-based filtering
     */
    com.project.quanlycanghangkhong.dto.MyTasksData getUnitTasksWithAdvancedSearchAndPagination(
        String status, String keyword, String startTime, String endTime, 
        java.util.List<String> priorities, Integer page, Integer size);
}