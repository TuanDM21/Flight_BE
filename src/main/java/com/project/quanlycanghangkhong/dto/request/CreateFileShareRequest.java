package com.project.quanlycanghangkhong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Request để chia sẻ file với user khác")
public class CreateFileShareRequest {
    
    @NotNull(message = "ID file đính kèm không được null")
    @Schema(description = "ID của file cần chia sẻ", example = "123")
    private Integer attachmentId;
    
    @NotEmpty(message = "Danh sách ID user không được rỗng")
    @Schema(description = "Danh sách ID của user được chia sẻ", example = "[1, 2, 3]")
    private List<Integer> userIds;
}