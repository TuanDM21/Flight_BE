package com.project.quanlycanghangkhong.dto;

import java.time.LocalDateTime;

public class EvaluationAssignmentDTO {
    private Integer id;
    private Integer evaluationPeriodId;
    private String targetType;
    private Integer targetId;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getEvaluationPeriodId() {
        return evaluationPeriodId;
    }
    public void setEvaluationPeriodId(Integer evaluationPeriodId) {
        this.evaluationPeriodId = evaluationPeriodId;
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
