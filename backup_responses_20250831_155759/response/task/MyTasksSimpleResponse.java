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
@Schema(description = "Simplified response for my tasks API - extends standard ApiResponseCustom")
public class MyTasksSimpleResponse extends ApiResponseCustom<MyTasksSimpleResponse.MyTasksData> {
    
    public MyTasksSimpleResponse(String message, int statusCode, MyTasksData data, boolean success) {
        super(message, statusCode, data, success);
    }
    
    @Data
    @NoArgsConstructor
    @Schema(description = "Data container for my tasks")
    public static class MyTasksData {
        @Schema(description = "List of tasks (includes hierarchy)")
        private List<TaskDetailDTO> tasks;
        
        @Schema(description = "Task type (created, assigned, received)")
        private String type;
        
        @Schema(description = "Total count of ROOT TASKS only (excludes subtasks)")
        private int totalRootCount;
        
        @Schema(description = "Count breakdown by task type (for reference)")
        private TaskCounts counts;
        
        public MyTasksData(List<TaskDetailDTO> tasks, String type, int totalRootCount, TaskCounts counts) {
            this.tasks = tasks;
            this.type = type;
            this.totalRootCount = totalRootCount;
            this.counts = counts;
        }
    }
    
    @Data
    @NoArgsConstructor
    @Schema(description = "Simple task counts (ROOT TASKS only)")
    public static class TaskCounts {
        @Schema(description = "Count of created ROOT tasks")
        private int created;
        
        @Schema(description = "Count of assigned ROOT tasks") 
        private int assigned;
        
        @Schema(description = "Count of received ROOT tasks")
        private int received;
        
        public TaskCounts(int created, int assigned, int received) {
            this.created = created;
            this.assigned = assigned;
            this.received = received;
        }
    }
}
