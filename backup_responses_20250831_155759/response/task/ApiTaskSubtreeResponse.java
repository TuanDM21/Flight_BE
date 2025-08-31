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
@Schema(description = "API response for task subtree (flat list), data is List<TaskDetailDTO>", required = true)
public class ApiTaskSubtreeResponse extends ApiResponseCustom<List<TaskDetailDTO>> {
    
    @Schema(description = "Total number of tasks in the subtree")
    private Integer totalTasks;
    
    @Schema(description = "Structure type for frontend understanding")
    private String structure = "flat";
    
    @Schema(description = "Root task ID")
    private Integer rootTaskId;
    
    public ApiTaskSubtreeResponse(String message, int statusCode, List<TaskDetailDTO> data, boolean success) {
        super(message, statusCode, data, success);
        if (data != null && !data.isEmpty()) {
            this.totalTasks = data.size();
            this.rootTaskId = data.get(0).getId(); // First task is the root
        } else {
            this.totalTasks = 0;
            this.rootTaskId = null;
        }
    }
}
