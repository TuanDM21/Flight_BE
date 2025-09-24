package com.project.quanlycanghangkhong.dto.response.activity;

import com.project.quanlycanghangkhong.dto.ActivityDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ⚠️ DEPRECATION NOTICE: This class will be removed in future versions.
 * Use ApiResponseCustom<ActivityDTO> directly instead.
 * 
 * Response wrapper for single ActivityDTO operations.
 * Following the project's standardized API response pattern.
 */
@Schema(description = "Response wrapper for single Activity operations")
public class ActivityApiResponse extends ApiResponseCustom<ActivityDTO> {
    
    public ActivityApiResponse() {
        super();
    }
}
