package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.model.Attachment;
import com.project.quanlycanghangkhong.model.Document;
import com.project.quanlycanghangkhong.repository.AttachmentRepository;
import com.project.quanlycanghangkhong.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private DocumentRepository documentRepository;

    private AttachmentDTO toDTO(Attachment att) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(att.getId());
        dto.setFilePath(att.getFilePath());
        dto.setFileName(att.getFileName());
        dto.setFileSize(att.getFileSize());
        dto.setCreatedAt(att.getCreatedAt());
        return dto;
    }
    @Override
    public AttachmentDTO addAttachmentToDocument(Integer documentId, AttachmentDTO dto) {
        Document doc = documentRepository.findById(documentId).orElse(null);
        if (doc == null) return null;
        Attachment att = new Attachment();
        att.setDocument(doc);
        att.setFilePath(dto.getFilePath());
        att.setFileName(dto.getFileName());
        att.setFileSize(dto.getFileSize());
        att.setCreatedAt(LocalDateTime.now());
        return toDTO(attachmentRepository.save(att));
    }
    @Override
    public AttachmentDTO updateAttachment(Integer id, AttachmentDTO dto) {
        Attachment att = attachmentRepository.findById(id).orElse(null);
        if (att == null) return null;
        att.setFilePath(dto.getFilePath());
        att.setFileName(dto.getFileName());
        att.setFileSize(dto.getFileSize());
        Attachment saved = attachmentRepository.save(att);
        return toDTO(saved);
    }
    @Override
    public void deleteAttachment(Integer id) {
        attachmentRepository.deleteById(id);
    }
    @Override
    public List<AttachmentDTO> getAttachmentsByDocumentId(Integer documentId) {
        return attachmentRepository.findByDocument_Id(documentId).stream().map(this::toDTO).collect(Collectors.toList());
    }
}
