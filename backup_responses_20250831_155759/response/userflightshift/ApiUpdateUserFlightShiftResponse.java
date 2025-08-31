package com.project.quanlycanghangkhong.dto.response.userflightshift;

import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for update user flight shift, data is UserFlightShiftResponseDTO", required = true)
public class ApiUpdateUserFlightShiftResponse extends ApiResponseCustom<UserFlightShiftResponseDTO> {
    public ApiUpdateUserFlightShiftResponse(String message, int statusCode, UserFlightShiftResponseDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}
