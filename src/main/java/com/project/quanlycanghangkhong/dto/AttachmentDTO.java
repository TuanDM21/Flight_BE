package com.project.quanlycanghangkhong.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Attachment data transfer object")
public class AttachmentDTO {
    @Schema(description = "Attachment ID", example = "1")
    private Integer id;

    @Schema(description = "File path", example = "/uploads/documents/file.pdf")
    private String filePath;

    @Schema(description = "Original file name", example = "document.pdf")
    private String fileName;

    @Schema(description = "File size in bytes", example = "1024000")
    private Long fileSize;

    @Schema(description = "Upload timestamp", example = "2025-09-04T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "User who uploaded the file")
    private UserDTO uploadedBy;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(UserDTO uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
}
