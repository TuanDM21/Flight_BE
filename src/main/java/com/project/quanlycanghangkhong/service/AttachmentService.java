package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import java.util.List;

public interface AttachmentService {
    AttachmentDTO addAttachmentToDocument(Integer documentId, AttachmentDTO dto);
    AttachmentDTO updateAttachment(Integer id, AttachmentDTO dto);
    void deleteAttachment(Integer id);
    List<AttachmentDTO> getAttachmentsByDocumentId(Integer documentId);
}
