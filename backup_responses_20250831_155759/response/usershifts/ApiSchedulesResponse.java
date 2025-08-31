package com.project.quanlycanghangkhong.dto.response.usershifts;

import java.util.List;
import com.project.quanlycanghangkhong.dto.ScheduleDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for get schedules, data is List<ScheduleDTO>", required = true)
public class ApiSchedulesResponse extends ApiResponseCustom<List<ScheduleDTO>> {
    public ApiSchedulesResponse(String message, int statusCode, List<ScheduleDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}