package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Data structure for my tasks API response")
public class MyTasksData {
    
    @Schema(description = "List of tasks")
    private List<TaskDetailDTO> tasks;
    
    @Schema(description = "Total count of ROOT TASKS only (excludes subtasks)")
    private int totalCount;
    
    @Schema(description = "Task type (created, assigned, received)")
    private String type;
    
    @Schema(description = "Simplified metadata with task counts")
    private TaskMetadata metadata;
    
    @Schema(description = "Pagination information (optional)")
    private PaginationInfo pagination;
    
    // Constructor với pagination
    public MyTasksData(List<TaskDetailDTO> tasks, int totalCount, String type, TaskMetadata metadata, PaginationInfo pagination) {
        this.tasks = tasks;
        this.totalCount = totalCount;
        this.type = type;
        this.metadata = metadata;
        this.pagination = pagination;
    }
    
    // Constructor không có pagination (backward compatibility)
    public MyTasksData(List<TaskDetailDTO> tasks, int totalCount, String type, TaskMetadata metadata) {
        this.tasks = tasks;
        this.totalCount = totalCount;
        this.type = type;
        this.metadata = metadata;
        this.pagination = null;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Simplified task metadata - flat structure")
    public static class TaskMetadata {
        
        @Schema(description = "Count of created tasks")
        private int createdCount;
        
        @Schema(description = "Count of assigned tasks")
        private int assignedCount;
        
        @Schema(description = "Count of received tasks")
        private int receivedCount;
    }
}
