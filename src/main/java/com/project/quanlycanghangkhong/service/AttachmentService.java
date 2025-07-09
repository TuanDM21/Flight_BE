package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
// import com.project.quanlycanghangkhong.dto.request.AttachmentAssignRequest; // Không cần nữa vì đã chuyển sang task-attachment
import java.util.List;

public interface AttachmentService {
    // THAY ĐỔI LOGIC NGHIỆP VỤ: Đã chuyển sang task-attachment trực tiếp
    // Document không còn quản lý attachment nữa
    // AttachmentDTO addAttachmentToDocument(Integer documentId, AttachmentDTO dto);
    // List<AttachmentDTO> getAttachmentsByDocumentId(Integer documentId);
    // void assignAttachmentsToDocument(Integer documentId, AttachmentAssignRequest request);
    // void removeAttachmentsFromDocument(Integer documentId, AttachmentAssignRequest request);
    
    AttachmentDTO updateAttachment(Integer id, AttachmentDTO dto);
    void deleteAttachment(Integer id);
    List<AttachmentDTO> getAllAttachments();
    AttachmentDTO getAttachmentById(Integer id);
    AttachmentDTO updateAttachmentFileName(Integer id, String fileName);
    List<AttachmentDTO> getMyAttachments();
    
    /**
     * Lấy danh sách file có quyền truy cập (bao gồm file của mình và file được chia sẻ)
     * @return Danh sách file có quyền truy cập
     */
    List<AttachmentDTO> getAccessibleAttachments();
    
    /**
     * Lấy danh sách attachment chưa được gán vào task nào (có thể gán được)
     * @return Danh sách attachment chưa gán
     */
    List<AttachmentDTO> getAvailableAttachments();
    
    /**
     * Lấy danh sách attachment chưa gán của user hiện tại
     * @return Danh sách attachment của tôi chưa gán
     */
    List<AttachmentDTO> getMyAvailableAttachments();
}
