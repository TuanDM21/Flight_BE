package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.dto.request.AttachmentAssignRequest;
import com.project.quanlycanghangkhong.model.Attachment;
import com.project.quanlycanghangkhong.model.Document;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.AttachmentRepository;
import com.project.quanlycanghangkhong.repository.DocumentRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.AttachmentService;
import com.project.quanlycanghangkhong.service.FileShareService;
import com.project.quanlycanghangkhong.dto.FileShareDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileShareService fileShareService;

    // Thêm import và dependency cho FileShareRepository
    @Autowired
    private com.project.quanlycanghangkhong.repository.FileShareRepository fileShareRepository;

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
     * Kiểm tra xem user hiện tại có quyền truy cập attachment không (owner hoặc được chia sẻ)
     * @param attachment Attachment cần kiểm tra
     * @return true nếu có quyền truy cập, false nếu không
     */
    private boolean hasReadAccess(Attachment attachment) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return false;
        
        // Owner luôn có quyền
        if (isOwner(attachment)) return true;
        
        // Kiểm tra file sharing
        FileShareDTO shareAccess = fileShareService.checkFileAccess(attachment.getId(), currentUser.getId());
        return shareAccess != null;
    }

    /**
     * Kiểm tra xem user hiện tại có quyền chỉnh sửa attachment không
     * @param attachment Attachment cần kiểm tra
     * @return true nếu có quyền chỉnh sửa, false nếu không
     */
    private boolean hasWriteAccess(Attachment attachment) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return false;
        
        // Owner luôn có quyền chỉnh sửa
        if (isOwner(attachment)) return true;
        
        // Kiểm tra file sharing với quyền write
        return fileShareService.hasWritePermission(attachment.getId(), currentUser.getId());
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
            return "admin".equalsIgnoreCase(roleName) || 
                   "administrator".equalsIgnoreCase(roleName) ||
                   "ADMIN".equals(roleName) ||
                   "Admin".equals(roleName);
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
        
        // 🔥 NEW: Tính shared count (số lượng người được chia sẻ)
        int sharedCount = fileShareRepository.countByAttachmentAndIsActiveTrue(att);
        dto.setSharedCount(sharedCount);
        
        return dto;
    }

    // 🔥 NEW: Method chuyên dụng cho "my files" - bao gồm shared count
    private AttachmentDTO toDTOWithSharedCount(Attachment att) {
        return toDTO(att); // Đã bao gồm shared count trong toDTO
    }

    // 🔥 NEW: Method chuyên dụng cho "shared with me" - không cần shared count (vì không phải owner)
    private AttachmentDTO toDTOForSharedFile(Attachment att) {
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
        
        // Đặt shared count = null hoặc 0 vì user không phải owner
        dto.setSharedCount(0);
        
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
            // 🔒 CHỈ OWNER MỚI CÓ QUYỀN XÓA (không cho phép shared user xóa)
            if (!isOwner(att)) {
                throw new RuntimeException("Bạn không có quyền xóa file này. Chỉ người upload file mới có thể thực hiện.");
            }
            
            // 🔥 XÓA TẤT CẢ FILE SHARES (CẢ ACTIVE VÀ INACTIVE) LIÊN QUAN TRƯỚC KHI SOFT DELETE
            List<com.project.quanlycanghangkhong.model.FileShare> allFileShares = 
                fileShareRepository.findByAttachment(att);
            if (!allFileShares.isEmpty()) {
                // Hard delete tất cả file shares để tránh foreign key constraint
                fileShareRepository.deleteAll(allFileShares);
            }
            
            // Thực hiện soft delete attachment
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
    public List<AttachmentDTO> getMyAttachments() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        // Lấy chỉ file của user hiện tại (owner)
        List<Attachment> myFiles = attachmentRepository.findByUploadedByAndIsDeletedFalse(currentUser);
        return myFiles.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Lấy danh sách file có quyền truy cập (bao gồm file của mình và file được chia sẻ)
     * @return Danh sách file có quyền truy cập
     */
    @Override
    public List<AttachmentDTO> getAccessibleAttachments() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        // Lấy file của mình
        List<Attachment> myFiles = attachmentRepository.findByUploadedByAndIsDeletedFalse(currentUser);
        List<AttachmentDTO> result = myFiles.stream().map(this::toDTO).collect(Collectors.toList());
        
        // Lấy file được chia sẻ với mình
        List<FileShareDTO> sharedFiles = fileShareService.getSharedWithMe();
        for (FileShareDTO shareDto : sharedFiles) {
            Attachment sharedAttachment = attachmentRepository.findByIdAndIsDeletedFalse(shareDto.getAttachmentId());
            if (sharedAttachment != null) {
                AttachmentDTO dto = toDTOForSharedFile(sharedAttachment);
                // Thêm thông tin về quyền chia sẻ
                dto.getClass(); // Có thể extend DTO để thêm field sharePermission nếu cần
                result.add(dto);
            }
        }
        
        return result;
    }
}
