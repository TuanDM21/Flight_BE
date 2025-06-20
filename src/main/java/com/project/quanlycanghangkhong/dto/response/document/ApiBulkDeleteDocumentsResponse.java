package com.project.quanlycanghangkhong.dto.response.document;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API response for bulk delete documents")
public class ApiBulkDeleteDocumentsResponse extends ApiResponseCustom<String> {
    
    public ApiBulkDeleteDocumentsResponse() {
        super();
    }

    public ApiBulkDeleteDocumentsResponse(String message, int statusCode, String data, boolean success) {
        super(message, statusCode, data, success);
    }
}