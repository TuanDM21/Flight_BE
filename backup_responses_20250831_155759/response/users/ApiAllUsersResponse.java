package com.project.quanlycanghangkhong.dto.response.users;

import com.project.quanlycanghangkhong.dto.UserDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for all users, data is List<UserDTO>", required = true)
public class ApiAllUsersResponse extends ApiResponseCustom<List<UserDTO>> {
    public ApiAllUsersResponse(String message, int statusCode, java.util.List<com.project.quanlycanghangkhong.dto.UserDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
