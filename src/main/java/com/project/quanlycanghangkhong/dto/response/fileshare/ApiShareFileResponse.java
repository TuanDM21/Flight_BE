package com.project.quanlycanghangkhong.dto.response.fileshare;

import com.project.quanlycanghangkhong.dto.FileShareDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for file sharing operations", required = true)
public class ApiShareFileResponse extends ApiResponseCustom<List<FileShareDTO>> {
    public ApiShareFileResponse(String message, int statusCode, List<FileShareDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}