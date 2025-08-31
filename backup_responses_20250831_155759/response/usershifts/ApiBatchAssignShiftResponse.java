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
@Schema(description = "API response for batch assign shifts, data is List<UserShiftDTO>", required = true)
public class ApiBatchAssignShiftResponse extends ApiResponseCustom<List<UserShiftDTO>> {
    public ApiBatchAssignShiftResponse(String message, int statusCode, List<UserShiftDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}