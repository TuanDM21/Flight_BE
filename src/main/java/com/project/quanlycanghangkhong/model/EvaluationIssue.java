package com.project.quanlycanghangkhong.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "evaluation_issue")
public class EvaluationIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_period_id", nullable = false)
    private EvaluationSession evaluationSession;

    @Column(name = "target_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Integer targetId;

    @Column(name = "issue_content", nullable = false)
    private String issueContent;

    @Column(name = "requested_resolution_date")
    private LocalDate requestedResolutionDate;

    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved = false;

    @Column(name = "resolution_date")
    private LocalDate resolutionDate;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "evaluationIssue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationIssueDocument> documents;

    public enum TargetType {
        team, unit
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EvaluationSession getEvaluationSession() {
        return evaluationSession;
    }

    public void setEvaluationSession(EvaluationSession evaluationSession) {
        this.evaluationSession = evaluationSession;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
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

    public List<EvaluationIssueDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<EvaluationIssueDocument> documents) {
        this.documents = documents;
    }
}