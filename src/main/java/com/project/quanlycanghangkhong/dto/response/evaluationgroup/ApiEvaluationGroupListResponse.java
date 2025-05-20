package com.project.quanlycanghangkhong.dto.response.evaluationgroup;

import com.project.quanlycanghangkhong.dto.EvaluationGroupDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for list of evaluation groups", required = true)
public class ApiEvaluationGroupListResponse extends ApiResponseCustom<List<EvaluationGroupDTO>> {
    public ApiEvaluationGroupListResponse(String message, int statusCode, List<EvaluationGroupDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
