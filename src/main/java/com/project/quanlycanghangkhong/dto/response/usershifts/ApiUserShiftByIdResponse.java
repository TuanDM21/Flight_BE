package com.project.quanlycanghangkhong.dto.response.usershifts;

import com.project.quanlycanghangkhong.dto.UserShiftDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for get user shift by ID, data is UserShiftDTO", required = true)
public class ApiUserShiftByIdResponse extends ApiResponseCustom<UserShiftDTO> {
    public ApiUserShiftByIdResponse(String message, int statusCode, UserShiftDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}