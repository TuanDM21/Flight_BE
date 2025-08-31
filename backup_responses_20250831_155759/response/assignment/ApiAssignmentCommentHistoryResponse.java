package com.project.quanlycanghangkhong.dto.response.assignment;

import com.project.quanlycanghangkhong.dto.AssignmentCommentHistoryDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for assignment comment history", required = true)
public class ApiAssignmentCommentHistoryResponse extends ApiResponseCustom<List<AssignmentCommentHistoryDTO>> {
    public ApiAssignmentCommentHistoryResponse(String message, int statusCode, List<AssignmentCommentHistoryDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
