package com.project.quanlycanghangkhong.dto;

import java.time.LocalDateTime;
import com.project.quanlycanghangkhong.model.AssignmentStatus;

/**
 * Simplified Assignment DTO - Thay thế cho nested AssignmentDTO  
 * Flattened structure với basic user info thay vì nested UserDTO objects
 */
public class SimpleAssignmentDTO {
    private Integer assignmentId;
    private Integer taskId;
    private String recipientType; // 'team', 'unit', 'user'
    private Integer recipientId;
    private LocalDateTime assignedAt;
    private LocalDateTime dueAt;
    private LocalDateTime completedAt;
    private AssignmentStatus status;
    private String note;
    
    // Flattened assignedBy user info (thay thế UserDTO assignedByUser)
    private Integer assignedByUserId;
    private String assignedByUserName;
    private String assignedByUserEmail;
    
    // Flattened completedBy user info (thay thế UserDTO completedByUser)
    private Integer completedByUserId;
    private String completedByUserName;
    private String completedByUserEmail;
    
    // Flattened recipient user info (thay thế UserDTO recipientUser) - chỉ khi recipientType = 'user'
    private String recipientUserName;
    private String recipientUserEmail;
    
    // Constructors
    public SimpleAssignmentDTO() {}

    // Getters and Setters
    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getAssignedByUserId() {
        return assignedByUserId;
    }

    public void setAssignedByUserId(Integer assignedByUserId) {
        this.assignedByUserId = assignedByUserId;
    }

    public String getAssignedByUserName() {
        return assignedByUserName;
    }

    public void setAssignedByUserName(String assignedByUserName) {
        this.assignedByUserName = assignedByUserName;
    }

    public String getAssignedByUserEmail() {
        return assignedByUserEmail;
    }

    public void setAssignedByUserEmail(String assignedByUserEmail) {
        this.assignedByUserEmail = assignedByUserEmail;
    }

    public Integer getCompletedByUserId() {
        return completedByUserId;
    }

    public void setCompletedByUserId(Integer completedByUserId) {
        this.completedByUserId = completedByUserId;
    }

    public String getCompletedByUserName() {
        return completedByUserName;
    }

    public void setCompletedByUserName(String completedByUserName) {
        this.completedByUserName = completedByUserName;
    }

    public String getCompletedByUserEmail() {
        return completedByUserEmail;
    }

    public void setCompletedByUserEmail(String completedByUserEmail) {
        this.completedByUserEmail = completedByUserEmail;
    }

    public String getRecipientUserName() {
        return recipientUserName;
    }

    public void setRecipientUserName(String recipientUserName) {
        this.recipientUserName = recipientUserName;
    }

    public String getRecipientUserEmail() {
        return recipientUserEmail;
    }

    public void setRecipientUserEmail(String recipientUserEmail) {
        this.recipientUserEmail = recipientUserEmail;
    }
}
