package com.project.quanlycanghangkhong.dto.response.presigned;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response cho pre-signed URL - hỗ trợ cả single và multiple files")
public class FlexiblePreSignedUrlResponse {
    
    @Schema(description = "Danh sách pre-signed URL response", required = true)
    private List<PreSignedUrlResponse> files;
    
    @Schema(description = "Thông báo kết quả", example = "Tạo thành công 3 pre-signed URL")
    private String message;
    
    // Constructors
    public FlexiblePreSignedUrlResponse() {}
    
    public FlexiblePreSignedUrlResponse(List<PreSignedUrlResponse> files) {
        this.files = files;
        this.message = generateMessage();
    }
    
    // Convenience constructor for single file
    public FlexiblePreSignedUrlResponse(PreSignedUrlResponse singleFile) {
        this.files = List.of(singleFile);
        this.message = "Tạo pre-signed URL thành công";
    }
    
    private String generateMessage() {
        int totalFiles = files != null ? files.size() : 0;
        if (totalFiles == 1) {
            return "Tạo pre-signed URL thành công";
        } else {
            return "Tạo thành công " + totalFiles + " pre-signed URL";
        }
    }
    
    // Helper methods (không hiển thị trong JSON)
    @JsonIgnore
    public boolean isSingleFile() {
        return files != null && files.size() == 1;
    }
    
    @JsonIgnore
    public Integer getTotalFiles() {
        return files != null ? files.size() : 0;
    }
    
    @JsonIgnore
    public PreSignedUrlResponse getSingleFile() {
        if (isSingleFile() && files != null && !files.isEmpty()) {
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
        this.message = generateMessage();
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}