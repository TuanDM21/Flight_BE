package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for my tasks API with count information")
public class MyTasksResponse {
    
    @Schema(description = "Response message")
    private String message;
    
    @Schema(description = "HTTP status code")
    private int status;
    
    @Schema(description = "List of tasks")
    private List<TaskDetailDTO> data;
    
    @Schema(description = "Total count of ROOT TASKS only (excludes subtasks)")
    private int totalCount;
    
    @Schema(description = "Task type (created, assigned, received)")
    private String type;
    
    @Schema(description = "Success flag")
    private boolean success;
    
    @Schema(description = "Additional metadata with ROOT TASKS count")
    private TaskCountMetadata metadata;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Task count metadata (ROOT TASKS only)")
    public static class TaskCountMetadata {
        @Schema(description = "Count of created ROOT tasks (not assigned yet)")
        private int createdCount;
        
        @Schema(description = "Count of assigned ROOT tasks (excludes subtasks in count)")
        private int assignedCount;
        
        @Schema(description = "Count of received ROOT tasks")
        private int receivedCount;
        
        @Schema(description = "Hierarchy information for assigned tasks")
        private HierarchyInfo hierarchyInfo;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Hierarchy information")
    public static class HierarchyInfo {
        @Schema(description = "Count of root tasks (level 0)")
        private int rootTasksCount;
        
        @Schema(description = "Count of subtasks (level > 0)")
        private int subtasksCount;
        
        @Schema(description = "Maximum hierarchy level")
        private int maxLevel;
        
        @Schema(description = "Count by level")
        private java.util.Map<Integer, Integer> countByLevel;
    }
}
