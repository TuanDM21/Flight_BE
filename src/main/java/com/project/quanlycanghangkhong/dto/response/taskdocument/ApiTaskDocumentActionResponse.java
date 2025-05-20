package com.project.quanlycanghangkhong.dto.response.taskdocument;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for task document action", required = true)
public class ApiTaskDocumentActionResponse extends ApiResponseCustom<Void> {
    public ApiTaskDocumentActionResponse(String message, int statusCode, boolean success) {
        super(message, statusCode, null, success);
    }
}
