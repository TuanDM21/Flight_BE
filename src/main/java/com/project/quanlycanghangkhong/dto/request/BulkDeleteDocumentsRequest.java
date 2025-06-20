package com.project.quanlycanghangkhong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "Request để xóa nhiều document cùng lúc")
public class BulkDeleteDocumentsRequest {
    
    @NotNull
    @NotEmpty
    @Schema(description = "Danh sách ID của các document cần xóa", example = "[1, 2, 3]", required = true)
    private List<Integer> documentIds;

    // Constructors
    public BulkDeleteDocumentsRequest() {}

    public BulkDeleteDocumentsRequest(List<Integer> documentIds) {
        this.documentIds = documentIds;
    }

    // Getters and Setters
    public List<Integer> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Integer> documentIds) {
        this.documentIds = documentIds;
    }
}