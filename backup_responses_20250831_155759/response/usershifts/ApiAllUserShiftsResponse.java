package com.project.quanlycanghangkhong.dto.response.usershifts;

import java.util.List;
import com.project.quanlycanghangkhong.dto.UserShiftDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for get all user shifts, data is List<UserShiftDTO>", required = true)
public class ApiAllUserShiftsResponse extends ApiResponseCustom<List<UserShiftDTO>> {
    public ApiAllUserShiftsResponse(String message, int statusCode, List<UserShiftDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}