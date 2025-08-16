package com.project.quanlycanghangkhong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Request để upload attachment cho task cụ thể")
public class TaskAttachmentUploadRequest {
    
    @NotNull(message = "ID task không được null")
    @Schema(description = "ID của task cần thêm attachment", example = "123")
    private Integer taskId;
    
    @NotEmpty(message = "Danh sách attachment ID không được rỗng")
    @Schema(description = "Danh sách ID của attachment cần gán vào task", example = "[1, 2, 3]")
    private List<Integer> attachmentIds;
}
