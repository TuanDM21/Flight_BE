package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
// import com.project.quanlycanghangkhong.request.AttachmentAssignRequest; // Không cần nữa vì đã chuyển sang task-attachment
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
}
