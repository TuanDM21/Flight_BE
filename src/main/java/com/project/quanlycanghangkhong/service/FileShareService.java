package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.FileShareDTO;
import com.project.quanlycanghangkhong.dto.request.ShareFileRequest;
import com.project.quanlycanghangkhong.dto.request.UpdateFileShareRequest;
import java.util.List;

public interface FileShareService {
    
    /**
     * Chia sẻ file cho user khác (hỗ trợ multiple files)
     * @param request Thông tin chia sẻ file - có thể chứa nhiều attachmentIds
     * @return Danh sách file share đã tạo cho tất cả files
     */
    List<FileShareDTO> shareFile(ShareFileRequest request);
    
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
     * Cập nhật quyền chia sẻ file (hỗ trợ batch operations)
     * @param shareId ID của file share (cho single update)
     * @param request Thông tin cập nhật với các batch options
     * @return Danh sách file share đã cập nhật
     */
    List<FileShareDTO> updateFileShare(Integer shareId, UpdateFileShareRequest request);
    
    /**
     * Cập nhật quyền chia sẻ file với batch support
     * @param request Thông tin cập nhật với đầy đủ batch operations
     * @return Danh sách file share đã cập nhật
     */
    List<FileShareDTO> updateFileShareBatch(UpdateFileShareRequest request);
    
    /**
     * Thêm user vào existing file shares
     * @param attachmentIds Danh sách file đã được share
     * @param addUserIds Danh sách user cần thêm
     * @param permission Quyền cho user mới
     * @param expiresAt Thời gian hết hạn
     * @param note Ghi chú
     * @return Danh sách file share mới tạo
     */
    List<FileShareDTO> addUsersToFileShares(List<Integer> attachmentIds, List<Integer> addUserIds, 
                                          com.project.quanlycanghangkhong.model.SharePermission permission,
                                          java.time.LocalDateTime expiresAt, String note);
    
    /**
     * Xóa user khỏi file shares
     * @param attachmentIds Danh sách file
     * @param removeUserIds Danh sách user cần xóa
     * @return Số lượng shares đã xóa
     */
    int removeUsersFromFileShares(List<Integer> attachmentIds, List<Integer> removeUserIds);
    
    /**
     * Thêm file vào existing user group shares
     * @param addAttachmentIds Danh sách file mới cần thêm
     * @param sharedWithUserIds Danh sách user đã có shares
     * @param permission Quyền cho file mới
     * @param expiresAt Thời gian hết hạn
     * @param note Ghi chú
     * @return Danh sách file share mới tạo
     */
    List<FileShareDTO> addFilesToUserShares(List<Integer> addAttachmentIds, List<Integer> sharedWithUserIds,
                                          com.project.quanlycanghangkhong.model.SharePermission permission,
                                          java.time.LocalDateTime expiresAt, String note);
    
    /**
     * Xóa file khỏi user shares
     * @param removeAttachmentIds Danh sách file cần xóa
     * @param sharedWithUserIds Danh sách user
     * @return Số lượng shares đã xóa
     */
    int removeFilesFromUserShares(List<Integer> removeAttachmentIds, List<Integer> sharedWithUserIds);
    
    /**
     * Hủy chia sẻ file
     * @param shareId ID của file share
     */
    void revokeFileShare(Integer shareId);
    
    /**
     * Hủy chia sẻ batch
     * @param shareIds Danh sách ID của file shares
     * @return Số lượng shares đã hủy
     */
    int revokeFileShareBatch(List<Integer> shareIds);
    
    /**
     * Kiểm tra user có quyền truy cập file không
     * @param attachmentId ID của file
     * @param userId ID của user
     * @return File share nếu có quyền, null nếu không
     */
    FileShareDTO checkFileAccess(Integer attachmentId, Integer userId);
    
    /**
     * Kiểm tra user có quyền chỉnh sửa file không
     * @param attachmentId ID của file
     * @param userId ID của user
     * @return true nếu có quyền chỉnh sửa
     */
    boolean hasWritePermission(Integer attachmentId, Integer userId);
    
    /**
     * Cleanup các file share đã hết hạn
     */
    void cleanupExpiredShares();
}