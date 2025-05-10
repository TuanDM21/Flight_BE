package com.project.quanlycanghangkhong.dto.response.userflightshift;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for checking if user is assigned to flight, data is Map<String, Boolean>", required = true)
public class ApiCheckUserAssignedFlightResponse extends ApiResponseCustom<Map<String, Boolean>> {
    public ApiCheckUserAssignedFlightResponse(String message, int statusCode, Map<String, Boolean> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
