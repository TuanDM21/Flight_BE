package com.project.quanlycanghangkhong.dto.response.evaluationgroup;

import com.project.quanlycanghangkhong.dto.EvaluationGroupDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for a single evaluation group", required = true)
public class ApiEvaluationGroupResponse extends ApiResponseCustom<EvaluationGroupDTO> {
    public ApiEvaluationGroupResponse(String message, int statusCode, EvaluationGroupDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}
