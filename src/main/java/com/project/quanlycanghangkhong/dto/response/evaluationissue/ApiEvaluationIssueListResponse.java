package com.project.quanlycanghangkhong.dto.response.evaluationissue;

import com.project.quanlycanghangkhong.dto.EvaluationIssueDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for list of evaluation issues", required = true)
public class ApiEvaluationIssueListResponse extends ApiResponseCustom<List<EvaluationIssueDTO>> {
    public ApiEvaluationIssueListResponse(String message, int statusCode, List<EvaluationIssueDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
