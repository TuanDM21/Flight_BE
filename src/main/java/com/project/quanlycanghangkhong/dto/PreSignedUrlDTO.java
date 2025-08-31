package com.project.quanlycanghangkhong.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PreSignedUrlDTO {
    
    /**
     * URL có chữ ký để upload file trực tiếp lên Azure Blob
     */
    private String uploadUrl;
    
    /**
     * ID của attachment được tạo trong database
     */
    private Integer attachmentId;
    
    /**
     * Tên file gốc do người dùng cung cấp
     */
    private String fileName;
    
    /**
     * Tên file unique được tạo để tránh trùng lặp
     */
    private String uniqueFileName;
    
    /**
     * Kích thước file (bytes)
     */
    private Long fileSize;
    
    /**
     * Content type của file
     */
    private String contentType;
    
    /**
     * Thời gian URL hết hạn
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;
    
    /**
     * Thông báo trạng thái
     */
    private String message;
    
    /**
     * Có lỗi hay không
     */
    private boolean hasError;
}
