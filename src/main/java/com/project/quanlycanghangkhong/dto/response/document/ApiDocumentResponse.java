package com.project.quanlycanghangkhong.dto.response.document;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for a single document", required = true)
public class ApiDocumentResponse extends ApiResponseCustom<DocumentDTO> {
    public ApiDocumentResponse(String message, int statusCode, DocumentDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}

