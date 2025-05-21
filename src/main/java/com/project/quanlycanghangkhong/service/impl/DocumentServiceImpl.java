package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.dto.CreateDocumentRequest;
import com.project.quanlycanghangkhong.dto.UpdateDocumentRequest;
import com.project.quanlycanghangkhong.model.Document;
import com.project.quanlycanghangkhong.model.Attachment;
import com.project.quanlycanghangkhong.repository.DocumentRepository;
import com.project.quanlycanghangkhong.service.DocumentService;
import com.project.quanlycanghangkhong.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;

    private DocumentDTO toDTO(Document doc) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(doc.getId());
        dto.setDocumentType(doc.getDocumentType());
        dto.setContent(doc.getContent());
        dto.setNotes(doc.getNotes());
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setUpdatedAt(doc.getUpdatedAt());
        if (doc.getAttachments() != null) {
            dto.setAttachments(doc.getAttachments().stream().map(this::toAttachmentDTO).collect(Collectors.toList()));
        }
        return dto;
    }
    private AttachmentDTO toAttachmentDTO(Attachment att) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(att.getId());
        dto.setFilePath(att.getFilePath());
        dto.setFileName(att.getFileName());
        dto.setFileSize(att.getFileSize());
        dto.setCreatedAt(att.getCreatedAt());
        return dto;
    }
    private Document toEntity(DocumentDTO dto) {
        Document doc = new Document();
        doc.setId(dto.getId());
        doc.setDocumentType(dto.getDocumentType());
        doc.setContent(dto.getContent());
        doc.setNotes(dto.getNotes());
        doc.setCreatedAt(dto.getCreatedAt());
        doc.setUpdatedAt(dto.getUpdatedAt());
        return doc;
    }
    private Document toEntity(CreateDocumentRequest request) {
        Document doc = new Document();
        doc.setDocumentType(request.getDocumentType());
        doc.setContent(request.getContent());
        doc.setNotes(request.getNotes());
        // attachments xử lý nếu cần
        return doc;
    }
    private void updateEntity(Document doc, UpdateDocumentRequest request) {
        doc.setDocumentType(request.getDocumentType());
        doc.setContent(request.getContent());
        doc.setNotes(request.getNotes());
        // attachments xử lý nếu cần
    }
    @Override
    public DocumentDTO createDocument(CreateDocumentRequest request) {
        Document doc = toEntity(request);
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());
        Document saved = documentRepository.save(doc);
        return toDTO(saved);
    }
    @Override
    public DocumentDTO updateDocument(Integer id, UpdateDocumentRequest request) {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null) return null;
        updateEntity(doc, request);
        doc.setUpdatedAt(LocalDateTime.now());
        Document saved = documentRepository.save(doc);
        return toDTO(saved);
    }
    @Override
    public void deleteDocument(Integer id) {
        documentRepository.deleteById(id);
    }
    @Override
    public DocumentDTO getDocumentById(Integer id) {
        return documentRepository.findById(id).map(this::toDTO).orElse(null);
    }
    @Override
    public List<DocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<DocumentDTO> bulkInsertDocuments(List<DocumentDTO> dtos) {
        List<Document> docs = dtos.stream().map(this::toEntity).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        docs.forEach(doc -> {
            doc.setCreatedAt(now);
            doc.setUpdatedAt(now);
        });
        List<Document> savedDocs = documentRepository.saveAll(docs);
        return savedDocs.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public void bulkDeleteDocuments(List<Integer> ids) {
        documentRepository.deleteAllById(ids);
    }
}
