package com.project.quanlycanghangkhong.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "evaluation_session")
public class EvaluationSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_group_id", nullable = false)
    private EvaluationGroup evaluationGroup;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "evaluationSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationAssignment> assignments;

    @OneToMany(mappedBy = "evaluationSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationIssue> issues;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EvaluationGroup getEvaluationGroup() {
        return evaluationGroup;
    }

    public void setEvaluationGroup(EvaluationGroup evaluationGroup) {
        this.evaluationGroup = evaluationGroup;
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

    public List<EvaluationAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<EvaluationAssignment> assignments) {
        this.assignments = assignments;
    }

    public List<EvaluationIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<EvaluationIssue> issues) {
        this.issues = issues;
    }
}
