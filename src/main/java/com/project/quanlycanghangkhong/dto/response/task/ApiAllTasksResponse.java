package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for all tasks, data is List<TaskDetailDTO>", required = true)
public class ApiAllTasksResponse extends ApiResponseCustom<List<TaskDetailDTO>> {
    public ApiAllTasksResponse(String message, int statusCode, List<TaskDetailDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
