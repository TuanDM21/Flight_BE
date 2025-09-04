package com.project.quanlycanghangkhong.dto;

import java.util.List;
import java.util.ArrayList;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO cho Task với cấu trúc phân cấp nested - dễ dàng render tree view trong
 * frontend
 * Bao gồm đầy đủ thông tin như TaskDetailDTO
 */
@Schema(description = "Task with hierarchical tree structure (nested representation)")
public class TaskTreeDTO {
    @Schema(description = "Task ID", example = "1")
    private Integer id;

    @Schema(description = "Task title", example = "Kiểm tra hành lý")
    private String title;

    @Schema(description = "Task content/description", example = "Kiểm tra hành lý khách hàng")
    private String content;

    @Schema(description = "Task instructions", example = "Thực hiện theo quy trình ABC")
    private String instructions; // Thêm instructions

    @Schema(description = "Task notes", example = "Ghi chú bổ sung")
    private String notes; // Thêm notes

    @Schema(description = "Task priority", example = "HIGH")
    private String priority;

    @Schema(description = "Task status", example = "IN_PROGRESS")
    private String status;

    @Schema(description = "Creation timestamp", example = "2025-09-04T10:30:00")
    private String createdAt;

    @Schema(description = "Last update timestamp", example = "2025-09-04T11:00:00")
    private String updatedAt;

    @Schema(description = "Parent task ID", example = "5")
    private Integer parentId;

    @Schema(description = "Depth level in tree (0 = root)", example = "1")
    private Integer level; // Depth level trong tree (0 = root, 1 = level 1, etc.)

    // Thêm thông tin người tạo và assignments, attachments
    @Schema(description = "User who created this task")
    private UserDTO createdByUser;

    @Schema(description = "List of task assignments")
    private List<AssignmentDTO> assignments = new ArrayList<>();

    @Schema(description = "List of attached files")
    private List<AttachmentDTO> attachments = new ArrayList<>();

    // Nested structure - chứa trực tiếp các subtask
    @Schema(description = "List of nested subtasks")
    private List<TaskTreeDTO> subtasks = new ArrayList<>();

    // Metadata cho frontend
    private Integer totalSubtasks; // Tổng số subtask (bao gồm cả nested)
    private Boolean hasSubtasks; // Có subtask hay không

    // Constructor rỗng
    public TaskTreeDTO() {
    }

    /**
     * Constructor từ TaskDetailDTO - bao gồm đầy đủ thông tin
     */
    public TaskTreeDTO(TaskDetailDTO taskDetail, Integer level) {
        this.id = taskDetail.getId();
        this.title = taskDetail.getTitle();
        this.content = taskDetail.getContent();
        this.instructions = taskDetail.getInstructions();
        this.notes = taskDetail.getNotes();
        this.priority = taskDetail.getPriority() != null ? taskDetail.getPriority().toString() : null;
        this.status = taskDetail.getStatus() != null ? taskDetail.getStatus().toString() : null;
        this.createdAt = taskDetail.getCreatedAt() != null ? taskDetail.getCreatedAt().toString() : null;
        this.updatedAt = taskDetail.getUpdatedAt() != null ? taskDetail.getUpdatedAt().toString() : null;
        this.parentId = taskDetail.getParentId();
        this.level = level;

        // Copy user, assignments, attachments
        this.createdByUser = taskDetail.getCreatedByUser();
        this.assignments = taskDetail.getAssignments() != null ? taskDetail.getAssignments() : new ArrayList<>();
        this.attachments = taskDetail.getAttachments() != null ? taskDetail.getAttachments() : new ArrayList<>();

        this.subtasks = new ArrayList<>();
        this.hasSubtasks = false;
        this.totalSubtasks = 0;
    }

    // Getters and Setters
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

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<TaskTreeDTO> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<TaskTreeDTO> subtasks) {
        this.subtasks = subtasks;
        this.hasSubtasks = subtasks != null && !subtasks.isEmpty();
        this.totalSubtasks = calculateTotalSubtasks();
    }

    public void addSubtask(TaskTreeDTO subtask) {
        if (this.subtasks == null) {
            this.subtasks = new ArrayList<>();
        }
        this.subtasks.add(subtask);
        this.hasSubtasks = true;
        this.totalSubtasks = calculateTotalSubtasks();
    }

    public Integer getTotalSubtasks() {
        return totalSubtasks;
    }

    public void setTotalSubtasks(Integer totalSubtasks) {
        this.totalSubtasks = totalSubtasks;
    }

    public Boolean getHasSubtasks() {
        return hasSubtasks;
    }

    public void setHasSubtasks(Boolean hasSubtasks) {
        this.hasSubtasks = hasSubtasks;
    }

    /**
     * Tính tổng số subtask (bao gồm cả nested)
     */
    private Integer calculateTotalSubtasks() {
        if (subtasks == null || subtasks.isEmpty()) {
            return 0;
        }

        int total = subtasks.size();
        for (TaskTreeDTO subtask : subtasks) {
            total += subtask.getTotalSubtasks();
        }
        return total;
    }

    /**
     * Helper method để tìm task theo ID trong tree
     */
    public TaskTreeDTO findTaskById(Integer taskId) {
        if (this.id.equals(taskId)) {
            return this;
        }

        if (subtasks != null) {
            for (TaskTreeDTO subtask : subtasks) {
                TaskTreeDTO found = subtask.findTaskById(taskId);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }
}
