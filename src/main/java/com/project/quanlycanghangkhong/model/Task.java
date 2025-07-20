package com.project.quanlycanghangkhong.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.CascadeType;
import com.project.quanlycanghangkhong.config.VietnamTimestampListener;
import jakarta.persistence.EntityListeners;

@Entity
@Table(name = "Task")
@EntityListeners(VietnamTimestampListener.class)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status = TaskStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority = TaskPriority.NORMAL;

    // MÔ HÌNH ADJACENCY LIST: Quan hệ cha-con cho task phân cấp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // Khóa ngoại tự tham chiếu
    private Task parent;

    // MÔ HÌNH ADJACENCY LIST: Quan hệ một-nhiều cho subtask
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> subtasks = new ArrayList<>();

    // THAY ĐỔI LOGIC NGHIỆP VỤ: Quan hệ attachment trực tiếp (thay thế cách tiếp cận dựa trên document)
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attachment> attachments = new ArrayList<>();

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    /**
     * Lấy task cha cho cấu trúc phân cấp Adjacency List
     * @return Task cha hoặc null nếu đây là task gốc
     */
    public Task getParent() {
        return parent;
    }

    /**
     * Đặt task cha cho cấu trúc phân cấp Adjacency List
     * @param parent Task cha
     */
    public void setParent(Task parent) {
        this.parent = parent;
    }

    /**
     * Lấy tất cả subtask (task con) trong mô hình Adjacency List
     * @return Danh sách task con
     */
    public List<Task> getSubtasks() {
        return subtasks;
    }

    /**
     * Đặt subtask (task con) cho mô hình Adjacency List
     * @param subtasks Danh sách task con
     */
    public void setSubtasks(List<Task> subtasks) {
        this.subtasks = subtasks;
    }

    /**
     * Lấy attachment được liên kết trực tiếp với task này
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Quan hệ task-attachment trực tiếp
     * @return Danh sách attachment
     */
    public List<Attachment> getAttachments() {
        return attachments;
    }

    /**
     * Đặt attachment được liên kết trực tiếp với task này
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Thay thế quản lý file dựa trên document
     * @param attachments Danh sách attachment
     */
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}