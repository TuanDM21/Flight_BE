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
@Schema(description = "API response for assigned user flight shifts, data is List<UserFlightShiftResponseDTO>", required = true)
public class ApiAssignedUserFlightShiftsResponse extends ApiResponseCustom<List<UserFlightShiftResponseDTO>> {
    public ApiAssignedUserFlightShiftsResponse(String message, int statusCode, List<UserFlightShiftResponseDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
