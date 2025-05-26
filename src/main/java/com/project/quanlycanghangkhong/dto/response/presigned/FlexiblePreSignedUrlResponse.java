package com.project.quanlycanghangkhong.dto.response.presigned;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response linh hoạt cho pre-signed URL - hỗ trợ cả single và multiple files")
public class FlexiblePreSignedUrlResponse {
    
    @Schema(description = "Danh sách pre-signed URL response", required = true)
    private List<PreSignedUrlResponse> files;
    
    @Schema(description = "Tổng số file được tạo URL", example = "3")
    private Integer totalFiles;
    
    @Schema(description = "Thông báo kết quả", example = "Tạo thành công 3 pre-signed URL")
    private String message;
    
    @Schema(description = "Có phải là single file hay không", example = "false")
    private boolean isSingleFile;
    
    // Constructors
    public FlexiblePreSignedUrlResponse() {}
    
    public FlexiblePreSignedUrlResponse(List<PreSignedUrlResponse> files) {
        this.files = files;
        this.totalFiles = files != null ? files.size() : 0;
        this.isSingleFile = totalFiles == 1;
        this.message = generateMessage();
    }
    
    // Convenience constructor for single file
    public FlexiblePreSignedUrlResponse(PreSignedUrlResponse singleFile) {
        this.files = List.of(singleFile);
        this.totalFiles = 1;
        this.isSingleFile = true;
        this.message = "Tạo pre-signed URL thành công";
    }
    
    private String generateMessage() {
        if (isSingleFile) {
            return "Tạo pre-signed URL thành công";
        } else {
            return "Tạo thành công " + totalFiles + " pre-signed URL";
        }
    }
    
    // Helper methods for single file access
    @JsonIgnore  // Ignore this method during JSON serialization
    public PreSignedUrlResponse getSingleFile() {
        if (isSingleFile && files != null && !files.isEmpty()) {
            return files.get(0);
        }
        throw new IllegalStateException("Response không phải single file");
    }
    
    // Getters and Setters
    public List<PreSignedUrlResponse> getFiles() {
        return files;
    }
    
    public void setFiles(List<PreSignedUrlResponse> files) {
        this.files = files;
        this.totalFiles = files != null ? files.size() : 0;
        this.isSingleFile = totalFiles == 1;
        this.message = generateMessage();
    }
    
    public Integer getTotalFiles() {
        return totalFiles;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isSingleFile() {
        return isSingleFile;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}