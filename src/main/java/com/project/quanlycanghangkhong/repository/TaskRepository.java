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
    
    /**
     * 🔴 KHÔNG SỬ DỤNG: Cú pháp thay thế để tìm task gốc 
     * CÓ THỂ XÓA: Trùng lặp với findByParentIsNullAndDeletedFalse()
     * @return Danh sách task gốc
     */
    // List<Task> findByParentIdIsNullAndDeletedFalse(); // Cú pháp thay thế cho task gốc
}