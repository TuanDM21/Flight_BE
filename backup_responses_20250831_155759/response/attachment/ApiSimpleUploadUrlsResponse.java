package com.project.quanlycanghangkhong.dto.response.attachment;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.response.presigned.PreSignedUrlResponseSimple;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Simplified API response for pre-signed upload URLs", required = true)
public class ApiSimpleUploadUrlsResponse extends ApiResponseCustom<List<PreSignedUrlResponseSimple>> {
    public ApiSimpleUploadUrlsResponse(String message, int statusCode, List<PreSignedUrlResponseSimple> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
