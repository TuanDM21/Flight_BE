package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}