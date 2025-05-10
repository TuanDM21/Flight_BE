package com.project.quanlycanghangkhong.dto.response.users;

import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for update user, data is User", required = true)
public class ApiUpdateUserResponse extends ApiResponseCustom<User> {
    public ApiUpdateUserResponse(String message, int statusCode, User data, boolean success) {
        super(message, statusCode, data, success);
    }
}
