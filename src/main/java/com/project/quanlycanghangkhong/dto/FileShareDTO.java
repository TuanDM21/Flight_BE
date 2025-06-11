package com.project.quanlycanghangkhong.dto;

import java.time.LocalDateTime;

public class FileShareDTO {
    private Integer id;
    private Integer attachmentId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private UserDTO sharedBy;
    private UserDTO sharedWith;
    private LocalDateTime sharedAt;
    private String note; // Luôn null (không có ghi chú)
    private boolean isActive;
    private Integer sharedCount; // Số lượng người được chia sẻ

    // Constructors
    public FileShareDTO() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Integer attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public UserDTO getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(UserDTO sharedBy) {
        this.sharedBy = sharedBy;
    }

    public UserDTO getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(UserDTO sharedWith) {
        this.sharedWith = sharedWith;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getSharedCount() {
        return sharedCount;
    }

    public void setSharedCount(Integer sharedCount) {
        this.sharedCount = sharedCount;
    }
}