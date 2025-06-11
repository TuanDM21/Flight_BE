package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.FileShare;
import com.project.quanlycanghangkhong.model.Attachment;
import com.project.quanlycanghangkhong.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileShareRepository extends JpaRepository<FileShare, Integer> {
    
    // Tìm file share cụ thể giữa attachment và user
    Optional<FileShare> findByAttachmentAndSharedWithAndIsActiveTrue(Attachment attachment, User sharedWith);
    
    // Tìm file share bất kể trạng thái active (để reactivate)
    Optional<FileShare> findByAttachmentAndSharedWith(Attachment attachment, User sharedWith);
    
    // Lấy tất cả file được chia sẻ với user cụ thể (đơn giản hóa - không cần kiểm tra expires_at)
    List<FileShare> findBySharedWithAndIsActiveTrueOrderBySharedAtDesc(User sharedWith);
    
    // Lấy tất cả file mà user đã chia sẻ cho người khác
    List<FileShare> findBySharedByAndIsActiveTrueOrderBySharedAtDesc(User sharedBy);
    
    // Lấy tất cả share của một attachment cụ thể (chỉ active)
    List<FileShare> findByAttachmentAndIsActiveTrueOrderBySharedAtDesc(Attachment attachment);
    
    // 🔥 NEW: Lấy TẤT CẢ share của một attachment (bao gồm cả active và inactive)
    List<FileShare> findByAttachment(Attachment attachment);
    
    // Kiểm tra user có đang chia sẻ file cho ai đó không
    boolean existsByAttachmentAndSharedWithAndIsActiveTrue(Attachment attachment, User sharedWith);
    
    // 🔥 NEW: Đếm số lượng người được chia sẻ của một attachment (chỉ active shares)
    int countByAttachmentAndIsActiveTrue(Attachment attachment);
}