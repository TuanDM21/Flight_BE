package com.project.quanlycanghangkhong.dto;

import com.project.quanlycanghangkhong.model.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update an existing task")
public class UpdateTaskDTO {
    @Schema(description = "Task ID", example = "1")
    private Integer id;

    @Schema(description = "Task title", example = "Kiểm tra hành lý (updated)")
    private String title;

    @Schema(description = "Task content/description", example = "Kiểm tra hành lý khách hàng - cập nhật")
    private String content;

    @Schema(description = "Task instructions", example = "Thực hiện theo quy trình ABC - cập nhật")
    private String instructions;

    @Schema(description = "Task notes", example = "Ghi chú bổ sung - cập nhật")
    private String notes;

    @Schema(description = "Task priority", example = "URGENT")
    private TaskPriority priority;

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

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }
}
