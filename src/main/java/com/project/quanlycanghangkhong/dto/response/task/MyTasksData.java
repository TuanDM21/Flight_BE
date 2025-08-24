package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Data structure for my tasks API response")
public class MyTasksData {
    
    @Schema(description = "List of tasks")
    private List<TaskDetailDTO> tasks;
    
    @Schema(description = "Task type (created, assigned, received)")
    private String type;
    
    @Schema(description = "Pagination information (optional)")
    private PaginationInfo pagination;
    
    // Constructor với pagination
    public MyTasksData(List<TaskDetailDTO> tasks, String type, PaginationInfo pagination) {
        this.tasks = tasks;
        this.type = type;
        this.pagination = pagination;
    }
    
    // Constructor không có pagination (backward compatibility)
    public MyTasksData(List<TaskDetailDTO> tasks, String type) {
        this.tasks = tasks;
        this.type = type;
        this.pagination = null;
    }
}
