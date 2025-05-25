package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.dto.response.presigned.PreSignedUrlResponse;

public interface AzurePreSignedUrlService {
    
    /**
     * Tạo pre-signed URL cho việc upload file
     * @param fileName Tên file gốc
     * @param fileSize Kích thước file (bytes)
     * @param contentType Loại content của file (ví dụ: image/jpeg, application/pdf)
     * @return PreSignedUrlResponse chứa URL để upload và thông tin file
     */
    PreSignedUrlResponse generateUploadUrl(String fileName, Long fileSize, String contentType);
    
    /**
     * Tạo pre-signed URL cho việc download file
     * @param attachmentId ID của attachment trong database
     * @return URL có chữ ký để download file trực tiếp từ Azure Blob
     */
    String generateDownloadUrl(Integer attachmentId);
    
    /**
     * Xác nhận file đã được upload thành công
     * Kiểm tra file có tồn tại trên Azure Blob không và cập nhật thông tin
     * @param attachmentId ID của attachment cần xác nhận
     * @return AttachmentDTO với thông tin đã cập nhật
     * @throws RuntimeException nếu file không tồn tại hoặc upload thất bại
     */
    AttachmentDTO confirmUpload(Integer attachmentId);
    
    /**
     * Xóa file khỏi Azure Blob Storage và database
     * @param attachmentId ID của attachment cần xóa
     * @throws RuntimeException nếu có lỗi trong quá trình xóa
     */
    void deleteFile(Integer attachmentId);
}