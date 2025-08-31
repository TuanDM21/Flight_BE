package com.project.quanlycanghangkhong.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Request để upload attachment cho task cụ thể")
public class TaskAttachmentUploadRequest {
    
    @NotEmpty(message = "Danh sách attachment ID không được rỗng")
    @Schema(description = "Danh sách ID của attachment cần gán vào task", example = "[1, 2, 3]")
    private List<Integer> attachmentIds;
}
