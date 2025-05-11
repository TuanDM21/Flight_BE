package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.TaskDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for a single task, data is TaskDTO", required = true)
public class ApiTaskResponse extends ApiResponseCustom<TaskDTO> {
    public ApiTaskResponse(String message, int statusCode, TaskDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}
