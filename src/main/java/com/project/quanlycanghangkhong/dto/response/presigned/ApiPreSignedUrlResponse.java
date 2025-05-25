package com.project.quanlycanghangkhong.dto.response.presigned;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiPreSignedUrlResponse", description = "Response cho pre-signed URL")
public class ApiPreSignedUrlResponse extends ApiResponseCustom<PreSignedUrlResponse> {

    public ApiPreSignedUrlResponse() {
        super();
    }

    public ApiPreSignedUrlResponse(String message, int statusCode, PreSignedUrlResponse data, boolean success) {
        super(message, statusCode, data, success);
    }
}