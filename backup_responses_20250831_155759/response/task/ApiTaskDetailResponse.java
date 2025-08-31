package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for a single task detail, data is TaskDetailDTO", required = true)
public class ApiTaskDetailResponse extends ApiResponseCustom<TaskDetailDTO> {
    public ApiTaskDetailResponse(String message, int statusCode, TaskDetailDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}
