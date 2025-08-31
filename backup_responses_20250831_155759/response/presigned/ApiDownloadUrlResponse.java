package com.project.quanlycanghangkhong.dto.response.presigned;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiDownloadUrlResponse", description = "Response cho download URL")
public class ApiDownloadUrlResponse extends ApiResponseCustom<String> {

    public ApiDownloadUrlResponse() {
        super();
    }

    public ApiDownloadUrlResponse(String message, int statusCode, String data, boolean success) {
        super(message, statusCode, data, success);
    }
}