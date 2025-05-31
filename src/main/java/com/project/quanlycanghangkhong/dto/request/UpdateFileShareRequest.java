package com.project.quanlycanghangkhong.dto.request;

import com.project.quanlycanghangkhong.model.SharePermission;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Request để cập nhật quyền chia sẻ file với hỗ trợ batch operations")
public class UpdateFileShareRequest {
    
    @Schema(description = "ID của FileShare cụ thể cần update (dùng cho single update)", example = "123")
    private Integer shareId;
    
    @Schema(description = "Danh sách ID của các file cần update sharing (dùng cho batch update)", example = "[1, 2, 3]")
    private List<Integer> attachmentIds;
    
    @Schema(description = "Danh sách ID user hiện tại được chia sẻ (để add/remove)", example = "[4, 5, 6]")
    private List<Integer> currentSharedWithUserIds;
    
    @Schema(description = "Danh sách ID user MỚI cần ADD vào sharing", example = "[7, 8]")
    private List<Integer> addUserIds;
    
    @Schema(description = "Danh sách ID user cần REMOVE khỏi sharing", example = "[9, 10]")
    private List<Integer> removeUserIds;
    
    @Schema(description = "Danh sách ID file MỚI cần ADD vào sharing cho cùng user group", example = "[4, 5]")
    private List<Integer> addAttachmentIds;
    
    @Schema(description = "Danh sách ID file cần REMOVE khỏi sharing", example = "[6, 7]")
    private List<Integer> removeAttachmentIds;
    
    @NotNull(message = "Quyền truy cập không được để trống")
    @Schema(description = "Quyền truy cập file mới", required = true, allowableValues = {"READ_ONLY", "READ_WRITE"})
    private SharePermission permission;
    
    @Schema(description = "Thời gian hết hạn chia sẻ mới (để trống nếu không thay đổi)", example = "2025-12-31T23:59:59")
    private LocalDateTime expiresAt;
    
    @Schema(description = "Ghi chú mới về việc chia sẻ", example = "Cập nhật quyền chỉnh sửa")
    private String note;
    
    @Schema(description = "Chế độ cập nhật", allowableValues = {"SINGLE", "BATCH", "ADD_USERS", "REMOVE_USERS", "ADD_FILES", "REMOVE_FILES", "FULL_REPLACE"})
    private UpdateMode updateMode = UpdateMode.SINGLE;

    public enum UpdateMode {
        SINGLE,          // Update 1 share theo shareId
        BATCH,           // Update nhiều shares của cùng attachment
        ADD_USERS,       // Thêm user vào existing shares
        REMOVE_USERS,    // Xóa user khỏi shares
        ADD_FILES,       // Thêm file vào existing user group
        REMOVE_FILES,    // Xóa file khỏi shares
        FULL_REPLACE     // Replace toàn bộ sharing scope
    }

    // Constructors
    public UpdateFileShareRequest() {}

    // Convenience methods
    @Schema(hidden = true)
    public boolean isSingleUpdate() {
        return updateMode == UpdateMode.SINGLE && shareId != null;
    }
    
    @Schema(hidden = true)
    public boolean isBatchUpdate() {
        return updateMode == UpdateMode.BATCH && attachmentIds != null && !attachmentIds.isEmpty();
    }
    
    @Schema(hidden = true)
    public boolean isAddUsersUpdate() {
        return updateMode == UpdateMode.ADD_USERS && addUserIds != null && !addUserIds.isEmpty();
    }
    
    @Schema(hidden = true)
    public boolean isRemoveUsersUpdate() {
        return updateMode == UpdateMode.REMOVE_USERS && removeUserIds != null && !removeUserIds.isEmpty();
    }
    
    @Schema(hidden = true)
    public boolean isAddFilesUpdate() {
        return updateMode == UpdateMode.ADD_FILES && addAttachmentIds != null && !addAttachmentIds.isEmpty();
    }
    
    @Schema(hidden = true)
    public boolean isRemoveFilesUpdate() {
        return updateMode == UpdateMode.REMOVE_FILES && removeAttachmentIds != null && !removeAttachmentIds.isEmpty();
    }
    
    @Schema(hidden = true)
    public boolean isFullReplaceUpdate() {
        return updateMode == UpdateMode.FULL_REPLACE && 
               attachmentIds != null && !attachmentIds.isEmpty() &&
               currentSharedWithUserIds != null && !currentSharedWithUserIds.isEmpty();
    }

    // Getters and Setters
    public Integer getShareId() {
        return shareId;
    }

    public void setShareId(Integer shareId) {
        this.shareId = shareId;
    }

    public List<Integer> getAttachmentIds() {
        return attachmentIds;
    }

    public void setAttachmentIds(List<Integer> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    public List<Integer> getCurrentSharedWithUserIds() {
        return currentSharedWithUserIds;
    }

    public void setCurrentSharedWithUserIds(List<Integer> currentSharedWithUserIds) {
        this.currentSharedWithUserIds = currentSharedWithUserIds;
    }

    public List<Integer> getAddUserIds() {
        return addUserIds;
    }

    public void setAddUserIds(List<Integer> addUserIds) {
        this.addUserIds = addUserIds;
    }

    public List<Integer> getRemoveUserIds() {
        return removeUserIds;
    }

    public void setRemoveUserIds(List<Integer> removeUserIds) {
        this.removeUserIds = removeUserIds;
    }

    public List<Integer> getAddAttachmentIds() {
        return addAttachmentIds;
    }

    public void setAddAttachmentIds(List<Integer> addAttachmentIds) {
        this.addAttachmentIds = addAttachmentIds;
    }

    public List<Integer> getRemoveAttachmentIds() {
        return removeAttachmentIds;
    }

    public void setRemoveAttachmentIds(List<Integer> removeAttachmentIds) {
        this.removeAttachmentIds = removeAttachmentIds;
    }

    public SharePermission getPermission() {
        return permission;
    }

    public void setPermission(SharePermission permission) {
        this.permission = permission;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public UpdateMode getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(UpdateMode updateMode) {
        this.updateMode = updateMode;
    }
}