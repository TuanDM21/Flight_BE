package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.model.Attachment;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.AttachmentRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.AttachmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy thông tin user hiện tại từ SecurityContext
     * @return User hiện tại hoặc null nếu không tìm thấy
     */
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName() != null) {
                String email = authentication.getName();
                return userRepository.findByEmail(email).orElse(null);
            }
        } catch (Exception e) {
            // Log error nếu cần
        }
        return null;
    }

    /**
     * Kiểm tra xem user hiện tại có phải là owner của attachment không
     * @param attachment Attachment cần kiểm tra
     * @return true nếu là owner, false nếu không
     */
    private boolean isOwner(Attachment attachment) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return false;
        
        // Chỉ kiểm tra owner - ai upload file thì toàn quyền file đó
        return attachment.getUploadedBy() != null && 
               attachment.getUploadedBy().getId().equals(currentUser.getId());
    }

    /**
     * Kiểm tra xem user hiện tại có quyền truy cập attachment không (chỉ owner)
     * @param attachment Attachment cần kiểm tra
     * @return true nếu có quyền truy cập, false nếu không
     */
    private boolean hasReadAccess(Attachment attachment) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return false;
        
        // Chỉ owner có quyền
        return isOwner(attachment);
    }

    /**
     * Kiểm tra xem user hiện tại có quyền chỉnh sửa attachment không (chỉ owner)
     * @param attachment Attachment cần kiểm tra
     * @return true nếu có quyền chỉnh sửa, false nếu không
     */
    private boolean hasWriteAccess(Attachment attachment) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return false;
        
        // Chỉ owner có quyền chỉnh sửa
        return isOwner(attachment);
    }

    /**
     * Kiểm tra xem user hiện tại có phải là admin không
     * @return true nếu là admin, false nếu không
     */
    private boolean isAdmin() {
        User currentUser = getCurrentUser();
        if (currentUser == null) return false;
        
        // Kiểm tra role admin thông qua Role entity
        if (currentUser.getRole() != null && currentUser.getRole().getRoleName() != null) {
            String roleName = currentUser.getRole().getRoleName();
            return "ADMIN".equals(roleName);
        }
        
        // Backup check qua email nếu role không có
        return "admin@admin.com".equals(currentUser.getEmail());
    }

    private AttachmentDTO toDTO(Attachment att) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(att.getId());
        dto.setFilePath(att.getFilePath());
        dto.setFileName(att.getFileName());
        dto.setFileSize(att.getFileSize());
        dto.setCreatedAt(att.getCreatedAt());
        
        // Map owner information
        if (att.getUploadedBy() != null) {
            com.project.quanlycanghangkhong.dto.UserDTO ownerDto = new com.project.quanlycanghangkhong.dto.UserDTO();
            ownerDto.setId(att.getUploadedBy().getId());
            ownerDto.setName(att.getUploadedBy().getName());
            ownerDto.setEmail(att.getUploadedBy().getEmail());
            dto.setUploadedBy(ownerDto);
        }
        
        return dto;
    }
    
    // THAY ĐỔI LOGIC NGHIỆP VỤ: Đã chuyển sang task-attachment trực tiếp
    // Document không còn quản lý attachment nữa
    /*
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
    */
    @Override
    public AttachmentDTO updateAttachment(Integer id, AttachmentDTO dto) {
        Attachment att = attachmentRepository.findById(id).orElse(null);
        if (att == null) return null;
        
        // 🔒 KIỂM TRA QUYỀN WRITE (owner hoặc shared với quyền READ-write)
        if (!hasWriteAccess(att)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa file này. Chỉ người upload file hoặc người được chia sẻ quyền chỉnh sửa mới có thể thực hiện.");
        }
        
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
        
        // 🔒 KIỂM TRA QUYỀN WRITE (owner hoặc shared với quyền read-write)
        if (!hasWriteAccess(att)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa file này. Chỉ người upload file hoặc người được chia sẻ quyền chỉnh sửa mới có thể thực hiện.");
        }
        
        att.setFileName(fileName);
        Attachment saved = attachmentRepository.save(att);
        return toDTO(saved);
    }
    
    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteAttachment(Integer id) {
        Attachment att = attachmentRepository.findById(id).orElse(null);
        if (att != null) {
            // Chỉ owner mới có quyền xóa
            if (!isOwner(att)) {
                throw new RuntimeException("Bạn không có quyền xóa file này. Chỉ người upload file mới có thể thực hiện.");
            }
            
            // Thực hiện soft delete attachment
            att.setDeleted(true);
            attachmentRepository.save(att);
        }
    }
    
    // === BUSINESS LOGIC: CHỈ SỬ DỤNG TASK-ATTACHMENT TRỰC TIẾP ===
    // Document đã được loại bỏ hoàn toàn khỏi hệ thống quản lý file
    
    @Override
    public List<AttachmentDTO> getAllAttachments() {
        // Kiểm tra quyền admin
        if (!isAdmin()) {
            throw new RuntimeException("Bạn không có quyền truy cập chức năng này. Chỉ admin mới có thể xem tất cả file.");
        }
        
        // Admin có thể xem tất cả file, kể cả file đã bị xóa mềm
        List<Attachment> allAttachments = attachmentRepository.findAll();
        return allAttachments.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    public AttachmentDTO getAttachmentById(Integer id) {
        Attachment att = attachmentRepository.findByIdAndIsDeletedFalse(id);
        if (att == null) return null;
        
        // 🔒 KIỂM TRA QUYỀN READ (owner hoặc được chia sẻ)
        if (!hasReadAccess(att)) {
            throw new RuntimeException("Bạn không có quyền truy cập file này.");
        }
        
        return toDTO(att);
    }
    
    @Override
    public List<AttachmentDTO> getAvailableAttachments() {
        // Chỉ admin mới có thể xem tất cả file chưa gán
        if (!isAdmin()) {
            throw new RuntimeException("Bạn không có quyền truy cập chức năng này. Chỉ admin mới có thể xem tất cả file chưa gán.");
        }
        
        List<Attachment> availableAttachments = attachmentRepository.findByTaskIsNullAndIsDeletedFalse();
        return availableAttachments.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
