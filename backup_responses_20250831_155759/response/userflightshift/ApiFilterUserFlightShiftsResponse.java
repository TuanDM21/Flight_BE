package com.project.quanlycanghangkhong.dto.response.userflightshift;

import com.project.quanlycanghangkhong.dto.UserFlightShiftResponseSearchDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for filtering user flight shifts, data is List<UserFlightShiftResponseSearchDTO>", required = true)
public class ApiFilterUserFlightShiftsResponse extends ApiResponseCustom<List<UserFlightShiftResponseSearchDTO>> {
    public ApiFilterUserFlightShiftsResponse(String message, int statusCode, List<UserFlightShiftResponseSearchDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
