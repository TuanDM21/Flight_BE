package com.project.quanlycanghangkhong.dto.response.usershifts;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for delete user shift, data is null", required = true)
public class ApiDeleteUserShiftResponse extends ApiResponseCustom<Void> {
    public ApiDeleteUserShiftResponse(String message, int statusCode, Void data, boolean success) {
        super(message, statusCode, data, success);
    }
}