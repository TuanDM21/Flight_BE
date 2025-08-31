package com.project.quanlycanghangkhong.dto.response.attachment;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for deleting attachment", required = true)
public class ApiDeleteAttachmentResponse extends ApiResponseCustom<Void> {
    public ApiDeleteAttachmentResponse(String message, int statusCode, Void data, boolean success) {
        super(message, statusCode, data, success);
    }
}