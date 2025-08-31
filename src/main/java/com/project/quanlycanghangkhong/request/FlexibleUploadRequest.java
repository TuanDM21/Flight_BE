package com.project.quanlycanghangkhong.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Schema(description = "Request để upload file - hỗ trợ cả single và multiple files")
public class FlexibleUploadRequest {
    
    // Allowed content types for security
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "application/pdf",
        "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "image/jpeg",
        "image/png", 
        "image/gif",
        "text/plain"
    );
    
    // Max file size: 50MB
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;
    
    @NotEmpty(message = "Danh sách file không được rỗng")
    @Size(max = 10, message = "Không thể upload quá 10 file cùng lúc")
    @Valid
    @Schema(description = "Danh sách file cần upload", required = true, example = "[{\"fileName\": \"document.pdf\", \"fileSize\": 1024000, \"contentType\": \"application/pdf\"}]")
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
    
    // Helper methods (không hiển thị trong JSON)
    @JsonIgnore
    public boolean isSingleFile() {
        return files != null && files.size() == 1;
    }
    
    @JsonIgnore
    public boolean isMultipleFiles() {
        return files != null && files.size() > 1;
    }
    
    @JsonIgnore
    public FileUploadInfo getSingleFile() {
        if (isSingleFile()) {
            return files.get(0);
        }
        throw new IllegalStateException("Request không phải single file");
    }
    
    // Helper method to validate all files
    public void validateFiles() {
        if (files != null) {
            for (FileUploadInfo file : files) {
                file.validate();
            }
        }
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
        
        // Custom validation method
        public void validate() {
            // Validate content type
            if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
                throw new IllegalArgumentException(
                    "Content type không được phép: " + contentType + 
                    ". Chỉ chấp nhận: " + String.join(", ", ALLOWED_CONTENT_TYPES)
                );
            }
            
            // Validate file size
            if (fileSize > MAX_FILE_SIZE) {
                throw new IllegalArgumentException(
                    "File quá lớn: " + fileSize + " bytes. Tối đa cho phép: " + MAX_FILE_SIZE + " bytes"
                );
            }
            
            // Validate file extension matches content type
            String extension = getFileExtension();
            if (!isValidExtensionForContentType(extension, contentType)) {
                throw new IllegalArgumentException(
                    "Extension file không khớp với content type: " + extension + " vs " + contentType
                );
            }
        }
        
        private String getFileExtension() {
            if (fileName != null && fileName.contains(".")) {
                return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            }
            return "";
        }
        
        private boolean isValidExtensionForContentType(String extension, String contentType) {
            return switch (contentType) {
                case "application/pdf" -> extension.equals(".pdf");
                case "application/msword" -> extension.equals(".doc");
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> extension.equals(".docx");
                case "application/vnd.ms-excel" -> extension.equals(".xls");
                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> extension.equals(".xlsx");
                case "image/jpeg" -> extension.equals(".jpg") || extension.equals(".jpeg");
                case "image/png" -> extension.equals(".png");
                case "image/gif" -> extension.equals(".gif");
                case "text/plain" -> extension.equals(".txt");
                default -> false;
            };
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