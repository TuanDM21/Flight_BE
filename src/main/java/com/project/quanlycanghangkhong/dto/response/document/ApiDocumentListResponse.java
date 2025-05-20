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
@Schema(description = "API response for list of documents", required = true)
public class ApiDocumentListResponse extends ApiResponseCustom<List<DocumentDTO>> {
    public ApiDocumentListResponse(String message, int statusCode, List<DocumentDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
