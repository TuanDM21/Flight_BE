package com.project.quanlycanghangkhong.dto.simplified;

import java.time.LocalDateTime;
import java.util.List;
import com.project.quanlycanghangkhong.model.TaskPriority;
import com.project.quanlycanghangkhong.model.TaskStatus;

/**
 * Simplified TaskDetail DTO - Thay thế cho nested TaskDetailDTO
 * ✅ Flattened user information thay vì nested UserDTO objects  
 * ✅ Simplified assignments và attachments
 * ✅ Maintained depth control cho subtasks
 */
public class TaskDetailSimplifiedDTO {
    private Integer id;
    private String title;
    private String content;
    private String instructions;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TaskStatus status;
    private TaskPriority priority;
    
    // Flattened createdBy user info (thay thế UserDTO createdByUser)
    private Integer createdByUserId;
    private String createdByUserName;
    private String createdByUserEmail;
    private String createdByTeamName;
    
    // Subtask hierarchy support (giữ nguyên từ TaskDetailDTO)
    private Integer parentId;
    private List<TaskDetailSimplifiedDTO> subtasks; // Recursive nhưng đã có depth control
    private Integer hierarchyLevel;
    
    // Depth control (giữ nguyên từ TaskDetailDTO)
    public static final int MAX_SUBTASK_DEPTH = 5;
    private boolean hasMoreSubtasks;
    private int currentDepth;
    
    // Simplified collections (thay thế nested DTOs)
    private List<SimpleAssignmentDTO> assignments;
    private List<SimpleAttachmentDTO> attachments;
    
    // Constructors
    public TaskDetailSimplifiedDTO() {}

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

    public Integer getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Integer createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getCreatedByUserName() {
        return createdByUserName;
    }

    public void setCreatedByUserName(String createdByUserName) {
        this.createdByUserName = createdByUserName;
    }

    public String getCreatedByUserEmail() {
        return createdByUserEmail;
    }

    public void setCreatedByUserEmail(String createdByUserEmail) {
        this.createdByUserEmail = createdByUserEmail;
    }

    public String getCreatedByTeamName() {
        return createdByTeamName;
    }

    public void setCreatedByTeamName(String createdByTeamName) {
        this.createdByTeamName = createdByTeamName;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<TaskDetailSimplifiedDTO> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<TaskDetailSimplifiedDTO> subtasks) {
        this.subtasks = subtasks;
    }

    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public boolean isHasMoreSubtasks() {
        return hasMoreSubtasks;
    }

    public void setHasMoreSubtasks(boolean hasMoreSubtasks) {
        this.hasMoreSubtasks = hasMoreSubtasks;
    }

    public int getCurrentDepth() {
        return currentDepth;
    }

    public void setCurrentDepth(int currentDepth) {
        this.currentDepth = currentDepth;
    }

    public List<SimpleAssignmentDTO> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<SimpleAssignmentDTO> assignments) {
        this.assignments = assignments;
    }

    public List<SimpleAttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<SimpleAttachmentDTO> attachments) {
        this.attachments = attachments;
    }
    
    /**
     * Utility method to check if subtasks can be loaded at current depth
     */
    public static boolean canLoadSubtasksAtLevel(int depth) {
        return depth < MAX_SUBTASK_DEPTH;
    }
    
    /**
     * Get assignment count by status
     */
    public long getAssignmentCountByStatus(String status) {
        if (assignments == null) return 0;
        return assignments.stream()
                .filter(a -> status.equals(a.getStatus().toString()))
                .count();
    }
    
    /**
     * Get total attachments size in bytes
     */
    public long getTotalAttachmentsSize() {
        if (attachments == null) return 0;
        return attachments.stream()
                .mapToLong(a -> a.getFileSize() != null ? a.getFileSize() : 0)
                .sum();
    }
}
