package com.project.quanlycanghangkhong.dto.response.users;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for delete user, data is Void", required = true)
public class ApiDeleteUserResponse extends ApiResponseCustom<Void> {

    public ApiDeleteUserResponse(String message, int statusCode, Void data, boolean success) {
        super(message, statusCode, data, success);
    }
}
