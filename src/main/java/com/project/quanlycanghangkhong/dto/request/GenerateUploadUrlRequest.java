package com.project.quanlycanghangkhong.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

@Data
public class GenerateUploadUrlRequest {
    
    /**
     * Tên file gốc
     */
    @NotBlank(message = "Tên file không được để trống")
    private String fileName;
    
    /**
     * Kích thước file (bytes)
     */
    @NotNull(message = "Kích thước file không được để trống")
    @Min(value = 1, message = "Kích thước file phải lớn hơn 0")
    private Long fileSize;
    
    /**
     * Loại content của file (ví dụ: image/jpeg, application/pdf)
     */
    @NotBlank(message = "Content type không được để trống")
    private String contentType;
}