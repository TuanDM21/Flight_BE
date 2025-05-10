package com.project.quanlycanghangkhong.dto.response.userflightshift;

import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for user flight shifts by flight and date, data is List<UserFlightShiftResponseDTO>", required = true)
public class ApiUserFlightShiftsByFlightAndDateResponse extends ApiResponseCustom<List<UserFlightShiftResponseDTO>> {
    public ApiUserFlightShiftsByFlightAndDateResponse(String message, int statusCode, List<UserFlightShiftResponseDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
