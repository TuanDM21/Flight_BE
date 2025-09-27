package com.project.quanlycanghangkhong.dto;

import java.time.LocalDateTime;
import com.project.quanlycanghangkhong.model.TaskStatus;
import com.project.quanlycanghangkhong.model.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task data transfer object")
public class TaskDTO {
    @Schema(description = "Task ID", example = "1")
    private Integer id;

    @Schema(description = "Task title", example = "Kiểm tra hành lý", required = true)
    private String title;

    @Schema(description = "Task content/description", example = "Kiểm tra hành lý khách hàng")
    private String content;

    @Schema(description = "Task instructions", example = "Thực hiện theo quy trình ABC")
    private String instructions;

    @Schema(description = "Task notes", example = "Ghi chú bổ sung")
    private String notes;

    @Schema(description = "Task status", example = "IN_PROGRESS")
    private TaskStatus status;

    @Schema(description = "Task priority", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Task type information")
    private TaskTypeDTO taskType;

    @Schema(description = "Creation timestamp", example = "2025-09-04T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2025-09-04T11:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "User ID who created this task", example = "123")
    private Integer createdBy; // userId

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskTypeDTO getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskTypeDTO taskType) {
        this.taskType = taskType;
    }
}
