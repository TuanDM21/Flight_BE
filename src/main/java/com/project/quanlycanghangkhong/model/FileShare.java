package com.project.quanlycanghangkhong.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "file_shares", uniqueConstraints = @UniqueConstraint(columnNames = {"attachment_id", "shared_with_user_id"}))
public class FileShare {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // File được chia sẻ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    private Attachment attachment;
    
    // User chia sẻ file (owner)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_by_user_id", nullable = false)
    private User sharedBy;
    
    // User được chia sẻ file
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with_user_id", nullable = false)
    private User sharedWith;
        
    // Thời gian chia sẻ
    @CreationTimestamp
    @Column(name = "shared_at", nullable = false, updatable = false)
    private LocalDateTime sharedAt;
    
    // Trạng thái active
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Constructors
    public FileShare() {}
    
    public FileShare(Attachment attachment, User sharedBy, User sharedWith) {
        this.attachment = attachment;
        this.sharedBy = sharedBy;
        this.sharedWith = sharedWith;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public User getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(User sharedBy) {
        this.sharedBy = sharedBy;
    }

    public User getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(User sharedWith) {
        this.sharedWith = sharedWith;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    
    // Helper method để kiểm tra xem file share có còn valid không (đơn giản - chỉ kiểm tra active)
    public boolean isValid() {
        return isActive;
    }
}