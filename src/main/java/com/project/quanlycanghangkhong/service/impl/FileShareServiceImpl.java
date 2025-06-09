package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.FileShareDTO;
import com.project.quanlycanghangkhong.dto.UserDTO;
import com.project.quanlycanghangkhong.model.*;
import com.project.quanlycanghangkhong.repository.AttachmentRepository;
import com.project.quanlycanghangkhong.repository.FileShareRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.FileShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class FileShareServiceImpl implements FileShareService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileShareServiceImpl.class);
    
    @Autowired
    private FileShareRepository fileShareRepository;
    
    @Autowired
    private AttachmentRepository attachmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Lấy thông tin user hiện tại từ SecurityContext
     */
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName() != null) {
                String email = authentication.getName();
                return userRepository.findByEmail(email).orElse(null);
            }
        } catch (Exception e) {
            logger.error("Error getting current user", e);
        }
        return null;
    }
    
    /**
     * Chuyển đổi FileShare entity sang DTO (đơn giản tối đa - không có permission, expires_at, note)
     */
    private FileShareDTO toDTO(FileShare fileShare) {
        FileShareDTO dto = new FileShareDTO();
        dto.setId(fileShare.getId());
        dto.setAttachmentId(fileShare.getAttachment().getId());
        dto.setFileName(fileShare.getAttachment().getFileName());
        dto.setFilePath(fileShare.getAttachment().getFilePath());
        dto.setFileSize(fileShare.getAttachment().getFileSize());
        dto.setSharedAt(fileShare.getSharedAt());
        dto.setNote(null); // Không có note
        dto.setActive(fileShare.isActive());
        
        // Map user information
        if (fileShare.getSharedBy() != null) {
            UserDTO sharedByDto = new UserDTO();
            sharedByDto.setId(fileShare.getSharedBy().getId());
            sharedByDto.setName(fileShare.getSharedBy().getName());
            sharedByDto.setEmail(fileShare.getSharedBy().getEmail());
            dto.setSharedBy(sharedByDto);
        }
        
        if (fileShare.getSharedWith() != null) {
            UserDTO sharedWithDto = new UserDTO();
            sharedWithDto.setId(fileShare.getSharedWith().getId());
            sharedWithDto.setName(fileShare.getSharedWith().getName());
            sharedWithDto.setEmail(fileShare.getSharedWith().getEmail());
            dto.setSharedWith(sharedWithDto);
        }
        
        return dto;
    }
    
    @Override
    @Transactional
    public String shareFileWithUsers(Integer attachmentId, List<Integer> userIds) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        Attachment attachment = attachmentRepository.findByIdAndIsDeletedFalse(attachmentId);
        if (attachment == null) {
            throw new RuntimeException("Không tìm thấy file đính kèm.");
        }
        
        // Chỉ owner mới có thể chia sẻ file
        if (!attachment.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền chia sẻ file này.");
        }
        
        List<String> successUsers = new ArrayList<>();
        List<String> failedUsers = new ArrayList<>();
        
        for (Integer userId : userIds) {
            try {
                // Tìm user theo ID
                Optional<User> userOpt = userRepository.findById(userId);
                if (!userOpt.isPresent()) {
                    failedUsers.add("User ID " + userId + " (không tìm thấy user)");
                    continue;
                }
                
                User targetUser = userOpt.get();
                
                // Không thể chia sẻ cho chính mình
                if (targetUser.getId().equals(currentUser.getId())) {
                    failedUsers.add(targetUser.getEmail() + " (không thể chia sẻ cho chính mình)");
                    continue;
                }
                
                // Kiểm tra xem đã có active share chưa
                Optional<FileShare> activeShare = fileShareRepository
                    .findByAttachmentAndSharedWithAndIsActiveTrue(attachment, targetUser);
                
                if (activeShare.isPresent()) {
                    failedUsers.add(targetUser.getEmail() + " (đã được chia sẻ)");
                    continue;
                }
                
                // Kiểm tra xem có inactive share không (để reactivate)
                Optional<FileShare> inactiveShare = fileShareRepository
                    .findByAttachmentAndSharedWith(attachment, targetUser);
                
                if (inactiveShare.isPresent()) {
                    // Reactivate record cũ
                    FileShare existingShare = inactiveShare.get();
                    existingShare.setActive(true);
                    existingShare.setSharedBy(currentUser); // Update shared_by nếu cần
                    fileShareRepository.save(existingShare);
                    successUsers.add(targetUser.getEmail() + " (reactivated)");
                    
                    logger.info("File share reactivated: User {} reactivated share of attachment {} with user ID {}", 
                        currentUser.getEmail(), attachmentId, userId);
                } else {
                    // Tạo file share mới
                    FileShare newShare = new FileShare();
                    newShare.setAttachment(attachment);
                    newShare.setSharedBy(currentUser);
                    newShare.setSharedWith(targetUser);
                    newShare.setActive(true);
                    
                    fileShareRepository.save(newShare);
                    successUsers.add(targetUser.getEmail());
                    
                    logger.info("File shared: User {} shared attachment {} with user ID {}", 
                        currentUser.getEmail(), attachmentId, userId);
                }
                
            } catch (Exception e) {
                logger.error("Error sharing file with user ID {}: {}", userId, e.getMessage());
                failedUsers.add("User ID " + userId + " (lỗi hệ thống)");
            }
        }
        
        // Tạo thông báo kết quả
        StringBuilder result = new StringBuilder();
        if (!successUsers.isEmpty()) {
            result.append("Chia sẻ thành công với ").append(successUsers.size()).append(" user: ")
                  .append(String.join(", ", successUsers));
        }
        
        if (!failedUsers.isEmpty()) {
            if (result.length() > 0) {
                result.append(". ");
            }
            result.append("Chia sẻ thất bại với ").append(failedUsers.size()).append(" user: ")
                  .append(String.join(", ", failedUsers));
        }
        
        return result.toString();
    }
    
    @Override
    public List<FileShareDTO> getSharedWithMe() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        // Đơn giản hóa - không cần kiểm tra expires_at
        List<FileShare> shares = fileShareRepository.findBySharedWithAndIsActiveTrueOrderBySharedAtDesc(currentUser);
        return shares.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<FileShareDTO> getMySharedFiles() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        List<FileShare> shares = fileShareRepository.findBySharedByAndIsActiveTrueOrderBySharedAtDesc(currentUser);
        return shares.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    public List<FileShareDTO> getFileSharesByAttachment(Integer attachmentId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        Attachment attachment = attachmentRepository.findByIdAndIsDeletedFalse(attachmentId);
        if (attachment == null) {
            throw new RuntimeException("Không tìm thấy file đính kèm.");
        }
        
        // Chỉ owner mới có thể xem danh sách người được chia sẻ
        if (!attachment.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền xem danh sách chia sẻ của file này.");
        }
        
        List<FileShare> shares = fileShareRepository.findByAttachmentAndIsActiveTrueOrderBySharedAtDesc(attachment);
        return shares.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public String bulkRevokeAllShares(Integer attachmentId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        Attachment attachment = attachmentRepository.findByIdAndIsDeletedFalse(attachmentId);
        if (attachment == null) {
            throw new RuntimeException("Không tìm thấy file đính kèm.");
        }
        
        // Chỉ owner mới có thể hủy toàn bộ chia sẻ
        if (!attachment.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền hủy chia sẻ của file này.");
        }
        
        // Lấy tất cả active shares của file này
        List<FileShare> activeShares = fileShareRepository.findByAttachmentAndIsActiveTrueOrderBySharedAtDesc(attachment);
        
        if (activeShares.isEmpty()) {
            return "Không có chia sẻ nào để hủy";
        }
        
        // Hủy tất cả shares
        int revokedCount = 0;
        for (FileShare share : activeShares) {
            share.setActive(false);
            fileShareRepository.save(share);
            revokedCount++;
        }
        
        logger.info("Bulk revoked all shares: User {} revoked {} shares for attachment {}", 
            currentUser.getEmail(), revokedCount, attachmentId);
        
        return "Đã hủy thành công " + revokedCount + " chia sẻ";
    }
    
    @Override
    @Transactional
    public String bulkRevokeMultipleUsers(Integer attachmentId, List<Integer> userIds) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        Attachment attachment = attachmentRepository.findByIdAndIsDeletedFalse(attachmentId);
        if (attachment == null) {
            throw new RuntimeException("Không tìm thấy file đính kèm.");
        }
        
        // Chỉ owner mới có thể hủy chia sẻ
        if (!attachment.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền hủy chia sẻ của file này.");
        }
        
        List<String> successUsers = new ArrayList<>();
        List<String> failedUsers = new ArrayList<>();
        
        for (Integer userId : userIds) {
            try {
                // Tìm user theo ID
                Optional<User> userOpt = userRepository.findById(userId);
                if (!userOpt.isPresent()) {
                    failedUsers.add("User ID " + userId + " (không tìm thấy user)");
                    continue;
                }
                
                User targetUser = userOpt.get();
                
                // Tìm active share giữa attachment và user này
                Optional<FileShare> shareOpt = fileShareRepository
                    .findByAttachmentAndSharedWithAndIsActiveTrue(attachment, targetUser);
                
                if (!shareOpt.isPresent()) {
                    failedUsers.add(targetUser.getEmail() + " (không có chia sẻ active)");
                    continue;
                }
                
                // Hủy chia sẻ
                FileShare share = shareOpt.get();
                share.setActive(false);
                fileShareRepository.save(share);
                successUsers.add(targetUser.getEmail());
                
                logger.info("Bulk revoked share: User {} revoked share with user {} for attachment {}", 
                    currentUser.getEmail(), targetUser.getEmail(), attachmentId);
                    
            } catch (Exception e) {
                logger.error("Error revoking share for user ID {}: {}", userId, e.getMessage());
                failedUsers.add("User ID " + userId + " (lỗi hệ thống)");
            }
        }
        
        // Tạo thông báo kết quả
        StringBuilder result = new StringBuilder();
        if (!successUsers.isEmpty()) {
            result.append("Hủy chia sẻ thành công với ").append(successUsers.size()).append(" user: ")
                  .append(String.join(", ", successUsers));
        }
        
        if (!failedUsers.isEmpty()) {
            if (result.length() > 0) {
                result.append(". ");
            }
            result.append("Hủy chia sẻ thất bại với ").append(failedUsers.size()).append(" user: ")
                  .append(String.join(", ", failedUsers));
        }
        
        return result.toString();
    }
    
    @Override
    public FileShareDTO checkFileAccess(Integer attachmentId, Integer userId) {
        Attachment attachment = attachmentRepository.findByIdAndIsDeletedFalse(attachmentId);
        if (attachment == null) {
            return null;
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        
        // Kiểm tra owner - owner có thể chỉnh sửa file
        if (attachment.getUploadedBy().getId().equals(userId)) {
            // Owner có full access - tạo DTO giả để represent owner access
            FileShareDTO ownerAccess = new FileShareDTO();
            ownerAccess.setAttachmentId(attachmentId);
            ownerAccess.setActive(true);
            return ownerAccess;
        }
        
        // Kiểm tra shared access - shared users chỉ có quyền read
        Optional<FileShare> share = fileShareRepository
            .findByAttachmentAndSharedWithAndIsActiveTrue(attachment, user);
        
        return share.map(this::toDTO).orElse(null);
    }
    
    @Override
    public boolean hasWritePermission(Integer attachmentId, Integer userId) {
        Attachment attachment = attachmentRepository.findByIdAndIsDeletedFalse(attachmentId);
        if (attachment == null) {
            return false;
        }
        
        // Chỉ owner mới có quyền write, shared users luôn read-only
        return attachment.getUploadedBy().getId().equals(userId);
    }
}