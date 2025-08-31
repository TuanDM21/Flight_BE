package com.project.quanlycanghangkhong.dto.response.task;

/**
 * Response DTO cho Task Subtree API với cấu trúc phân cấp nested
 */
public class ApiTaskTreeResponse {
    private String message;
    private Integer statusCode;
    private TaskTreeDTO data; // Single root task với nested subtasks
    private Boolean success;
    
    // Metadata
    private Integer totalTasks; // Tổng số task trong tree
    private Integer maxDepth;   // Độ sâu tối đa của tree
    private String structure;   // "hierarchical" để frontend biết đây là nested structure
    
    public ApiTaskTreeResponse() {}
    
    public ApiTaskTreeResponse(String message, Integer statusCode, TaskTreeDTO data, Boolean success) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
        this.success = success;
        this.structure = "hierarchical";
        
        if (data != null) {
            this.totalTasks = 1 + data.getTotalSubtasks(); // Root + all subtasks
            this.maxDepth = calculateMaxDepth(data, 0);
        } else {
            this.totalTasks = 0;
            this.maxDepth = 0;
        }
    }
    
    /**
     * Tính độ sâu tối đa của tree
     */
    private Integer calculateMaxDepth(TaskTreeDTO task, Integer currentDepth) {
        if (task.getSubtasks() == null || task.getSubtasks().isEmpty()) {
            return currentDepth;
        }
        
        Integer maxChildDepth = currentDepth;
        for (TaskTreeDTO subtask : task.getSubtasks()) {
            Integer childDepth = calculateMaxDepth(subtask, currentDepth + 1);
            maxChildDepth = Math.max(maxChildDepth, childDepth);
        }
        
        return maxChildDepth;
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Integer getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
    
    public TaskTreeDTO getData() {
        return data;
    }
    
    public void setData(TaskTreeDTO data) {
        this.data = data;
        if (data != null) {
            this.totalTasks = 1 + data.getTotalSubtasks();
            this.maxDepth = calculateMaxDepth(data, 0);
        }
    }
    
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public Integer getTotalTasks() {
        return totalTasks;
    }
    
    public void setTotalTasks(Integer totalTasks) {
        this.totalTasks = totalTasks;
    }
    
    public Integer getMaxDepth() {
        return maxDepth;
    }
    
    public void setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth;
    }
    
    public String getStructure() {
        return structure;
    }
    
    public void setStructure(String structure) {
        this.structure = structure;
    }
}
