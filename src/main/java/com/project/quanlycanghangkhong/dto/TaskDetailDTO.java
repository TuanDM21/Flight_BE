package com.project.quanlycanghangkhong.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TaskDetailDTO {
    private Integer id;
    private String content;
    private String instructions;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO createdByUser;
    private List<AssignmentDTO> assignments;
    private List<DocumentDetailDTO> documents;
    private com.project.quanlycanghangkhong.model.TaskStatus status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<DocumentDetailDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDetailDTO> documents) {
        this.documents = documents;
    }

    public com.project.quanlycanghangkhong.model.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(com.project.quanlycanghangkhong.model.TaskStatus status) {
        this.status = status;
    }
}
