package com.project.quanlycanghangkhong.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.project.quanlycanghangkhong.model.TaskPriority;

public class TaskDetailDTO {
    private Integer id;
    private String title;
    private String content;
    private String instructions;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO createdByUser;
    private List<AssignmentDTO> assignments;
    private com.project.quanlycanghangkhong.model.TaskStatus status;
    private TaskPriority priority;
    
    // MỚI: Hỗ trợ subtask cho mô hình Adjacency List
    private Integer parentId; // Tham chiếu đến ID task cha
    
    // MỚI: Attachment trực tiếp (THAY THẾ hoàn toàn documents)
    private List<AttachmentDTO> attachments; // Quan hệ task-attachment trực tiếp

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

    public UserDTO getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(UserDTO createdByUser) {
        this.createdByUser = createdByUser;
    }

    public List<AssignmentDTO> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentDTO> assignments) {
        this.assignments = assignments;
    }

    public com.project.quanlycanghangkhong.model.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(com.project.quanlycanghangkhong.model.TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    /**
     * Lấy ID task cha cho cấu trúc phân cấp Adjacency List
     * @return ID task cha, null nếu đây là task gốc
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * Đặt ID task cha cho cấu trúc phân cấp Adjacency List
     * @param parentId ID task cha
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * Lấy attachment được liên kết trực tiếp với task này
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Quan hệ task-attachment trực tiếp thay thế cách tiếp cận dựa trên document
     * @return Danh sách attachment được liên kết trực tiếp với task
     */
    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    /**
     * Đặt attachment được liên kết trực tiếp với task này
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Thay thế quản lý file dựa trên document cũ
     * @param attachments Danh sách attachment để liên kết với task
     */
    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TaskDetailDTO that = (TaskDetailDTO) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "TaskDetailDTO{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", parentId=" + parentId +
            '}';
    }
}
