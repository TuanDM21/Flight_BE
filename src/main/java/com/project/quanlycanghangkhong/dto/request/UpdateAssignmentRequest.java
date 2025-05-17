package com.project.quanlycanghangkhong.dto.request;

import java.util.Date;
import com.project.quanlycanghangkhong.dto.UserDTO;

public class UpdateAssignmentRequest {
    private String recipientType; // 'team', 'unit', 'user'
    private UserDTO recipientUser; // user nhận việc (nếu recipientType là 'user')
    private Integer recipientId; // Id của team hoặc unit nếu recipientType là 'team' hoặc 'unit'
    private Date dueAt;
    private Integer status;
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
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}
