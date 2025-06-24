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
import com.project.quanlycanghangkhong.model.TaskDocument;
import com.project.quanlycanghangkhong.repository.TaskDocumentRepository;
import com.project.quanlycanghangkhong.repository.EvaluationIssueDocumentRepository;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.dto.UserDTO;
import com.project.quanlycanghangkhong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private TaskDocumentRepository taskDocumentRepository;
    @Autowired
    private EvaluationIssueDocumentRepository evaluationIssueDocumentRepository;
    @Autowired
    private UserRepository userRepository;

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
        if (doc.getCreatedBy() != null) {
            dto.setCreatedByUser(new UserDTO(doc.getCreatedBy()));
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
        // Lấy user hiện tại từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            // Tìm user theo email
            User user = userRepository.findByEmail(email).orElse(null);
            doc.setCreatedBy(user);
        }
        Document saved = documentRepository.save(doc);
        // Gán các attachment đã upload vào document này
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            List<Attachment> atts = attachmentRepository.findAllByIdIn(request.getAttachmentIds());
            for (Attachment att : atts) {
                att.setDocument(saved);
            }
            attachmentRepository.saveAll(atts);
        }
        return toDTO(saved);
    }
    @Override
    public DocumentDTO updateDocument(Integer id, UpdateDocumentRequest request) {
        Document doc = documentRepository.findById(id).orElse(null);
        if (doc == null) return null;
        updateEntity(doc, request);
        doc.setUpdatedAt(LocalDateTime.now());
        Document saved = documentRepository.save(doc);
        
        // ✅ OPTION 2 (ADD): Đơn giản - chỉ ADD thêm attachment mới
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            // Chỉ cần lấy các attachment mới và gán vào document
            List<Attachment> newAtts = attachmentRepository.findAllByIdIn(request.getAttachmentIds());
            for (Attachment att : newAtts) {
                att.setDocument(saved);
            }
            attachmentRepository.saveAll(newAtts);
        } else if (request.getAttachmentIds() != null && request.getAttachmentIds().isEmpty()) {
            // ✅ Trường hợp muốn XÓA HẾT attachment (gửi mảng rỗng)
            List<Attachment> oldAtts = attachmentRepository.findByDocument_IdAndIsDeletedFalse(id);
            for (Attachment att : oldAtts) {
                att.setDocument(null);
            }
            attachmentRepository.saveAll(oldAtts);
        }
        // ✅ Trường hợp attachmentIds == null: KHÔNG làm gì với attachment (giữ nguyên)
        
        return toDTO(saved);
    }
    @Transactional
    @Override
    public void deleteDocument(Integer id) {
        // Tìm document trước để đảm bảo nó tồn tại
        Document document = documentRepository.findById(id).orElse(null);
        if (document == null) {
            throw new RuntimeException("Không tìm thấy document với id: " + id);
        }
        
        // Gỡ document khỏi tất cả evaluation issues trước
        evaluationIssueDocumentRepository.deleteAllByDocument_Id(id);
        
        // Tìm tất cả các task đang sử dụng document này
        List<TaskDocument> taskDocuments = taskDocumentRepository.findAllByDocument_Id(id);
        
        // Gỡ document khỏi tất cả các task trước
        for (TaskDocument taskDoc : taskDocuments) {
            taskDocumentRepository.delete(taskDoc);
        }
        
        // Gỡ liên kết tất cả Attachment khỏi Document để giữ chúng
        List<Attachment> attachments = attachmentRepository.findByDocument_IdAndIsDeletedFalse(id);
        for (Attachment att : attachments) {
            att.setDocument(null); // Gỡ liên kết với document
        }
        attachmentRepository.saveAll(attachments);
        
        // Xóa document (bây giờ không còn attachment nào bị xóa theo)
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
    @Transactional
    public void bulkDeleteDocuments(List<Integer> ids) {
        // Xử lý từng document để gỡ tất cả liên kết trước
        for (Integer id : ids) {
            // Gỡ document khỏi tất cả evaluation issues
            evaluationIssueDocumentRepository.deleteAllByDocument_Id(id);
            
            // Gỡ document khỏi tất cả tasks
            List<TaskDocument> taskDocuments = taskDocumentRepository.findAllByDocument_Id(id);
            for (TaskDocument taskDoc : taskDocuments) {
                taskDocumentRepository.delete(taskDoc);
            }
            
            // Gỡ liên kết tất cả Attachment khỏi Document để giữ chúng
            List<Attachment> attachments = attachmentRepository.findByDocument_IdAndIsDeletedFalse(id);
            for (Attachment att : attachments) {
                att.setDocument(null); // Gỡ liên kết với document
            }
            attachmentRepository.saveAll(attachments);
        }
        
        // Cuối cùng mới xóa tất cả documents
        documentRepository.deleteAllById(ids);
    }
}
