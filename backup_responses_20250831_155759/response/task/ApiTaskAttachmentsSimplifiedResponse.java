package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.simplified.SimpleAttachmentDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for task attachments (simplified structure), data is List<SimpleAttachmentDTO>", required = true)
public class ApiTaskAttachmentsSimplifiedResponse extends ApiResponseCustom<List<SimpleAttachmentDTO>> {
    public ApiTaskAttachmentsSimplifiedResponse(String message, int statusCode, List<SimpleAttachmentDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
