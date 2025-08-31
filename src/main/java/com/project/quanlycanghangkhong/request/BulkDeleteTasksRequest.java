package com.project.quanlycanghangkhong.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "Request để xóa nhiều task cùng lúc")
public class BulkDeleteTasksRequest {
    
    @NotNull
    @NotEmpty
    @Schema(description = "Danh sách ID của các task cần xóa", example = "[1, 2, 3]", required = true)
    private List<Integer> taskIds;

    // Constructors
    public BulkDeleteTasksRequest() {}

    public BulkDeleteTasksRequest(List<Integer> taskIds) {
        this.taskIds = taskIds;
    }

    // Getters and Setters
    public List<Integer> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Integer> taskIds) {
        this.taskIds = taskIds;
    }
}