package com.project.quanlycanghangkhong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Schema(description = "Request linh hoạt cho upload file - hỗ trợ cả single và multiple files")
public class FlexibleUploadRequest {
    
    @NotEmpty(message = "Danh sách file không được rỗng")
    @Size(max = 10, message = "Không thể upload quá 10 file cùng lúc")
    @Valid
    @Schema(description = "Danh sách file cần upload", required = true)
    private List<FileUploadInfo> files;
    
    // Constructors
    public FlexibleUploadRequest() {}
    
    public FlexibleUploadRequest(List<FileUploadInfo> files) {
        this.files = files;
    }
    
    // Convenience constructor for single file
    public FlexibleUploadRequest(String fileName, Long fileSize, String contentType) {
        this.files = List.of(new FileUploadInfo(fileName, fileSize, contentType));
    }
    
    // Helper methods
    public boolean isSingleFile() {
        return files != null && files.size() == 1;
    }
    
    public boolean isMultipleFiles() {
        return files != null && files.size() > 1;
    }
    
    public FileUploadInfo getSingleFile() {
        if (isSingleFile()) {
            return files.get(0);
        }
        throw new IllegalStateException("Request không phải single file");
    }
    
    // Getters and Setters
    public List<FileUploadInfo> getFiles() {
        return files;
    }
    
    public void setFiles(List<FileUploadInfo> files) {
        this.files = files;
    }
    
    // Inner class for file info
    @Schema(description = "Thông tin file upload")
    public static class FileUploadInfo {
        
        @javax.validation.constraints.NotBlank(message = "Tên file không được rỗng")
        @Schema(description = "Tên file gốc", example = "document.pdf", required = true)
        private String fileName;
        
        @javax.validation.constraints.NotNull(message = "Kích thước file không được null")
        @javax.validation.constraints.Min(value = 1, message = "Kích thước file phải lớn hơn 0")
        @Schema(description = "Kích thước file (bytes)", example = "1024000", required = true)
        private Long fileSize;
        
        @javax.validation.constraints.NotBlank(message = "Content type không được rỗng")
        @Schema(description = "Loại content của file", example = "application/pdf", required = true)
        private String contentType;
        
        // Constructors
        public FileUploadInfo() {}
        
        public FileUploadInfo(String fileName, Long fileSize, String contentType) {
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.contentType = contentType;
        }
        
        // Getters and Setters
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
        
        public String getContentType() {
            return contentType;
        }
        
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }
}