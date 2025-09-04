package com.project.quanlycanghangkhong.dto;

import java.util.Date;
import com.project.quanlycanghangkhong.model.AssignmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Assignment information for tasks")
public class AssignmentDTO {
    @Schema(description = "Assignment ID", example = "1")
    private Integer assignmentId;

    @Schema(description = "Task ID", example = "5")
    private Integer taskId;

    @Schema(description = "Recipient type", example = "USER", allowableValues = { "USER", "TEAM", "UNIT" })
    private String recipientType; // 'team', 'unit', 'user'

    @Schema(description = "Assignment timestamp", example = "2025-09-04T10:30:00")
    private Date assignedAt;

    @Schema(description = "Due date", example = "2025-09-05T18:00:00")
    private Date dueAt;

    @Schema(description = "Completion timestamp", example = "2025-09-05T16:30:00")
    private Date completedAt;

    @Schema(description = "Assignment status", example = "IN_PROGRESS")
    private AssignmentStatus status;

    @Schema(description = "Assignment note", example = "Urgent task")
    private String note;

    @Schema(description = "User who assigned the task")
    private UserDTO assignedByUser;

    @Schema(description = "User who completed the task")
    private UserDTO completedByUser;

    @Schema(description = "Recipient user (if recipientType = USER)")
    private UserDTO recipientUser;

    @Schema(description = "Recipient ID (team or unit ID)", example = "10")
    private Integer recipientId; // Id của team hoặc unit nếu recipientType là 'team' hoặc 'unit'

    // Additional fields for team/unit information
    @Schema(description = "Team name (if recipientType = TEAM)", example = "Security Team")
    private String recipientTeamName; // Tên team nếu recipientType = 'team'

    @Schema(description = "Unit name (if recipientType = UNIT)", example = "Airport Operations")
    private String recipientUnitName; // Tên unit nếu recipientType = 'unit'

    @Schema(description = "Team leader (if recipientType = TEAM)")
    private UserDTO recipientTeamLead; // Team lead nếu recipientType = 'team'

    @Schema(description = "Unit leader (if recipientType = UNIT)")
    private UserDTO recipientUnitLead; // Unit lead nếu recipientType = 'unit'

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

    public Date getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Date assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Date getDueAt() {
        return dueAt;
    }

    public void setDueAt(Date dueAt) {
        this.dueAt = dueAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public UserDTO getAssignedByUser() {
        return assignedByUser;
    }

    public void setAssignedByUser(UserDTO assignedByUser) {
        this.assignedByUser = assignedByUser;
    }

    public UserDTO getCompletedByUser() {
        return completedByUser;
    }

    public void setCompletedByUser(UserDTO completedByUser) {
        this.completedByUser = completedByUser;
    }

    public UserDTO getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(UserDTO recipientUser) {
        this.recipientUser = recipientUser;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientTeamName() {
        return recipientTeamName;
    }

    public void setRecipientTeamName(String recipientTeamName) {
        this.recipientTeamName = recipientTeamName;
    }

    public String getRecipientUnitName() {
        return recipientUnitName;
    }

    public void setRecipientUnitName(String recipientUnitName) {
        this.recipientUnitName = recipientUnitName;
    }

    public UserDTO getRecipientTeamLead() {
        return recipientTeamLead;
    }

    public void setRecipientTeamLead(UserDTO recipientTeamLead) {
        this.recipientTeamLead = recipientTeamLead;
    }

    public UserDTO getRecipientUnitLead() {
        return recipientUnitLead;
    }

    public void setRecipientUnitLead(UserDTO recipientUnitLead) {
        this.recipientUnitLead = recipientUnitLead;
    }
}
