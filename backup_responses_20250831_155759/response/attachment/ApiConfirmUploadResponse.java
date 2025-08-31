package com.project.quanlycanghangkhong.dto.response.attachment;

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
@Schema(description = "API response for confirming upload", required = true)
public class ApiConfirmUploadResponse extends ApiResponseCustom<List<AttachmentDTO>> {
    public ApiConfirmUploadResponse(String message, int statusCode, List<AttachmentDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}