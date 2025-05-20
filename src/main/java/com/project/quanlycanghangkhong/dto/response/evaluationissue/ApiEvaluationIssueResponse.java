package com.project.quanlycanghangkhong.dto.response.evaluationissue;

import com.project.quanlycanghangkhong.dto.EvaluationIssueDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for a single evaluation issue", required = true)
public class ApiEvaluationIssueResponse extends ApiResponseCustom<EvaluationIssueDTO> {
    public ApiEvaluationIssueResponse(String message, int statusCode, EvaluationIssueDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}
