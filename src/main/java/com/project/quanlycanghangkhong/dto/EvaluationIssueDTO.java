package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EvaluationIssueDTO {
    private Integer id;
    private Integer evaluationSessionId;
    private String targetType;
    private Integer targetId;
    private String issueContent;
    private LocalDate requestedResolutionDate;
    private Boolean isResolved;
    private LocalDate resolutionDate;
    private String notes;
    private LocalDateTime createdAt;
    private List<Integer> documentIds;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEvaluationSessionId() {
        return evaluationSessionId;
    }

    public void setEvaluationSessionId(Integer evaluationSessionId) {
        this.evaluationSessionId = evaluationSessionId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getIssueContent() {
        return issueContent;
    }

    public void setIssueContent(String issueContent) {
        this.issueContent = issueContent;
    }

    public LocalDate getRequestedResolutionDate() {
        return requestedResolutionDate;
    }

    public void setRequestedResolutionDate(LocalDate requestedResolutionDate) {
        this.requestedResolutionDate = requestedResolutionDate;
    }

    public Boolean getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }

    public LocalDate getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDate resolutionDate) {
        this.resolutionDate = resolutionDate;
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

    public List<Integer> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Integer> documentIds) {
        this.documentIds = documentIds;
    }
}