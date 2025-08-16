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
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Simplified task metadata - flattened structure")
    public static class TaskMetadata {
        
        @Schema(description = "Count of created ROOT tasks (not assigned yet)")
        private int createdCount;
        
        @Schema(description = "Count of assigned ROOT tasks (excludes subtasks in count)")
        private int assignedCount;
        
        @Schema(description = "Count of received ROOT tasks")
        private int receivedCount;
        
        // Flattened hierarchy info - NO MORE NESTED HierarchyInfo object
        @Schema(description = "Count of root tasks (level 0) - flattened from hierarchyInfo")
        private int rootTasksCount;
        
        @Schema(description = "Count of subtasks (level > 0) - flattened from hierarchyInfo")
        private int subtasksCount;
        
        @Schema(description = "Maximum hierarchy level - flattened from hierarchyInfo")
        private int maxLevel;
        
        // REMOVED: countByLevel Map - too complex and nested
        // Frontend can calculate this if needed from the tasks data
    }
}
