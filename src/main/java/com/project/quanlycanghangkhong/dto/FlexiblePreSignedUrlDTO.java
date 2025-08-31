package com.project.quanlycanghangkhong.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response cho pre-signed URL - hỗ trợ cả single và multiple files")
public class FlexiblePreSignedUrlDTO {
    
    @Schema(description = "Danh sách pre-signed URL", required = true)
    private List<PreSignedUrlDTO> files;
    
    @Schema(description = "Thông báo kết quả", example = "Tạo thành công 3 pre-signed URL")
    private String message;
    
    // Constructors
    public FlexiblePreSignedUrlDTO() {}
    
    public FlexiblePreSignedUrlDTO(List<PreSignedUrlDTO> files) {
        this.files = files;
        this.message = generateMessage();
    }
    
    // Convenience constructor for single file
    public FlexiblePreSignedUrlDTO(PreSignedUrlDTO singleFile) {
        this.files = List.of(singleFile);
        this.message = "Tạo pre-signed URL thành công";
    }
    
    private String generateMessage() {
        if (files == null || files.isEmpty()) {
            return "Không có file nào được xử lý";
        }
        
        long successCount = files.stream()
            .filter(file -> !file.isHasError())
            .count();
        
        if (successCount == files.size()) {
            return String.format("Tạo thành công %d pre-signed URL", successCount);
        } else {
            long errorCount = files.size() - successCount;
            return String.format("Tạo thành công %d/%d pre-signed URL (%d lỗi)", 
                successCount, files.size(), errorCount);
        }
    }
    
    // Getters and setters
    public List<PreSignedUrlDTO> getFiles() {
        return files;
    }
    
    public void setFiles(List<PreSignedUrlDTO> files) {
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
