package com.project.quanlycanghangkhong.dto.response.activity;

import com.project.quanlycanghangkhong.dto.ActivityDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ⚠️ DEPRECATION NOTICE: This class will be removed in future versions.  
 * Use ApiResponseCustom<List<ActivityDTO>> directly instead.
 * 
 * Response wrapper for list of ActivityDTO operations.
 * Following the project's standardized API response pattern.
 */
@Schema(description = "Response wrapper for list of Activities")
public class ActivityListApiResponse extends ApiResponseCustom<List<ActivityDTO>> {
    
    public ActivityListApiResponse() {
        super();
    }
}
