package com.project.quanlycanghangkhong.dto.response.fileshare;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for creating file share", required = true)
public class ApiCreateFileShareResponse extends ApiResponseCustom<String> {
    public ApiCreateFileShareResponse(String message, int statusCode, String data, boolean success) {
        super(message, statusCode, data, success);
    }
}