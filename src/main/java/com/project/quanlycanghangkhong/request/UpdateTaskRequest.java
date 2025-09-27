package com.project.quanlycanghangkhong.request;

import java.util.List;
import com.project.quanlycanghangkhong.model.TaskPriority;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update an existing task")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateTaskRequest {

    public UpdateTaskRequest() {
    }

    @Schema(description = "Task title", example = "Kiểm tra hành lý")
    private String title;

    @Schema(description = "Task content/description", example = "Kiểm tra hành lý khách hàng")
    private String content;

    @Schema(description = "Task instructions", example = "Thực hiện theo quy trình ABC")
    private String instructions;

    @Schema(description = "Task notes", example = "Ghi chú bổ sung")
    private String notes;

    @Schema(description = "Task priority", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Task type ID", example = "1")
    private Integer taskTypeId;

    @Schema(description = "List of task assignments")
    private List<AssignmentRequest> assignments;

    @Schema(description = "List of attachment IDs to assign to this task", example = "[1, 2, 3]")
    private List<Integer> attachmentIds;

    // Getters and setters
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

    public Integer getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(Integer taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public List<AssignmentRequest> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentRequest> assignments) {
        this.assignments = assignments;
    }

    public List<Integer> getAttachmentIds() {
        return attachmentIds;
    }

    public void setAttachmentIds(List<Integer> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    @Override
    public String toString() {
        return "UpdateTaskRequest{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", instructions='" + instructions + '\'' +
                ", notes='" + notes + '\'' +
                ", priority=" + priority +
                ", taskTypeId=" + taskTypeId +
                ", assignments=" + assignments +
                ", attachmentIds=" + attachmentIds +
                '}';
    }
}
