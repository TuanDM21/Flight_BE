package com.project.quanlycanghangkhong.dto;

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
    
    @Schema(description = "Pagination information (optional)")
    private PaginationInfo pagination;
    
    // Constructor với pagination
    public MyTasksData(List<TaskDetailDTO> tasks, PaginationInfo pagination) {
        this.tasks = tasks;
        this.pagination = pagination;
    }
    
    // Constructor không có pagination
    public MyTasksData(List<TaskDetailDTO> tasks) {
        this.tasks = tasks;
        this.pagination = null;
    }
}
