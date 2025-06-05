package com.project.quanlycanghangkhong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "Request để xóa nhiều người khỏi chia sẻ file")
public class BulkRevokeMultipleUsersRequest {
    
    @NotNull(message = "Attachment ID không được để trống")
    @Schema(description = "ID của file cần xóa chia sẻ", required = true)
    private Integer attachmentId;
    
    @NotEmpty(message = "Danh sách user ID không được để trống")
    @Schema(description = "Danh sách ID của user cần xóa khỏi chia sẻ", required = true)
    private List<Integer> userIds;
    
    public BulkRevokeMultipleUsersRequest() {}
    
    public Integer getAttachmentId() {
        return attachmentId;
    }
    
    public void setAttachmentId(Integer attachmentId) {
        this.attachmentId = attachmentId;
    }
    
    public List<Integer> getUserIds() {
        return userIds;
    }
    
    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }
}
