package com.project.quanlycanghangkhong.dto.request;

import java.util.Date;
import com.project.quanlycanghangkhong.dto.UserDTO;
import com.project.quanlycanghangkhong.model.AssignmentStatus;

public class UpdateAssignmentRequest {
    private String recipientType; // 'team', 'unit', 'user'
    private UserDTO recipientUser; // user nhận việc (nếu recipientType là 'user')
    private Integer recipientId; // Id của team hoặc unit nếu recipientType là 'team' hoặc 'unit'
    private Date dueAt;
    private AssignmentStatus status;
    private String note;

    // Getter & Setter
    public String getRecipientType() {
        return recipientType;
    }
    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
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
    public Date getDueAt() {
        return dueAt;
    }
    public void setDueAt(Date dueAt) {
        this.dueAt = dueAt;
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
}
