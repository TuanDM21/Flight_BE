package com.project.quanlycanghangkhong.request;

import java.util.List;
import com.project.quanlycanghangkhong.model.TaskPriority;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to create a new task")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateTaskRequest {

    // Constructor mặc định cần thiết cho Jackson
    public CreateTaskRequest() {
    }

    public CreateTaskRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Schema(description = "Task title", example = "Kiểm tra hành lý", required = true)
    private String title;

    @Schema(description = "Task content/description", example = "Kiểm tra hành lý khách hàng")
    private String content;

    @Schema(description = "Task instructions", example = "Thực hiện theo quy trình ABC")
    private String instructions;

    @Schema(description = "Task notes", example = "Ghi chú bổ sung")
    private String notes;

    @Schema(description = "Task priority", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "List of task assignments")
    private List<AssignmentRequest> assignments;

    // MỚI: Chỉ hỗ trợ attachment trực tiếp (thay thế hoàn toàn documents)
    @Schema(description = "List of attachment IDs to assign to this task", example = "[1, 2, 3]")
    private List<Integer> attachmentIds; // Gán attachment trực tiếp vào task

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

    public List<AssignmentRequest> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentRequest> assignments) {
        this.assignments = assignments;
    }

    /**
     * Lấy danh sách ID attachment để gán trực tiếp vào task
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Thay thế cách tiếp cận dựa trên document bằng việc
     * gán attachment trực tiếp
     * 
     * @return Danh sách ID attachment để gán vào task
     */
    public List<Integer> getAttachmentIds() {
        return attachmentIds;
    }

    /**
     * Đặt danh sách ID attachment để gán trực tiếp vào task
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Thay thế cách tiếp cận documentIds và newDocuments
     * cũ
     * 
     * @param attachmentIds Danh sách ID attachment hiện có để gán vào task
     */
    public void setAttachmentIds(List<Integer> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    @Override
    public String toString() {
        return "CreateTaskRequest{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", instructions='" + instructions + '\'' +
                ", notes='" + notes + '\'' +
                ", priority=" + priority +
                ", assignments=" + (assignments != null ? assignments.size() + " assignments" : "null") +
                ", attachmentIds=" + (attachmentIds != null ? attachmentIds.size() + " attachments" : "null") +
                '}';
    }
}
