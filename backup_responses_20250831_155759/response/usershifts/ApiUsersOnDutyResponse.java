package com.project.quanlycanghangkhong.dto.response.usershifts;

import java.util.List;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for users on duty, data is List<Integer>", required = true)
public class ApiUsersOnDutyResponse extends ApiResponseCustom<List<Integer>> {
    public ApiUsersOnDutyResponse(String message, int statusCode, List<Integer> data, boolean success) {
        super(message, statusCode, data, success);
    }
}