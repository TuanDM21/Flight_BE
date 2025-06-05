package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.FileShareDTO;
import java.util.List;

public interface FileShareService {
    
    /**
     * Chia sẻ file với các user khác (đơn giản - chỉ READ_ONLY)
     * @param attachmentId ID của file cần chia sẻ
     * @param userIds Danh sách ID của user được chia sẻ
     * @return Thông báo kết quả
     */
    String shareFileWithUsers(Integer attachmentId, List<Integer> userIds);
    
    /**
     * Lấy danh sách file được chia sẻ với user hiện tại
     * @return Danh sách file được chia sẻ
     */
    List<FileShareDTO> getSharedWithMe();
    
    /**
     * Lấy danh sách file mà user hiện tại đã chia sẻ cho người khác
     * @return Danh sách file đã chia sẻ
     */
    List<FileShareDTO> getMySharedFiles();
    
    /**
     * Lấy danh sách user được chia sẻ một file cụ thể
     * @param attachmentId ID của file
     * @return Danh sách file share
     */
    List<FileShareDTO> getFileSharesByAttachment(Integer attachmentId);
    
    /**
     * Hủy toàn bộ chia sẻ của một file (bulk revoke all shares)
     * @param attachmentId ID của file cần hủy toàn bộ chia sẻ
     * @return Thông báo kết quả
     */
    String bulkRevokeAllShares(Integer attachmentId);
    
    /**
     * Hủy chia sẻ với nhiều user cùng lúc (bulk revoke multiple users)
     * @param attachmentId ID của file cần hủy chia sẻ
     * @param userIds Danh sách ID của user cần hủy chia sẻ
     * @return Thông báo kết quả
     */
    String bulkRevokeMultipleUsers(Integer attachmentId, List<Integer> userIds);
    
    /**
     * Kiểm tra user có quyền truy cập file không
     * @param attachmentId ID của file
     * @param userId ID của user
     * @return File share nếu có quyền, null nếu không
     */
    FileShareDTO checkFileAccess(Integer attachmentId, Integer userId);
    
    /**
     * Kiểm tra user có quyền chỉnh sửa file không (luôn false cho shared files)
     * @param attachmentId ID của file
     * @param userId ID của user
     * @return true nếu có quyền write, false nếu không
     */
    boolean hasWritePermission(Integer attachmentId, Integer userId);
}