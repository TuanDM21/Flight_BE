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
import com.project.quanlycanghangkhong.dto.response.presigned.FlexiblePreSignedUrlResponse;
import com.project.quanlycanghangkhong.dto.request.FlexibleUploadRequest;
import com.project.quanlycanghangkhong.model.Attachment;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.AttachmentRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.repository.FileShareRepository;
import com.project.quanlycanghangkhong.model.FileShare;
import com.project.quanlycanghangkhong.service.AzurePreSignedUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Service
public class AzurePreSignedUrlServiceImpl implements AzurePreSignedUrlService {
    
    private static final Logger logger = LoggerFactory.getLogger(AzurePreSignedUrlServiceImpl.class);
    
    @Value("${AZURE_STORAGE_CONNECTION_STRING}")
    private String connectionString;
    
    @Value("${AZURE_STORAGE_CONTAINER_NAME}")
    private String containerName;
    
    @Autowired
    private AttachmentRepository attachmentRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileShareRepository fileShareRepository;

    /**
     * Lấy thông tin user hiện tại từ SecurityContext
     * @return User hiện tại hoặc null nếu không tìm thấy
     */
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName() != null) {
                String email = authentication.getName();
                return userRepository.findByEmail(email).orElse(null);
            }
        } catch (Exception e) {
            logger.error("Error getting current user", e);
        }
        return null;
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
     * Xóa file và record trong database
     * @param attachmentId ID của attachment
     */
    @Override
    @Transactional
    public void deleteFile(Integer attachmentId) {
        try {
            // Tìm attachment trong database
            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy file đính kèm"));
            
            // 🔒 CHỈ KIỂM TRA OWNER
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
            }
            
            boolean isOwner = attachment.getUploadedBy() != null && 
                attachment.getUploadedBy().getId().equals(currentUser.getId());
            
            if (!isOwner) {
                throw new RuntimeException("Bạn không có quyền xóa file này. Chỉ người upload file mới có thể thực hiện.");
            }
            
            logger.info("User {} (ID: {}) deleting attachment {} - File: {}", 
                currentUser.getEmail(), currentUser.getId(), attachmentId, attachment.getFileName());
            
            // 🔥 BƯỚC 1: XÓA TẤT CẢ FILE SHARES (CẢ ACTIVE VÀ INACTIVE) LIÊN QUAN TRƯỚC
            List<FileShare> allFileShares = fileShareRepository.findByAttachment(attachment);
            if (!allFileShares.isEmpty()) {
                logger.info("Deleting {} file shares (active and inactive) for attachment {}", allFileShares.size(), attachmentId);
                fileShareRepository.deleteAll(allFileShares);
                logger.info("Successfully deleted all file shares for attachment {}", attachmentId);
            }
            
            // 🔥 BƯỚC 2: XÓA FILE TRÊN AZURE BLOB STORAGE
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
                logger.info("Successfully deleted blob file: {}", blobName);
            }
            
            // 🔥 BƯỚC 3: XÓA ATTACHMENT RECORD TRONG DATABASE
            attachmentRepository.deleteById(attachmentId);
            logger.info("Successfully deleted attachment record with ID: {}", attachmentId);
            
        } catch (Exception e) {
            logger.error("Error deleting file with ID: " + attachmentId, e);
            throw new RuntimeException("Lỗi khi xóa file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tạo pre-signed URL linh hoạt cho việc upload file (single hoặc multiple)
     * @param request FlexibleUploadRequest chứa thông tin file(s) cần upload
     * @return FlexiblePreSignedUrlResponse chứa URL(s) để upload và thông tin file(s)
     */
    @Override
    public FlexiblePreSignedUrlResponse generateFlexibleUploadUrls(FlexibleUploadRequest request) {
        // Validate files before processing
        try {
            request.validateFiles();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation failed: " + e.getMessage(), e);
        }
        
        if (request.isSingleFile()) {
            // Optimized path for single file
            FlexibleUploadRequest.FileUploadInfo fileInfo = request.getSingleFile();
            try {
                PreSignedUrlResponse response = createPreSignedUrlForFile(
                    fileInfo.getFileName(),
                    fileInfo.getFileSize(),
                    fileInfo.getContentType()
                );
                return new FlexiblePreSignedUrlResponse(response);
            } catch (Exception e) {
                logger.error("Error generating upload URL for single file: " + fileInfo.getFileName(), e);
                throw new RuntimeException("Lỗi tạo URL cho file: " + e.getMessage());
            }
        } else {
            // Batch processing for multiple files
            List<PreSignedUrlResponse> results = new ArrayList<>();
            
            for (FlexibleUploadRequest.FileUploadInfo fileInfo : request.getFiles()) {
                try {
                    PreSignedUrlResponse response = createPreSignedUrlForFile(
                        fileInfo.getFileName(),
                        fileInfo.getFileSize(),
                        fileInfo.getContentType()
                    );
                    results.add(response);
                } catch (Exception e) {
                    logger.error("Error generating upload URL for file: " + fileInfo.getFileName(), e);
                    
                    // Create error response for this file
                    PreSignedUrlResponse errorResponse = new PreSignedUrlResponse();
                    errorResponse.setFileName(fileInfo.getFileName());
                    errorResponse.setError("Lỗi tạo URL: " + e.getMessage());
                    results.add(errorResponse);
                }
            }
            
            return new FlexiblePreSignedUrlResponse(results);
        }
    }

    /**
     * Tạo pre-signed URL cho một file cụ thể (private helper method)
     * @param fileName Tên file gốc
     * @param fileSize Kích thước file
     * @param contentType Loại content của file
     * @return PreSignedUrlResponse chứa URL để upload và thông tin file
     */
    private PreSignedUrlResponse createPreSignedUrlForFile(String fileName, Long fileSize, String contentType) {
        try {
            // Lấy thông tin user hiện tại
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                throw new RuntimeException("Không thể xác định user hiện tại. Vui lòng đăng nhập lại.");
            }
            
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
            
            // Lưu metadata vào database trước với owner
            Attachment attachment = new Attachment();
            attachment.setFileName(fileName);
            attachment.setFilePath(blobClient.getBlobUrl()); // URL không có SAS token
            attachment.setFileSize(fileSize);
            attachment.setCreatedAt(LocalDateTime.now());
            attachment.setUploadedBy(currentUser); // 🔥 SET OWNER
            
            logger.info("Creating attachment for user: {} (ID: {}) - File: {}", 
                currentUser.getEmail(), currentUser.getId(), fileName);
            
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
     * Xác nhận upload linh hoạt (single hoặc multiple files)
     * @param attachmentIds Danh sách ID của attachment cần xác nhận
     * @return List<AttachmentDTO> với thông tin các file đã được xác nhận
     */
    @Override
    public List<AttachmentDTO> confirmFlexibleUpload(List<Integer> attachmentIds) {
        if (attachmentIds.size() == 1) {
            // Optimized path for single file
            try {
                AttachmentDTO confirmed = confirmSingleUpload(attachmentIds.get(0));
                return List.of(confirmed);
            } catch (Exception e) {
                logger.error("Error confirming upload for attachment ID: " + attachmentIds.get(0), e);
                throw new RuntimeException("Lỗi xác nhận upload: " + e.getMessage());
            }
        } else {
            // Batch processing for multiple files
            List<AttachmentDTO> results = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            for (Integer attachmentId : attachmentIds) {
                try {
                    AttachmentDTO confirmed = confirmSingleUpload(attachmentId);
                    results.add(confirmed);
                } catch (Exception e) {
                    logger.error("Error confirming upload for attachment ID: " + attachmentId, e);
                    errors.add("AttachmentId " + attachmentId + ": " + e.getMessage());
                }
            }
            
            if (!errors.isEmpty() && results.isEmpty()) {
                throw new RuntimeException("Không thể xác nhận upload cho bất kỳ file nào: " + String.join(", ", errors));
            }
            
            if (!errors.isEmpty()) {
                logger.warn("Some files failed to confirm upload: " + String.join(", ", errors));
            }
            
            return results;
        }
    }

    /**
     * Xác nhận upload cho một file cụ thể (private helper method)
     * @param attachmentId ID của attachment
     * @return AttachmentDTO
     */
    private AttachmentDTO confirmSingleUpload(Integer attachmentId) {
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
        
        // Map owner information
        if (attachment.getUploadedBy() != null) {
            com.project.quanlycanghangkhong.dto.UserDTO ownerDto = new com.project.quanlycanghangkhong.dto.UserDTO();
            ownerDto.setId(attachment.getUploadedBy().getId());
            ownerDto.setName(attachment.getUploadedBy().getName());
            ownerDto.setEmail(attachment.getUploadedBy().getEmail());
            dto.setUploadedBy(ownerDto);
        }
        
        return dto;
    }
}