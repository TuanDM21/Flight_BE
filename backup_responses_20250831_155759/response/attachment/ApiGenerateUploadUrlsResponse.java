package com.project.quanlycanghangkhong.dto.response.attachment;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.response.presigned.FlexiblePreSignedUrlResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for generating pre-signed upload URLs", required = true)
public class ApiGenerateUploadUrlsResponse extends ApiResponseCustom<FlexiblePreSignedUrlResponse> {
    public ApiGenerateUploadUrlsResponse(String message, int statusCode, FlexiblePreSignedUrlResponse data, boolean success) {
        super(message, statusCode, data, success);
    }
}