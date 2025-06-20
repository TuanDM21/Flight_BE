package com.project.quanlycanghangkhong.dto;

import java.util.List;

public class CreateTaskRequest {
    private String content;
    private String instructions;
    private String notes;
    private List<AssignmentRequest> assignments;
    private List<Integer> documentIds; // Document có sẵn
    private List<CreateDocumentInTaskRequest> newDocuments; // Document mới tạo

    // Getters and setters
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

    public List<AssignmentRequest> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentRequest> assignments) {
        this.assignments = assignments;
    }

    public List<Integer> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Integer> documentIds) {
        this.documentIds = documentIds;
    }

    public List<CreateDocumentInTaskRequest> getNewDocuments() {
        return newDocuments;
    }

    public void setNewDocuments(List<CreateDocumentInTaskRequest> newDocuments) {
        this.newDocuments = newDocuments;
    }
}