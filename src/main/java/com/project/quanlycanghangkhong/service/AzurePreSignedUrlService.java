package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.dto.response.presigned.FlexiblePreSignedUrlResponse;
import com.project.quanlycanghangkhong.dto.request.FlexibleUploadRequest;
import java.util.List;

public interface AzurePreSignedUrlService {
    
    /**
     * Tạo pre-signed URL linh hoạt cho việc upload file (single hoặc multiple)
     * @param request FlexibleUploadRequest chứa thông tin file(s) cần upload
     * @return FlexiblePreSignedUrlResponse chứa URL(s) để upload và thông tin file(s)
     */
    FlexiblePreSignedUrlResponse generateFlexibleUploadUrls(FlexibleUploadRequest request);
    
    /**
     * Tạo pre-signed URL cho việc download file
     * @param attachmentId ID của attachment trong database
     * @return URL có chữ ký để download file trực tiếp từ Azure Blob
     */
    String generateDownloadUrl(Integer attachmentId);
    
    /**
     * Xác nhận upload linh hoạt (single hoặc multiple files)
     * @param attachmentIds Danh sách ID của attachment cần xác nhận
     * @return List<AttachmentDTO> với thông tin các file đã được xác nhận
     */
    List<AttachmentDTO> confirmFlexibleUpload(List<Integer> attachmentIds);
    
    /**
     * Xóa file khỏi Azure Blob Storage và database
     * @param attachmentId ID của attachment cần xóa
     * @throws RuntimeException nếu có lỗi trong quá trình xóa
     */
    void deleteFile(Integer attachmentId);
}