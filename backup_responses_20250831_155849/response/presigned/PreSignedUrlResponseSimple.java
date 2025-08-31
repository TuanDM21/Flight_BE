package com.project.quanlycanghangkhong.dto.response.presigned;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor  
@AllArgsConstructor
public class PreSignedUrlResponseSimple {
    
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
     * Thời gian hết hạn của pre-signed URL
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryTime;
    
    /**
     * URL công khai của file (không có SAS token)
     */
    private String fileUrl;
}
