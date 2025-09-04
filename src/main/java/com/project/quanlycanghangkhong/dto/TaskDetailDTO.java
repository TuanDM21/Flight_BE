package com.project.quanlycanghangkhong.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.project.quanlycanghangkhong.model.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed task information with assignments and attachments")
public class TaskDetailDTO {
    @Schema(description = "Task ID", example = "1")
    private Integer id;

    @Schema(description = "Task title", example = "Kiểm tra hành lý")
    private String title;

    @Schema(description = "Task content/description", example = "Kiểm tra hành lý khách hàng")
    private String content;

    @Schema(description = "Task instructions", example = "Thực hiện theo quy trình ABC")
    private String instructions;

    @Schema(description = "Task notes", example = "Ghi chú bổ sung")
    private String notes;

    @Schema(description = "Creation timestamp", example = "2025-09-04T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2025-09-04T11:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created this task")
    private UserDTO createdByUser;

    @Schema(description = "List of task assignments")
    private List<AssignmentDTO> assignments;

    @Schema(description = "Task status", example = "IN_PROGRESS")
    private com.project.quanlycanghangkhong.model.TaskStatus status;

    @Schema(description = "Task priority", example = "HIGH")
    private TaskPriority priority;

    // MỚI: Hỗ trợ subtask cho mô hình Adjacency List
    @Schema(description = "Parent task ID (for subtasks)", example = "5")
    private Integer parentId; // Tham chiếu đến ID task cha

    // MỚI: Thông tin có subtask hay không
    @Schema(description = "Whether this task has subtasks", example = "true")
    private Boolean hasSubtask; // Cho biết task này có subtask con hay không

    // MỚI: Attachment trực tiếp (THAY THẾ hoàn toàn documents)
    @Schema(description = "List of attached files")
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
     * 
     * @return ID task cha, null nếu đây là task gốc
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * Đặt ID task cha cho cấu trúc phân cấp Adjacency List
     * 
     * @param parentId ID task cha
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * Kiểm tra task này có subtask con hay không
     * 
     * @return true nếu có subtask, false nếu không có
     */
    public Boolean getHasSubtask() {
        return hasSubtask;
    }

    /**
     * Đặt thông tin task này có subtask con hay không
     * 
     * @param hasSubtask true nếu có subtask, false nếu không có
     */
    public void setHasSubtask(Boolean hasSubtask) {
        this.hasSubtask = hasSubtask;
    }

    /**
     * Lấy attachment được liên kết trực tiếp với task này
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Quan hệ task-attachment trực tiếp thay thế cách
     * tiếp cận dựa trên document
     * 
     * @return Danh sách attachment được liên kết trực tiếp với task
     */
    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    /**
     * Đặt attachment được liên kết trực tiếp với task này
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Thay thế quản lý file dựa trên document cũ
     * 
     * @param attachments Danh sách attachment để liên kết với task
     */
    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
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
