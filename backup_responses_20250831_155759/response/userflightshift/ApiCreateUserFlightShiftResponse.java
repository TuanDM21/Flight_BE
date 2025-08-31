package com.project.quanlycanghangkhong.dto.response.userflightshift;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for creating user flight shift, data is null", required = true)
public class ApiCreateUserFlightShiftResponse extends ApiResponseCustom<Void> {
    public ApiCreateUserFlightShiftResponse(String message, int statusCode, Void data, boolean success) {
        super(message, statusCode, data, success);
    }
}
