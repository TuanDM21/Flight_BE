package com.project.quanlycanghangkhong.dto.response.users;

import com.project.quanlycanghangkhong.dto.UserDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for current user, data is UserDTO", required = true)
public class ApiMeResponse extends ApiResponseCustom<UserDTO> {
    public ApiMeResponse(String message, int statusCode, com.project.quanlycanghangkhong.dto.UserDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}
