package com.project.quanlycanghangkhong.dto;

import java.time.LocalDateTime;

/**
 * Simplified Attachment DTO - Thay thế cho nested AttachmentDTO
 * Flattened structure với basic user info thay vì nested UserDTO object  
 */
public class SimpleAttachmentDTO {
    private Integer id;
    private String filePath;
    private String fileName;
    private Long fileSize;
    private String fileType; // MIME type or extension
    private LocalDateTime createdAt;
    
    // Flattened uploadedBy user info (thay thế UserDTO uploadedBy)
    private Integer uploadedByUserId;
    private String uploadedByUserName;
    private String uploadedByUserEmail;
    
    // Optional: Additional simplified info
    private Boolean isShared;
    private Boolean isDeleted;
    
    // Constructors
    public SimpleAttachmentDTO() {}
    
    public SimpleAttachmentDTO(Integer id, String fileName, Long fileSize, LocalDateTime createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUploadedByUserId() {
        return uploadedByUserId;
    }

    public void setUploadedByUserId(Integer uploadedByUserId) {
        this.uploadedByUserId = uploadedByUserId;
    }

    public String getUploadedByUserName() {
        return uploadedByUserName;
    }

    public void setUploadedByUserName(String uploadedByUserName) {
        this.uploadedByUserName = uploadedByUserName;
    }

    public String getUploadedByUserEmail() {
        return uploadedByUserEmail;
    }

    public void setUploadedByUserEmail(String uploadedByUserEmail) {
        this.uploadedByUserEmail = uploadedByUserEmail;
    }

    public Boolean getIsShared() {
        return isShared;
    }

    public void setIsShared(Boolean isShared) {
        this.isShared = isShared;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    /**
     * Utility method to get human-readable file size
     */
    public String getFormattedFileSize() {
        if (fileSize == null) return "Unknown";
        
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024) return String.format("%.1f MB", fileSize / (1024.0 * 1024));
        return String.format("%.1f GB", fileSize / (1024.0 * 1024 * 1024));
    }
}
