package com.project.quanlycanghangkhong.dto.response.airports;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for delete airport, data is null", required = true)
public class ApiDeleteAirportResponse extends ApiResponseCustom<Object> {
    public ApiDeleteAirportResponse(String message, int statusCode, Object data, boolean success) {
        super(message, statusCode, data, success);
    }
}