package com.project.quanlycanghangkhong.request;

import java.util.List;
import com.project.quanlycanghangkhong.model.TaskPriority;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateTaskRequest {
    
    // Constructor mặc định cần thiết cho Jackson
    public CreateTaskRequest() {}
    
    public CreateTaskRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
    private String title;
    private String content;
    private String instructions;
    private String notes;
    private TaskPriority priority;
    private List<AssignmentRequest> assignments;
    
    // MỚI: Chỉ hỗ trợ attachment trực tiếp (thay thế hoàn toàn documents)
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
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Thay thế cách tiếp cận dựa trên document bằng việc gán attachment trực tiếp
     * @return Danh sách ID attachment để gán vào task
     */
    public List<Integer> getAttachmentIds() {
        return attachmentIds;
    }

    /**
     * Đặt danh sách ID attachment để gán trực tiếp vào task
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Thay thế cách tiếp cận documentIds và newDocuments cũ
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