package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.FileShare;
import com.project.quanlycanghangkhong.model.Attachment;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.model.SharePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileShareRepository extends JpaRepository<FileShare, Integer> {
    
    // Tìm file share cụ thể giữa attachment và user
    Optional<FileShare> findByAttachmentAndSharedWithAndIsActiveTrue(Attachment attachment, User sharedWith);
    
    // Lấy tất cả file được chia sẻ với user cụ thể (còn active và chưa hết hạn)
    @Query("SELECT fs FROM FileShare fs WHERE fs.sharedWith = :user AND fs.isActive = true AND (fs.expiresAt IS NULL OR fs.expiresAt > :now)")
    List<FileShare> findActiveSharesForUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    // Lấy tất cả file mà user đã chia sẻ cho người khác
    List<FileShare> findBySharedByAndIsActiveTrueOrderBySharedAtDesc(User sharedBy);
    
    // Lấy tất cả share của một attachment cụ thể
    List<FileShare> findByAttachmentAndIsActiveTrueOrderBySharedAtDesc(Attachment attachment);
    
    // Kiểm tra xem user có quyền truy cập file không
    @Query("SELECT fs FROM FileShare fs WHERE fs.attachment = :attachment AND fs.sharedWith = :user AND fs.isActive = true AND (fs.expiresAt IS NULL OR fs.expiresAt > :now)")
    Optional<FileShare> findValidShareForUserAndAttachment(@Param("attachment") Attachment attachment, @Param("user") User user, @Param("now") LocalDateTime now);
    
    // Lấy file share với quyền cụ thể
    List<FileShare> findBySharedWithAndPermissionAndIsActiveTrueOrderBySharedAtDesc(User sharedWith, SharePermission permission);
    
    // Đếm số lượng user đã được chia sẻ một file cụ thể
    @Query("SELECT COUNT(fs) FROM FileShare fs WHERE fs.attachment = :attachment AND fs.isActive = true AND (fs.expiresAt IS NULL OR fs.expiresAt > :now)")
    long countActiveSharesForAttachment(@Param("attachment") Attachment attachment, @Param("now") LocalDateTime now);
    
    // Tìm tất cả share hết hạn để cleanup
    @Query("SELECT fs FROM FileShare fs WHERE fs.isActive = true AND fs.expiresAt IS NOT NULL AND fs.expiresAt <= :now")
    List<FileShare> findExpiredShares(@Param("now") LocalDateTime now);
    
    // Kiểm tra user có đang chia sẻ file cho ai đó không
    boolean existsByAttachmentAndSharedWithAndIsActiveTrue(Attachment attachment, User sharedWith);
    
    // ==================== BATCH OPERATIONS SUPPORT ====================
    
    // Tìm tất cả shares của attachment theo sharedBy user
    List<FileShare> findByAttachmentIdAndSharedByAndIsActiveTrue(Integer attachmentId, User sharedBy);
    
    // Tìm shares theo nhiều attachment IDs
    @Query("SELECT fs FROM FileShare fs WHERE fs.attachment.id IN :attachmentIds AND fs.sharedBy = :sharedBy AND fs.isActive = true")
    List<FileShare> findByAttachmentIdsAndSharedByAndIsActiveTrue(@Param("attachmentIds") List<Integer> attachmentIds, @Param("sharedBy") User sharedBy);
    
    // Tìm shares theo nhiều user IDs
    @Query("SELECT fs FROM FileShare fs WHERE fs.sharedWith.id IN :userIds AND fs.attachment = :attachment AND fs.isActive = true")
    List<FileShare> findByUserIdsAndAttachmentAndIsActiveTrue(@Param("userIds") List<Integer> userIds, @Param("attachment") Attachment attachment);
    
    // Batch deactivate shares
    @Query("UPDATE FileShare fs SET fs.isActive = false WHERE fs.id IN :shareIds AND fs.sharedBy = :sharedBy")
    int deactivateSharesByIds(@Param("shareIds") List<Integer> shareIds, @Param("sharedBy") User sharedBy);
    
    // Tìm shares để batch update
    @Query("SELECT fs FROM FileShare fs WHERE fs.attachment.id IN :attachmentIds AND fs.sharedWith.id IN :userIds AND fs.sharedBy = :sharedBy AND fs.isActive = true")
    List<FileShare> findForBatchUpdate(@Param("attachmentIds") List<Integer> attachmentIds, @Param("userIds") List<Integer> userIds, @Param("sharedBy") User sharedBy);
}