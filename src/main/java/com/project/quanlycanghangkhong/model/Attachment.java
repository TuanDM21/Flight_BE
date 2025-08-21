package com.project.quanlycanghangkhong.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import com.project.quanlycanghangkhong.config.VietnamTimestampListener;

@Entity
@Table(name = "attachment")
@EntityListeners(VietnamTimestampListener.class)
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = true) // Cho phép null để upload file rời
    private Document document;

    // THAY ĐỔI LOGIC NGHIỆP VỤ: Quan hệ task trực tiếp cho quy trình làm việc dựa trên attachment mới
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = true) // Liên kết task-attachment trực tiếp
    private Task task;

    // ✅ THÊM: 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    private User uploadedBy;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    // ❌ XÓA @CreationTimestamp - sẽ dùng VietnamTimestampListener thay thế
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ✅ THÊM updatedAt field với nullable = true và @UpdateTimestamp
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    /**
     * Lấy task được liên kết trực tiếp với attachment này
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Quan hệ task-attachment trực tiếp
     * @return Task được liên kết hoặc null nếu không được gán cho task nào
     */
    public Task getTask() {
        return task;
    }

    /**
     * Đặt task được liên kết trực tiếp với attachment này
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Cho phép gán task-attachment trực tiếp
     * @param task Task để liên kết với attachment này
     */
    public void setTask(Task task) {
        this.task = task;
    }
}