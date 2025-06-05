package com.project.quanlycanghangkhong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;

@Schema(description = "Request để xóa hết chia sẻ của một file")
public class BulkRevokeAllSharesRequest {
    
    @NotNull(message = "Attachment ID không được để trống")
    @Schema(description = "ID của file cần xóa hết chia sẻ", required = true)
    private Integer attachmentId;
    
    public BulkRevokeAllSharesRequest() {}
    
    public Integer getAttachmentId() {
        return attachmentId;
    }
    
    public void setAttachmentId(Integer attachmentId) {
        this.attachmentId = attachmentId;
    }
}