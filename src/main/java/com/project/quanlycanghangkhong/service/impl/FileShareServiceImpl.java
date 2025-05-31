package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.FileShareDTO;
import com.project.quanlycanghangkhong.dto.UserDTO;
import com.project.quanlycanghangkhong.dto.request.ShareFileRequest;
import com.project.quanlycanghangkhong.dto.request.UpdateFileShareRequest;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * Chuyển đổi FileShare entity sang DTO
     */
    private FileShareDTO toDTO(FileShare fileShare) {
        FileShareDTO dto = new FileShareDTO();
        dto.setId(fileShare.getId());
        dto.setAttachmentId(fileShare.getAttachment().getId());
        dto.setFileName(fileShare.getAttachment().getFileName());
        dto.setFilePath(fileShare.getAttachment().getFilePath());
        dto.setFileSize(fileShare.getAttachment().getFileSize());
        dto.setPermission(fileShare.getPermission());
        dto.setSharedAt(fileShare.getSharedAt());
        dto.setExpiresAt(fileShare.getExpiresAt());
        dto.setNote(fileShare.getNote());
        dto.setActive(fileShare.isActive());
        dto.setExpired(fileShare.isExpired());
        
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
    public List<FileShareDTO> shareFile(ShareFileRequest request) {
        // Hỗ trợ batch sharing - chia sẻ nhiều file cùng lúc
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            return shareFileInternal(request.getAttachmentIds(), request);
        }
        
        // Backward compatibility - nếu dùng attachmentId cũ
        if (request.getAttachmentId() != null) {
            request.setAttachmentIds(List.of(request.getAttachmentId()));
            return shareFileInternal(request.getAttachmentIds(), request);
        }
        
        throw new RuntimeException("Vui lòng cung cấp danh sách file cần chia sẻ.");
    }

    private List<FileShareDTO> shareFileInternal(List<Integer> attachmentIds, ShareFileRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }

        if (attachmentIds == null || attachmentIds.isEmpty()) {
            throw new RuntimeException("Danh sách file không được để trống.");
        }

        if (request.getSharedWithUserIds() == null || request.getSharedWithUserIds().isEmpty()) {
            throw new RuntimeException("Danh sách user được chia sẻ không được để trống.");
        }

        List<FileShareDTO> allResults = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<String> successMessages = new ArrayList<>();

        // Xử lý từng file
        for (Integer attachmentId : attachmentIds) {
            try {
                // Tìm attachment
                Attachment attachment = attachmentRepository.findByIdAndIsDeletedFalse(attachmentId);
                if (attachment == null) {
                    errors.add("File ID " + attachmentId + ": Không tìm thấy file đính kèm");
                    continue;
                }

                // Kiểm tra quyền ownership
                if (!attachment.getUploadedBy().getId().equals(currentUser.getId())) {
                    errors.add("File ID " + attachmentId + ": Bạn không có quyền chia sẻ file '" + attachment.getFileName() + "'");
                    continue;
                }

                List<FileShareDTO> fileResults = new ArrayList<>();
                List<String> fileErrors = new ArrayList<>();

                // Chia sẻ file này cho từng user
                for (Integer userId : request.getSharedWithUserIds()) {
                    try {
                        // Tìm user được chia sẻ
                        User sharedWithUser = userRepository.findById(userId).orElse(null);
                        if (sharedWithUser == null) {
                            fileErrors.add("User ID " + userId + ": Không tìm thấy user");
                            continue;
                        }

                        // Kiểm tra không chia sẻ cho chính mình
                        if (sharedWithUser.getId().equals(currentUser.getId())) {
                            fileErrors.add("User ID " + userId + ": Không thể chia sẻ file cho chính mình");
                            continue;
                        }

                        // Kiểm tra đã chia sẻ cho user này chưa
                        Optional<FileShare> existingShare = fileShareRepository
                            .findByAttachmentAndSharedWithAndIsActiveTrue(attachment, sharedWithUser);

                        if (existingShare.isPresent()) {
                            // Cập nhật share hiện có
                            FileShare share = existingShare.get();
                            share.setPermission(request.getPermission());
                            share.setExpiresAt(request.getExpiresAt());
                            share.setNote(request.getNote());
                            share = fileShareRepository.save(share);
                            fileResults.add(toDTO(share));

                            logger.info("Updated file share: User {} updated share for attachment {} with user {}", 
                                currentUser.getEmail(), attachment.getId(), sharedWithUser.getEmail());
                        } else {
                            // Tạo share mới
                            FileShare newShare = new FileShare(attachment, currentUser, sharedWithUser, request.getPermission());
                            newShare.setExpiresAt(request.getExpiresAt());
                            newShare.setNote(request.getNote());
                            newShare = fileShareRepository.save(newShare);
                            fileResults.add(toDTO(newShare));

                            logger.info("Created file share: User {} shared attachment {} with user {}", 
                                currentUser.getEmail(), attachment.getId(), sharedWithUser.getEmail());
                        }

                    } catch (Exception e) {
                        logger.error("Error sharing file {} with user ID: {}", attachmentId, userId, e);
                        fileErrors.add("User ID " + userId + ": " + e.getMessage());
                    }
                }

                // Thêm kết quả của file này vào tổng kết
                allResults.addAll(fileResults);
                
                if (!fileResults.isEmpty()) {
                    successMessages.add("File '" + attachment.getFileName() + "': Chia sẻ thành công cho " + fileResults.size() + " user(s)");
                }
                
                if (!fileErrors.isEmpty()) {
                    errors.addAll(fileErrors.stream()
                        .map(error -> "File '" + attachment.getFileName() + "' - " + error)
                        .collect(Collectors.toList()));
                }

            } catch (Exception e) {
                logger.error("Error processing file ID: " + attachmentId, e);
                errors.add("File ID " + attachmentId + ": " + e.getMessage());
            }
        }

        // Log kết quả
        if (!successMessages.isEmpty()) {
            logger.info("Batch file sharing completed successfully: {}", String.join("; ", successMessages));
        }
        
        if (!errors.isEmpty()) {
            logger.warn("Some files/users failed in batch sharing: {}", String.join("; ", errors));
        }

        // Nếu không có file nào được chia sẻ thành công
        if (allResults.isEmpty()) {
            throw new RuntimeException("Không thể chia sẻ bất kỳ file nào: " + String.join("; ", errors));
        }

        return allResults;
    }
    
    @Override
    public List<FileShareDTO> getSharedWithMe() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        List<FileShare> shares = fileShareRepository.findActiveSharesForUser(currentUser, LocalDateTime.now());
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
    public List<FileShareDTO> updateFileShare(Integer shareId, UpdateFileShareRequest request) {
        // Nếu là batch update, chuyển sang updateFileShareBatch
        if (!request.isSingleUpdate()) {
            return updateFileShareBatch(request);
        }
        
        // Single update logic (backward compatibility)
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        FileShare share = fileShareRepository.findById(shareId).orElse(null);
        if (share == null || !share.isActive()) {
            throw new RuntimeException("Không tìm thấy chia sẻ file.");
        }
        
        // Chỉ người chia sẻ mới có thể cập nhật
        if (!share.getSharedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật chia sẻ này.");
        }
        
        share.setPermission(request.getPermission());
        if (request.getExpiresAt() != null) {
            share.setExpiresAt(request.getExpiresAt());
        }
        if (request.getNote() != null) {
            share.setNote(request.getNote());
        }
        
        share = fileShareRepository.save(share);
        
        logger.info("Updated file share: User {} updated share {} for attachment {}", 
            currentUser.getEmail(), shareId, share.getAttachment().getId());
        
        return List.of(toDTO(share));
    }
    
    @Override
    @Transactional
    public List<FileShareDTO> updateFileShareBatch(UpdateFileShareRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        List<FileShareDTO> results = new ArrayList<>();
        
        // Xử lý theo từng update mode
        switch (request.getUpdateMode()) {
            case ADD_USERS:
                if (request.isAddUsersUpdate()) {
                    results.addAll(addUsersToFileShares(
                        request.getAttachmentIds(), 
                        request.getAddUserIds(),
                        request.getPermission(),
                        request.getExpiresAt(),
                        request.getNote()
                    ));
                }
                break;
                
            case REMOVE_USERS:
                if (request.isRemoveUsersUpdate()) {
                    int removed = removeUsersFromFileShares(
                        request.getAttachmentIds(), 
                        request.getRemoveUserIds()
                    );
                    logger.info("Removed {} user shares", removed);
                }
                break;
                
            case ADD_FILES:
                if (request.isAddFilesUpdate()) {
                    results.addAll(addFilesToUserShares(
                        request.getAddAttachmentIds(),
                        request.getCurrentSharedWithUserIds(),
                        request.getPermission(),
                        request.getExpiresAt(),
                        request.getNote()
                    ));
                }
                break;
                
            case REMOVE_FILES:
                if (request.isRemoveFilesUpdate()) {
                    int removed = removeFilesFromUserShares(
                        request.getRemoveAttachmentIds(),
                        request.getCurrentSharedWithUserIds()
                    );
                    logger.info("Removed {} file shares", removed);
                }
                break;
                
            case BATCH:
                if (request.isBatchUpdate()) {
                    // Update existing shares for attachments
                    for (Integer attachmentId : request.getAttachmentIds()) {
                        List<FileShare> shares = fileShareRepository
                            .findByAttachmentIdAndSharedByAndIsActiveTrue(attachmentId, currentUser);
                        
                        for (FileShare share : shares) {
                            share.setPermission(request.getPermission());
                            if (request.getExpiresAt() != null) {
                                share.setExpiresAt(request.getExpiresAt());
                            }
                            if (request.getNote() != null) {
                                share.setNote(request.getNote());
                            }
                        }
                        
                        List<FileShare> updated = fileShareRepository.saveAll(shares);
                        results.addAll(updated.stream().map(this::toDTO).collect(Collectors.toList()));
                    }
                }
                break;
                
            case FULL_REPLACE:
                if (request.isFullReplaceUpdate()) {
                    // Deactivate existing shares
                    for (Integer attachmentId : request.getAttachmentIds()) {
                        List<FileShare> existingShares = fileShareRepository
                            .findByAttachmentIdAndSharedByAndIsActiveTrue(attachmentId, currentUser);
                        
                        for (FileShare share : existingShares) {
                            share.setActive(false);
                        }
                        fileShareRepository.saveAll(existingShares);
                    }
                    
                    // Create new shares
                    ShareFileRequest newShareRequest = new ShareFileRequest();
                    newShareRequest.setAttachmentIds(request.getAttachmentIds());
                    newShareRequest.setSharedWithUserIds(request.getCurrentSharedWithUserIds());
                    newShareRequest.setPermission(request.getPermission());
                    newShareRequest.setExpiresAt(request.getExpiresAt());
                    newShareRequest.setNote(request.getNote());
                    
                    results.addAll(shareFileInternal(request.getAttachmentIds(), newShareRequest));
                }
                break;
                
            default:
                throw new RuntimeException("Update mode không được hỗ trợ: " + request.getUpdateMode());
        }
        
        return results;
    }
    
    @Override
    @Transactional
    public List<FileShareDTO> addUsersToFileShares(List<Integer> attachmentIds, List<Integer> addUserIds,
                                                  SharePermission permission, LocalDateTime expiresAt, String note) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        List<FileShareDTO> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (Integer attachmentId : attachmentIds) {
            Attachment attachment = attachmentRepository.findByIdAndIsDeletedFalse(attachmentId);
            if (attachment == null) {
                errors.add("File ID " + attachmentId + ": Không tìm thấy file");
                continue;
            }
            
            // Kiểm tra quyền ownership
            if (!attachment.getUploadedBy().getId().equals(currentUser.getId())) {
                errors.add("File ID " + attachmentId + ": Không có quyền chia sẻ file này");
                continue;
            }
            
            for (Integer userId : addUserIds) {
                User sharedWithUser = userRepository.findById(userId).orElse(null);
                if (sharedWithUser == null) {
                    errors.add("User ID " + userId + ": Không tìm thấy user");
                    continue;
                }
                
                // Kiểm tra đã share chưa
                Optional<FileShare> existingShare = fileShareRepository
                    .findByAttachmentAndSharedWithAndIsActiveTrue(attachment, sharedWithUser);
                
                if (existingShare.isPresent()) {
                    // Update existing share
                    FileShare share = existingShare.get();
                    share.setPermission(permission);
                    share.setExpiresAt(expiresAt);
                    share.setNote(note);
                    share = fileShareRepository.save(share);
                    results.add(toDTO(share));
                } else {
                    // Create new share
                    FileShare newShare = new FileShare(attachment, currentUser, sharedWithUser, permission);
                    newShare.setExpiresAt(expiresAt);
                    newShare.setNote(note);
                    newShare = fileShareRepository.save(newShare);
                    results.add(toDTO(newShare));
                }
            }
        }
        
        if (!errors.isEmpty()) {
            logger.warn("Some errors in addUsersToFileShares: {}", String.join("; ", errors));
        }
        
        return results;
    }
    
    @Override
    @Transactional
    public int removeUsersFromFileShares(List<Integer> attachmentIds, List<Integer> removeUserIds) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        int removedCount = 0;
        
        for (Integer attachmentId : attachmentIds) {
            Attachment attachment = attachmentRepository.findByIdAndIsDeletedFalse(attachmentId);
            if (attachment == null || !attachment.getUploadedBy().getId().equals(currentUser.getId())) {
                continue;
            }
            
            for (Integer userId : removeUserIds) {
                User sharedWithUser = userRepository.findById(userId).orElse(null);
                if (sharedWithUser == null) continue;
                
                Optional<FileShare> existingShare = fileShareRepository
                    .findByAttachmentAndSharedWithAndIsActiveTrue(attachment, sharedWithUser);
                
                if (existingShare.isPresent()) {
                    FileShare share = existingShare.get();
                    share.setActive(false);
                    fileShareRepository.save(share);
                    removedCount++;
                }
            }
        }
        
        return removedCount;
    }
    
    @Override
    @Transactional
    public List<FileShareDTO> addFilesToUserShares(List<Integer> addAttachmentIds, List<Integer> sharedWithUserIds,
                                                  SharePermission permission, LocalDateTime expiresAt, String note) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        List<FileShareDTO> results = new ArrayList<>();
        
        for (Integer attachmentId : addAttachmentIds) {
            Attachment attachment = attachmentRepository.findByIdAndIsDeletedFalse(attachmentId);
            if (attachment == null || !attachment.getUploadedBy().getId().equals(currentUser.getId())) {
                continue;
            }
            
            for (Integer userId : sharedWithUserIds) {
                User sharedWithUser = userRepository.findById(userId).orElse(null);
                if (sharedWithUser == null) continue;
                
                // Kiểm tra đã share chưa
                Optional<FileShare> existingShare = fileShareRepository
                    .findByAttachmentAndSharedWithAndIsActiveTrue(attachment, sharedWithUser);
                
                if (!existingShare.isPresent()) {
                    // Create new share
                    FileShare newShare = new FileShare(attachment, currentUser, sharedWithUser, permission);
                    newShare.setExpiresAt(expiresAt);
                    newShare.setNote(note);
                    newShare = fileShareRepository.save(newShare);
                    results.add(toDTO(newShare));
                }
            }
        }
        
        return results;
    }
    
    @Override
    @Transactional
    public int removeFilesFromUserShares(List<Integer> removeAttachmentIds, List<Integer> sharedWithUserIds) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        int removedCount = 0;
        
        for (Integer attachmentId : removeAttachmentIds) {
            Attachment attachment = attachmentRepository.findByIdAndIsDeletedFalse(attachmentId);
            if (attachment == null || !attachment.getUploadedBy().getId().equals(currentUser.getId())) {
                continue;
            }
            
            for (Integer userId : sharedWithUserIds) {
                User sharedWithUser = userRepository.findById(userId).orElse(null);
                if (sharedWithUser == null) continue;
                
                Optional<FileShare> existingShare = fileShareRepository
                    .findByAttachmentAndSharedWithAndIsActiveTrue(attachment, sharedWithUser);
                
                if (existingShare.isPresent()) {
                    FileShare share = existingShare.get();
                    share.setActive(false);
                    fileShareRepository.save(share);
                    removedCount++;
                }
            }
        }
        
        return removedCount;
    }
    
    @Override
    @Transactional
    public int revokeFileShareBatch(List<Integer> shareIds) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        int revokedCount = 0;
        
        for (Integer shareId : shareIds) {
            FileShare share = fileShareRepository.findById(shareId).orElse(null);
            if (share != null && share.getSharedBy().getId().equals(currentUser.getId())) {
                share.setActive(false);
                fileShareRepository.save(share);
                revokedCount++;
            }
        }
        
        return revokedCount;
    }
    
    @Override
    @Transactional
    public void revokeFileShare(Integer shareId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
        }
        
        FileShare share = fileShareRepository.findById(shareId).orElse(null);
        if (share == null) {
            throw new RuntimeException("Không tìm thấy chia sẻ file.");
        }
        
        // Chỉ người chia sẻ mới có thể hủy
        if (!share.getSharedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền hủy chia sẻ này.");
        }
        
        share.setActive(false);
        fileShareRepository.save(share);
        
        logger.info("Revoked file share: User {} revoked share {} for attachment {}", 
            currentUser.getEmail(), shareId, share.getAttachment().getId());
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
        
        // Kiểm tra owner
        if (attachment.getUploadedBy().getId().equals(userId)) {
            // Owner có full access - tạo DTO giả để represent owner access
            FileShareDTO ownerAccess = new FileShareDTO();
            ownerAccess.setAttachmentId(attachmentId);
            ownerAccess.setPermission(SharePermission.READ_WRITE);
            ownerAccess.setActive(true);
            ownerAccess.setExpired(false);
            return ownerAccess;
        }
        
        // Kiểm tra shared access
        Optional<FileShare> share = fileShareRepository
            .findValidShareForUserAndAttachment(attachment, user, LocalDateTime.now());
        
        return share.map(this::toDTO).orElse(null);
    }
    
    @Override
    public boolean hasWritePermission(Integer attachmentId, Integer userId) {
        FileShareDTO access = checkFileAccess(attachmentId, userId);
        return access != null && access.getPermission() == SharePermission.READ_WRITE;
    }
    
    @Override
    @Transactional
    public void cleanupExpiredShares() {
        List<FileShare> expiredShares = fileShareRepository.findExpiredShares(LocalDateTime.now());
        
        for (FileShare share : expiredShares) {
            share.setActive(false);
        }
        
        if (!expiredShares.isEmpty()) {
            fileShareRepository.saveAll(expiredShares);
            logger.info("Cleaned up {} expired file shares", expiredShares.size());
        }
    }
}