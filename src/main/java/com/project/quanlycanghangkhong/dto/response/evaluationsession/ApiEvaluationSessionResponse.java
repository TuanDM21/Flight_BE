package com.project.quanlycanghangkhong.dto.response.evaluationsession;

import com.project.quanlycanghangkhong.dto.EvaluationSessionDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for a single evaluation session", required = true)
public class ApiEvaluationSessionResponse extends ApiResponseCustom<EvaluationSessionDTO> {
    public ApiEvaluationSessionResponse(String message, int statusCode, EvaluationSessionDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}
