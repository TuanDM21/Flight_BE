package com.project.quanlycanghangkhong.dto.response.evaluationsession;

import com.project.quanlycanghangkhong.dto.EvaluationSessionDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for list of evaluation sessions", required = true)
public class ApiEvaluationSessionListResponse extends ApiResponseCustom<List<EvaluationSessionDTO>> {
    public ApiEvaluationSessionListResponse(String message, int statusCode, List<EvaluationSessionDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
