package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for task attachments, data is List<AttachmentDTO>", required = true)
public class ApiTaskAttachmentsResponse extends ApiResponseCustom<List<AttachmentDTO>> {
    public ApiTaskAttachmentsResponse(String message, int statusCode, List<AttachmentDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
