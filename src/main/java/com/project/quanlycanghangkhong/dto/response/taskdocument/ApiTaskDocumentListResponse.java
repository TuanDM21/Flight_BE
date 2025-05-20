package com.project.quanlycanghangkhong.dto.response.taskdocument;

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
@Schema(description = "API response for list of task documents", required = true)
public class ApiTaskDocumentListResponse extends ApiResponseCustom<List<DocumentDTO>> {
    public ApiTaskDocumentListResponse(String message, int statusCode, List<DocumentDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
