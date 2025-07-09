package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Attachment;
import com.project.quanlycanghangkhong.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    
    // ============== LEGACY DOCUMENT OPERATIONS (CÂN NHẮC XÓA) ==============
    
    /**
     * 🟡 CÂN NHẮC XÓA: Lấy attachment theo document ID (logic cũ)
     * THAY ĐỔI NGHIỆP VỤ: Đã chuyển sang task-attachment trực tiếp
     * Có thể cần giữ lại cho backward compatibility với document system
     */
    List<Attachment> findByDocument_Id(Integer documentId);
    
    /**
     * 🟡 CÂN NHẮC XÓA: Lấy attachment theo documentId và chưa bị xoá mềm (logic cũ)
     * THAY ĐỔI NGHIỆP VỤ: Đã chuyển sang task-attachment trực tiếp  
     */
    List<Attachment> findByDocument_IdAndIsDeletedFalse(Integer documentId);

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
    
    /**
     * � ĐANG SỬ DỤNG: Lấy attachment theo owner và chưa bị xoá mềm
     * Dùng cho quản lý file theo user ownership
     */
    List<Attachment> findByUploadedByAndIsDeletedFalse(User uploadedBy);
    
    // ============== TASK-ATTACHMENT DIRECT RELATIONSHIP (ĐANG SỬ DỤNG) ==============
    
    /**
     * 🟢 ĐANG SỬ DỤNG: Tìm tất cả attachment được liên kết trực tiếp với một task cụ thể
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Quan hệ task-attachment trực tiếp thay thế cách tiếp cận dựa trên document
     * Được dùng trong: getTaskDetailById(), getTaskAttachments()
     * @param taskId ID Task
     * @return Danh sách attachment được liên kết trực tiếp với task
     */
    List<Attachment> findByTask_IdAndIsDeletedFalse(Integer taskId);
    
    // ============== UTILITY QUERIES (ĐANG BỔ SUNG) ==============
    
    /**
     * � ĐANG BỔ SUNG: Tìm tất cả attachment chưa được gán cho task nào
     * HỮU ÍCH: Để lấy danh sách file có thể gán vào task mới
     * @return Danh sách attachment chưa được gán vào task
     */
    List<Attachment> findByTaskIsNullAndIsDeletedFalse();
    
    /**
     * 🟢 ĐANG BỔ SUNG: Tìm attachment chưa gán của một user cụ thể
     * HỮU ÍCH: User chỉ thấy file của mình để gán
     * @param uploadedBy User đã upload
     * @return Danh sách attachment của user chưa được gán
     */
    List<Attachment> findByTaskIsNullAndUploadedByAndIsDeletedFalse(User uploadedBy);
    
    /**
     * 🔴 CÂN NHẮC: Tìm tất cả attachment không được gán cho task hoặc document nào (hoàn toàn mồ côi)
     * CÓ THỂ HỮU ÍCH: Để cleanup file không sử dụng
     * @return Danh sách attachment hoàn toàn chưa được gán
     */
    // List<Attachment> findByTaskIsNullAndDocumentIsNullAndIsDeletedFalse(); // Hoàn toàn chưa được gán
}