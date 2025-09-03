package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    
    // ============== CORE ATTACHMENT OPERATIONS (ĐANG SỬ DỤNG) ==============
    
    /**
     * 🟢 ĐANG SỬ DỤNG: Lấy nhiều attachment theo danh sách ID
     * Được dùng trong: assignAttachmentsToTask(), removeAttachmentsFromTask()
     */
    List<Attachment> findAllByIdIn(List<Integer> ids);
    
    /**
     * 🟢 ĐANG SỬ DỤNG: Lấy tất cả attachment chưa bị xoá mềm
     * Dùng cho các API quản lý attachment tổng quát
     */
    List<Attachment> findByIsDeletedFalse();
    
    /**
     * 🟢 ĐANG SỬ DỤNG: Lấy attachment theo id và chưa bị xoá mềm
     * Dùng cho validation và chi tiết attachment
     */
    Attachment findByIdAndIsDeletedFalse(Integer id);
    
    // ============== TASK-ATTACHMENT DIRECT RELATIONSHIP (ĐANG SỬ DỤNG) ==============
    
    /**
     * 🟢 ĐANG SỬ DỤNG: Tìm tất cả attachment được liên kết trực tiếp với một task cụ thể
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Quan hệ task-attachment trực tiếp thay thế cách tiếp cận dựa trên document
     * Được dùng trong: getTaskDetailById(), getTaskAttachments()
     * @param taskId ID Task
     * @return Danh sách attachment được liên kết trực tiếp với task
     */
    List<Attachment> findByTask_IdAndIsDeletedFalse(Integer taskId);
    
        /**
     * 🚀 ULTRA OPTIMIZED: Batch load attachments without heavy JOINs  
     * Only load essential attachment data, users will be batch loaded separately
     * @param taskIds List of task IDs
     * @return List of attachments with minimal data
     */
    @Query("SELECT a FROM Attachment a WHERE a.task.id IN :taskIds AND a.isDeleted = false ORDER BY a.createdAt DESC")
    List<Attachment> findByTaskIdInAndIsDeletedFalse(@Param("taskIds") List<Integer> taskIds);
    
    // ============== UTILITY QUERIES (ĐANG BỔ SUNG) ==============
    
    /**
     * � ĐANG BỔ SUNG: Tìm tất cả attachment chưa được gán cho task nào
     * HỮU ÍCH: Để lấy danh sách file có thể gán vào task mới
     * @return Danh sách attachment chưa được gán vào task
     */
    List<Attachment> findByTaskIsNullAndIsDeletedFalse();
    
    /**
     *  CÂN NHẮC: Tìm tất cả attachment không được gán cho task hoặc document nào (hoàn toàn mồ côi)
     * CÓ THỂ HỮU ÍCH: Để cleanup file không sử dụng
     * @return Danh sách attachment hoàn toàn chưa được gán
     */
    // List<Attachment> findByTaskIsNullAndDocumentIsNullAndIsDeletedFalse(); // Hoàn toàn chưa được gán
    
    /**
     * 🚀 BATCH LOADING: Load attachments cho nhiều tasks cùng lúc để tránh N+1
     * @param taskIds List of task IDs
     * @return List attachments for multiple tasks
     */
    @Query("SELECT a FROM Attachment a WHERE a.task.id IN :taskIds AND a.isDeleted = false")
    List<Attachment> findByTaskIdsAndIsDeletedFalse(@Param("taskIds") List<Integer> taskIds);

    /**
     * 🟢 BATCH LOADING: Lấy tất cả attachments cho nhiều task ID một lần
     * PERFORMANCE: Để tránh N+1 query trong batch processing
     * Dùng cho: ultra-fast native queries với batch loading
     * @param taskIds Danh sách task ID
     * @return Map<taskId, List<Attachment>> grouped by task
     */
    List<Attachment> findByTask_IdInAndIsDeletedFalse(List<Integer> taskIds);
}