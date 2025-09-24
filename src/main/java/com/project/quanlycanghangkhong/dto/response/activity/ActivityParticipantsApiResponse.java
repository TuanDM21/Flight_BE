package com.project.quanlycanghangkhong.dto.response.activity;

import com.project.quanlycanghangkhong.dto.ActivityParticipantDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * ⚠️ DEPRECATION NOTICE: This class will be removed in future versions.
 * Use ApiResponseCustom<List<ActivityParticipantDTO>> directly instead.
 * 
 * Response wrapper for activity participant operations.
 * Following the project's standardized API response pattern.
 */
@Schema(description = "Response wrapper for activity participants")
public class ActivityParticipantsApiResponse extends ApiResponseCustom<List<ActivityParticipantDTO>> {
    
    public ActivityParticipantsApiResponse() {
        super();
    }
}
