package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API response for bulk delete tasks")
public class ApiBulkDeleteTasksResponse extends ApiResponseCustom<String> {
    
    public ApiBulkDeleteTasksResponse() {
        super();
    }

    public ApiBulkDeleteTasksResponse(String message, int statusCode, String data, boolean success) {
        super(message, statusCode, data, success);
    }
}