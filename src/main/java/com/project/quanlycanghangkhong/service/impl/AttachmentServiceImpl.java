package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.dto.request.AttachmentAssignRequest;
import com.project.quanlycanghangkhong.model.Attachment;
import com.project.quanlycanghangkhong.model.Document;
import com.project.quanlycanghangkhong.repository.AttachmentRepository;
import com.project.quanlycanghangkhong.repository.DocumentRepository;
import com.project.quanlycanghangkhong.service.AttachmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

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
    public AttachmentDTO updateAttachmentFileName(Integer id, String fileName) {
        Attachment att = attachmentRepository.findById(id).orElse(null);
        if (att == null) return null;
        att.setFileName(fileName);
        Attachment saved = attachmentRepository.save(att);
        return toDTO(saved);
    }
    @Override
    public void deleteAttachment(Integer id) {
        Attachment att = attachmentRepository.findById(id).orElse(null);
        if (att != null) {
            att.setDeleted(true);
            attachmentRepository.save(att);
        }
    }
    @Override
    public List<AttachmentDTO> getAttachmentsByDocumentId(Integer documentId) {
        return attachmentRepository.findByDocument_IdAndIsDeletedFalse(documentId).stream().map(this::toDTO).collect(Collectors.toList());
    }
    @Override
    public void assignAttachmentsToDocument(Integer documentId, AttachmentAssignRequest request) {
        Document doc = documentRepository.findById(documentId).orElse(null);
        if (doc == null || request == null || request.getAttachmentIds() == null) return;
        List<Attachment> attachments = attachmentRepository.findAllByIdIn(request.getAttachmentIds());
        for (Attachment att : attachments) {
            att.setDocument(doc);
        }
        attachmentRepository.saveAll(attachments);
    }
    @Override
    public void removeAttachmentsFromDocument(Integer documentId, AttachmentAssignRequest request) {
        if (request == null || request.getAttachmentIds() == null) return;
        List<Attachment> attachments = attachmentRepository.findAllByIdIn(request.getAttachmentIds());
        for (Attachment att : attachments) {
            if (att.getDocument() != null && att.getDocument().getId().equals(documentId)) {
                att.setDocument(null);
            }
        }
        attachmentRepository.saveAll(attachments);
    }
    @Override
    public List<AttachmentDTO> getAllAttachments() {
        return attachmentRepository.findByIsDeletedFalse().stream().map(this::toDTO).collect(Collectors.toList());
    }
    @Override
    public AttachmentDTO getAttachmentById(Integer id) {
        Attachment att = attachmentRepository.findByIdAndIsDeletedFalse(id);
        return att == null ? null : toDTO(att);
    }
}
