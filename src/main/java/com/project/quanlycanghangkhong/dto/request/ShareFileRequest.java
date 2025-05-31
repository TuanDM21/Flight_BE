package com.project.quanlycanghangkhong.dto.request;

import com.project.quanlycanghangkhong.model.SharePermission;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Request để chia sẻ file cho user khác")
public class ShareFileRequest {
    
    @NotEmpty(message = "Danh sách file không được để trống")
    @Schema(description = "Danh sách ID của các file cần chia sẻ", required = true, example = "[1, 2, 3]")
    private List<Integer> attachmentIds;
    
    @NotEmpty(message = "Danh sách user được chia sẻ không được để trống")
    @Schema(description = "Danh sách ID của các user được chia sẻ file", required = true)
    private List<Integer> sharedWithUserIds;
    
    @NotNull(message = "Quyền truy cập không được để trống")
    @Schema(description = "Quyền truy cập file", required = true, allowableValues = {"READ_ONLY", "READ_WRITE"})
    private SharePermission permission = SharePermission.READ_ONLY;
    
    @Schema(description = "Thời gian hết hạn chia sẻ (để trống nếu không có thời hạn)", example = "2025-12-31T23:59:59")
    private LocalDateTime expiresAt;
    
    @Schema(description = "Ghi chú về việc chia sẻ", example = "Chia sẻ để review tài liệu")
    private String note;

    // Constructors
    public ShareFileRequest() {}

    // Convenience method for backward compatibility
    @Schema(hidden = true)
    public Integer getAttachmentId() {
        return attachmentIds != null && !attachmentIds.isEmpty() ? attachmentIds.get(0) : null;
    }

    @Schema(hidden = true)
    public void setAttachmentId(Integer attachmentId) {
        if (attachmentId != null) {
            this.attachmentIds = List.of(attachmentId);
        }
    }

    // Getters and Setters
    public List<Integer> getAttachmentIds() {
        return attachmentIds;
    }

    public void setAttachmentIds(List<Integer> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    public List<Integer> getSharedWithUserIds() {
        return sharedWithUserIds;
    }

    public void setSharedWithUserIds(List<Integer> sharedWithUserIds) {
        this.sharedWithUserIds = sharedWithUserIds;
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
}