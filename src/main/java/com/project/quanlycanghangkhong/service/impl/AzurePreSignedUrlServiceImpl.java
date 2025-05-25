package com.project.quanlycanghangkhong.service.impl;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.dto.response.presigned.PreSignedUrlResponse;
import com.project.quanlycanghangkhong.model.Attachment;
import com.project.quanlycanghangkhong.repository.AttachmentRepository;
import com.project.quanlycanghangkhong.service.AzurePreSignedUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AzurePreSignedUrlServiceImpl implements AzurePreSignedUrlService {
    
    @Value("${AZURE_STORAGE_CONNECTION_STRING}")
    private String connectionString;
    
    @Value("${AZURE_STORAGE_CONTAINER_NAME}")
    private String containerName;
    
    @Autowired
    private AttachmentRepository attachmentRepository;
    
    /**
     * Tạo pre-signed URL cho việc upload file
     * @param fileName Tên file gốc
     * @param fileSize Kích thước file
     * @param contentType Loại content của file
     * @return PreSignedUrlResponse chứa URL để upload và thông tin file
     */
    @Override
    public PreSignedUrlResponse generateUploadUrl(String fileName, Long fileSize, String contentType) {
        try {
            // Tạo BlobServiceClient
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
            
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            
            // Tạo tên file unique để tránh trùng lặp
            String uniqueFileName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + "_" + fileName;
            
            // Tạo BlobClient cho file mới
            BlobClient blobClient = containerClient.getBlobClient(uniqueFileName);
            
            // Tạo SAS permissions cho upload (write)
            BlobSasPermission permissions = new BlobSasPermission()
                    .setWritePermission(true)
                    .setCreatePermission(true);
            
            // Thiết lập thời gian hết hạn cho URL (15 phút)
            OffsetDateTime expiryTime = OffsetDateTime.now().plusMinutes(15);
            
            // Tạo SAS signature
            BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions)
                    .setContentType(contentType);
            
            // Tạo pre-signed URL
            String sasUrl = blobClient.getBlobUrl() + "?" + blobClient.generateSas(sasValues);
            
            // Lưu metadata vào database trước
            Attachment attachment = new Attachment();
            attachment.setFileName(fileName);
            attachment.setFilePath(blobClient.getBlobUrl()); // URL không có SAS token
            attachment.setFileSize(fileSize);
            attachment.setCreatedAt(LocalDateTime.now());
            
            Attachment savedAttachment = attachmentRepository.save(attachment);
            
            // Tạo response
            PreSignedUrlResponse response = new PreSignedUrlResponse();
            response.setUploadUrl(sasUrl);
            response.setAttachmentId(savedAttachment.getId());
            response.setFileName(fileName);
            response.setUniqueFileName(uniqueFileName);
            response.setExpiryTime(expiryTime.toLocalDateTime());
            response.setFileUrl(blobClient.getBlobUrl());
            
            return response;
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo pre-signed URL: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tạo pre-signed URL cho việc download file
     * @param attachmentId ID của attachment
     * @return URL để download file
     */
    @Override
    public String generateDownloadUrl(Integer attachmentId) {
        try {
            // Tìm attachment trong database
            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy file đính kèm"));
            
            // Tạo BlobServiceClient
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
            
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            
            // Lấy blob name từ filePath
            String blobName = extractBlobNameFromUrl(attachment.getFilePath());
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            
            // Kiểm tra file có tồn tại không
            if (!blobClient.exists()) {
                throw new RuntimeException("File không tồn tại trên Azure Blob Storage");
            }
            
            // Tạo SAS permissions cho download (read)
            BlobSasPermission permissions = new BlobSasPermission()
                    .setReadPermission(true);
            
            // Thiết lập thời gian hết hạn cho URL (1 giờ)
            OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1);
            
            // Tạo SAS signature với Content-Disposition để force download
            BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions)
                    .setContentDisposition("attachment; filename=\"" + attachment.getFileName() + "\"");
            
            // Tạo pre-signed URL cho download
            return blobClient.getBlobUrl() + "?" + blobClient.generateSas(sasValues);
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo pre-signed URL cho download: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xác nhận file đã được upload thành công
     * @param attachmentId ID của attachment
     * @return AttachmentDTO
     */
    @Override
    public AttachmentDTO confirmUpload(Integer attachmentId) {
        try {
            // Tìm attachment trong database
            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy file đính kèm"));
            
            // Tạo BlobServiceClient để kiểm tra file
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
            
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            String blobName = extractBlobNameFromUrl(attachment.getFilePath());
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            
            // Kiểm tra file có tồn tại không
            if (!blobClient.exists()) {
                // Nếu file không tồn tại, xóa record trong database
                attachmentRepository.deleteById(attachmentId);
                throw new RuntimeException("File chưa được upload hoặc upload thất bại");
            }
            
            // Cập nhật thông tin file size thực tế nếu cần
            BlobProperties properties = blobClient.getProperties();
            if (!attachment.getFileSize().equals(properties.getBlobSize())) {
                attachment.setFileSize(properties.getBlobSize());
                attachment = attachmentRepository.save(attachment);
            }
            
            // Chuyển đổi sang DTO
            return toDTO(attachment);
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xác nhận upload: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xóa file và record trong database
     * @param attachmentId ID của attachment
     */
    @Override
    public void deleteFile(Integer attachmentId) {
        try {
            // Tìm attachment trong database
            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy file đính kèm"));
            
            // Tạo BlobServiceClient
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
            
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            String blobName = extractBlobNameFromUrl(attachment.getFilePath());
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            
            // Xóa file trên Azure Blob nếu tồn tại
            if (blobClient.exists()) {
                blobClient.delete();
            }
            
            // Xóa record trong database
            attachmentRepository.deleteById(attachmentId);
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Trích xuất blob name từ URL
     * @param fileUrl URL của file
     * @return Blob name
     */
    private String extractBlobNameFromUrl(String fileUrl) {
        if (fileUrl != null && fileUrl.contains("/")) {
            return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        }
        return fileUrl;
    }
    
    /**
     * Chuyển đổi Attachment entity sang DTO
     * @param attachment Attachment entity
     * @return AttachmentDTO
     */
    private AttachmentDTO toDTO(Attachment attachment) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(attachment.getId());
        dto.setFilePath(attachment.getFilePath());
        dto.setFileName(attachment.getFileName());
        dto.setFileSize(attachment.getFileSize());
        dto.setCreatedAt(attachment.getCreatedAt());
        return dto;
    }
}