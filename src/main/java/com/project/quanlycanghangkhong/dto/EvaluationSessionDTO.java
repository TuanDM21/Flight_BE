package com.project.quanlycanghangkhong.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EvaluationSessionDTO {
    private Integer id;
    private Integer evaluationGroupId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
    private LocalDateTime createdAt;
    private List<EvaluationAssignmentDTO> assignments;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEvaluationGroupId() {
        return evaluationGroupId;
    }

    public void setEvaluationGroupId(Integer evaluationGroupId) {
        this.evaluationGroupId = evaluationGroupId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public List<EvaluationAssignmentDTO> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<EvaluationAssignmentDTO> assignments) {
        this.assignments = assignments;
    }
}