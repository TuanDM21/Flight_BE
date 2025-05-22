package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.dto.request.AttachmentAssignRequest;
import java.util.List;

public interface AttachmentService {
    AttachmentDTO addAttachmentToDocument(Integer documentId, AttachmentDTO dto);
    AttachmentDTO updateAttachment(Integer id, AttachmentDTO dto);
    void deleteAttachment(Integer id);
    List<AttachmentDTO> getAttachmentsByDocumentId(Integer documentId);
    void assignAttachmentsToDocument(Integer documentId, AttachmentAssignRequest request);
    void removeAttachmentsFromDocument(Integer documentId, AttachmentAssignRequest request);
    List<AttachmentDTO> getAllAttachments();
    AttachmentDTO getAttachmentById(Integer id);
    AttachmentDTO updateAttachmentFileName(Integer id, String fileName);
}
