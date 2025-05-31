package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.service.AttachmentService;
import com.project.quanlycanghangkhong.service.AzurePreSignedUrlService;
import com.project.quanlycanghangkhong.dto.request.UpdateAttachmentFileNameRequest;
import com.project.quanlycanghangkhong.dto.request.FlexibleUploadRequest;
import com.project.quanlycanghangkhong.dto.request.ConfirmFlexibleUploadRequest;
import com.project.quanlycanghangkhong.dto.response.presigned.FlexiblePreSignedUrlResponse;
// Import các response classes chuyên biệt cho attachment
import com.project.quanlycanghangkhong.dto.response.attachment.ApiGenerateUploadUrlsResponse;
import com.project.quanlycanghangkhong.dto.response.attachment.ApiConfirmUploadResponse;
import com.project.quanlycanghangkhong.dto.response.attachment.ApiDownloadUrlResponse;
import com.project.quanlycanghangkhong.dto.response.attachment.ApiAttachmentResponse;
import com.project.quanlycanghangkhong.dto.response.attachment.ApiAttachmentListResponse;
import com.project.quanlycanghangkhong.dto.response.attachment.ApiUpdateAttachmentResponse;
import com.project.quanlycanghangkhong.dto.response.attachment.ApiDeleteAttachmentResponse;
import com.project.quanlycanghangkhong.dto.response.attachment.ApiBulkDeleteAttachmentResponse;
import com.project.quanlycanghangkhong.service.FileShareService;
import com.project.quanlycanghangkhong.dto.FileShareDTO;
import com.project.quanlycanghangkhong.dto.request.ShareFileRequest;
import com.project.quanlycanghangkhong.dto.request.UpdateFileShareRequest;
import com.project.quanlycanghangkhong.dto.response.fileshare.ApiShareFileResponse;
import com.project.quanlycanghangkhong.dto.response.fileshare.ApiFileShareListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/attachments")
@CrossOrigin(origins = "*")
@Tag(name = "Azure Pre-signed URL File Management", description = "APIs quản lý file đính kèm sử dụng Azure Pre-signed URL")
public class AttachmentController {
    
    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);
    
    @Autowired
    private AttachmentService attachmentService;
    
    @Autowired
    private AzurePreSignedUrlService preSignedUrlService;

    @Autowired
    private FileShareService fileShareService;

    // ==================== FLEXIBLE PRE-SIGNED URL ENDPOINTS ====================
    
    @PostMapping("/generate-upload-urls")
    @Operation(summary = "Tạo pre-signed URL để upload file", 
               description = "Tạo pre-signed URL để client upload file trực tiếp lên Azure Blob Storage. " +
                            "Tự động detect và xử lý cả single file (1 file) và multiple files (nhiều file)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo pre-signed URL thành công", 
                    content = @Content(schema = @Schema(implementation = ApiGenerateUploadUrlsResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi tạo pre-signed URL")
    })
    public ResponseEntity<ApiGenerateUploadUrlsResponse> generateUploadUrls(
            @Valid @RequestBody FlexibleUploadRequest request) {
        try {
            // Log upload request for monitoring
            logger.info("Generating upload URLs for {} files", request.getFiles().size());
            
            FlexiblePreSignedUrlResponse result = preSignedUrlService.generateFlexibleUploadUrls(request);
            
            // Log success
            logger.info("Successfully generated {} upload URLs", result.getTotalFiles());
            
            ApiGenerateUploadUrlsResponse response = new ApiGenerateUploadUrlsResponse();
            response.setMessage(result.getMessage());
            response.setStatusCode(200);
            response.setData(result);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error in upload URL generation: {}", e.getMessage());
            return ResponseEntity.status(400).body(
                new ApiGenerateUploadUrlsResponse(e.getMessage(), 400, null, false)
            );
        } catch (Exception e) {
            logger.error("Error generating upload URLs", e);
            return ResponseEntity.status(500).body(
                new ApiGenerateUploadUrlsResponse("Lỗi khi tạo pre-signed URL: " + e.getMessage(), 500, null, false)
            );
        }
    }
    
    @PostMapping("/confirm-upload")
    @Operation(summary = "Xác nhận upload thành công", 
               description = "Xác nhận file đã được upload thành công qua pre-signed URL. " +
                            "Tự động detect và xử lý cả single file và multiple files")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xác nhận upload thành công", 
                    content = @Content(schema = @Schema(implementation = ApiConfirmUploadResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file hoặc upload thất bại"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi xác nhận upload")
    })
    public ResponseEntity<ApiConfirmUploadResponse> confirmUpload(
            @Valid @RequestBody ConfirmFlexibleUploadRequest request) {
        try {
            List<AttachmentDTO> result = preSignedUrlService.confirmFlexibleUpload(request.getAttachmentIds());
            
            String message = request.isSingleFile() ? 
                "Xác nhận upload thành công" : 
                "Xác nhận upload thành công " + result.size() + " file";
            
            ApiConfirmUploadResponse response = new ApiConfirmUploadResponse();
            response.setMessage(message);
            response.setStatusCode(200);
            response.setData(result);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy") || e.getMessage().contains("chưa được upload")) {
                return ResponseEntity.status(404).body(
                    new ApiConfirmUploadResponse(e.getMessage(), 404, null, false)
                );
            }
            
            return ResponseEntity.status(500).body(
                new ApiConfirmUploadResponse("Lỗi khi xác nhận upload: " + e.getMessage(), 500, null, false)
            );
        }
    }
    
    @GetMapping("/download-url/{attachmentId}")
    @Operation(summary = "Tạo pre-signed URL để download file", 
               description = "Tạo pre-signed URL để download file từ Azure Blob Storage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo download URL thành công", 
                    content = @Content(schema = @Schema(implementation = ApiDownloadUrlResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi tạo download URL")
    })
    public ResponseEntity<ApiDownloadUrlResponse> generateDownloadUrl(@PathVariable Integer attachmentId) {
        try {
            String downloadUrl = preSignedUrlService.generateDownloadUrl(attachmentId);
            
            ApiDownloadUrlResponse response = new ApiDownloadUrlResponse();
            response.setMessage("Tạo download URL thành công");
            response.setStatusCode(200);
            response.setData(downloadUrl);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(404).body(
                    new ApiDownloadUrlResponse(e.getMessage(), 404, null, false)
                );
            }
            
            return ResponseEntity.status(500).body(
                new ApiDownloadUrlResponse("Lỗi khi tạo download URL: " + e.getMessage(), 500, null, false)
            );
        }
    }

    // ==================== CRUD ENDPOINTS ====================

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật tên file đính kèm", 
               description = "Chỉ cho phép cập nhật tên file đính kèm (fileName)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiUpdateAttachmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm")
    })
    public ResponseEntity<ApiUpdateAttachmentResponse> updateAttachment(
            @PathVariable Integer id, 
            @RequestBody UpdateAttachmentFileNameRequest request) {
        AttachmentDTO result = attachmentService.updateAttachmentFileName(id, request.getFileName());
        if (result == null) {
            return ResponseEntity.status(404).body(
                new ApiUpdateAttachmentResponse("Không tìm thấy file đính kèm", 404, null, false)
            );
        }
        
        ApiUpdateAttachmentResponse response = new ApiUpdateAttachmentResponse();
        response.setMessage("Cập nhật thành công");
        response.setStatusCode(200);
        response.setData(result);
        response.setSuccess(true);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa file đính kèm", 
               description = "Xóa file đính kèm khỏi Azure Blob Storage và database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiDeleteAttachmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi xóa file")
    })
    public ResponseEntity<ApiDeleteAttachmentResponse> deleteAttachment(@PathVariable Integer id) {
        try {
            preSignedUrlService.deleteFile(id);
            
            ApiDeleteAttachmentResponse response = new ApiDeleteAttachmentResponse();
            response.setMessage("Xóa file thành công");
            response.setStatusCode(200);
            response.setData(null);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(404).body(
                    new ApiDeleteAttachmentResponse(e.getMessage(), 404, null, false)
                );
            }
            
            return ResponseEntity.status(500).body(
                new ApiDeleteAttachmentResponse("Lỗi khi xóa file: " + e.getMessage(), 500, null, false)
            );
        }
    }

    @DeleteMapping("/bulk-delete")
    @Operation(summary = "Xóa nhiều file đính kèm", 
               description = "Xóa nhiều file đính kèm khỏi Azure Blob Storage và database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiBulkDeleteAttachmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy một hoặc nhiều file đính kèm"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi xóa file")
    })
    public ResponseEntity<ApiBulkDeleteAttachmentResponse> bulkDeleteAttachments(
            @Valid @RequestBody ConfirmFlexibleUploadRequest request) {
        try {
            int successCount = 0;
            List<String> errors = new ArrayList<>();
            
            for (Integer attachmentId : request.getAttachmentIds()) {
                try {
                    preSignedUrlService.deleteFile(attachmentId);
                    successCount++;
                } catch (Exception e) {
                    errors.add("ID " + attachmentId + ": " + e.getMessage());
                }
            }
            
            String message = "Đã xóa thành công " + successCount + " file";
            if (!errors.isEmpty()) {
                message += ". Lỗi: " + String.join(", ", errors);
            }
            
            ApiBulkDeleteAttachmentResponse response = new ApiBulkDeleteAttachmentResponse();
            response.setMessage(message);
            response.setStatusCode(200);
            response.setData(message);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                new ApiBulkDeleteAttachmentResponse("Lỗi khi xóa bulk file: " + e.getMessage(), 500, null, false)
            );
        }
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả file đính kèm", 
               description = "Lấy danh sách tất cả file đính kèm đã upload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiAttachmentListResponse.class)))
    })
    public ResponseEntity<ApiAttachmentListResponse> getAllAttachments() {
        List<AttachmentDTO> result = attachmentService.getAllAttachments();
        
        ApiAttachmentListResponse response = new ApiAttachmentListResponse();
        response.setMessage("Thành công");
        response.setStatusCode(200);
        response.setData(result);
        response.setSuccess(true);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết file đính kèm", 
               description = "Lấy chi tiết một file đính kèm theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy chi tiết file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiAttachmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm")
    })
    public ResponseEntity<ApiAttachmentResponse> getAttachmentById(@PathVariable Integer id) {
        AttachmentDTO result = attachmentService.getAttachmentById(id);
        if (result == null) {
            return ResponseEntity.status(404).body(
                new ApiAttachmentResponse("Không tìm thấy file đính kèm", 404, null, false)
            );
        }
        
        ApiAttachmentResponse response = new ApiAttachmentResponse();
        response.setMessage("Thành công");
        response.setStatusCode(200);
        response.setData(result);
        response.setSuccess(true);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-files")
    @Operation(summary = "Lấy danh sách file của tôi", 
               description = "Lấy danh sách tất cả file đính kèm mà user hiện tại đã upload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiAttachmentListResponse.class)))
    })
    public ResponseEntity<ApiAttachmentListResponse> getMyAttachments() {
        List<AttachmentDTO> result = attachmentService.getMyAttachments();
        
        ApiAttachmentListResponse response = new ApiAttachmentListResponse();
        response.setMessage("Thành công");
        response.setStatusCode(200);
        response.setData(result);
        response.setSuccess(true);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accessible-files")
    @Operation(summary = "Lấy danh sách file có quyền truy cập", 
               description = "Lấy danh sách tất cả file mà user hiện tại có quyền truy cập (bao gồm file của mình và file được chia sẻ)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiAttachmentListResponse.class)))
    })
    public ResponseEntity<ApiAttachmentListResponse> getAccessibleAttachments() {
        try {
            List<AttachmentDTO> result = attachmentService.getAccessibleAttachments();
            
            ApiAttachmentListResponse response = new ApiAttachmentListResponse();
            response.setMessage("Thành công");
            response.setStatusCode(200);
            response.setData(result);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting accessible files", e);
            return ResponseEntity.status(500).body(
                new ApiAttachmentListResponse("Lỗi khi lấy danh sách file có quyền truy cập: " + e.getMessage(), 500, null, false)
            );
        }
    }

    // ==================== FILE SHARING ENDPOINTS ====================

    @PostMapping("/share")
    @Operation(summary = "Chia sẻ file cho user khác (Batch Support)", 
               description = "Chia sẻ một hoặc nhiều file đính kèm cho một hoặc nhiều user khác với quyền truy cập cụ thể. " +
                            "Hỗ trợ chia sẻ batch: có thể chia sẻ nhiều file cùng lúc cho nhiều user. " +
                            "Format: attachmentIds=[1,2,3], sharedWithUserIds=[4,5,6]")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chia sẻ file thành công (có thể một phần thành công nếu là batch)", 
                    content = @Content(schema = @Schema(implementation = ApiShareFileResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "403", description = "Không có quyền chia sẻ file này"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi chia sẻ file")
    })
    public ResponseEntity<ApiShareFileResponse> shareFile(@Valid @RequestBody ShareFileRequest request) {
        try {
            List<FileShareDTO> result = fileShareService.shareFile(request);
            
            // Dynamic message based on batch or single operation
            String message;
            if (request.getAttachmentIds() != null && request.getAttachmentIds().size() > 1) {
                message = String.format("Chia sẻ batch thành công: %d file share đã tạo cho %d file(s)", 
                    result.size(), request.getAttachmentIds().size());
            } else {
                message = String.format("Chia sẻ file thành công cho %d user(s)", result.size());
            }
            
            ApiShareFileResponse response = new ApiShareFileResponse();
            response.setMessage(message);
            response.setStatusCode(200);
            response.setData(result);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error in file sharing: {}", e.getMessage());
            return ResponseEntity.status(400).body(
                new ApiShareFileResponse(e.getMessage(), 400, null, false)
            );
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(404).body(
                    new ApiShareFileResponse(e.getMessage(), 404, null, false)
                );
            } else if (e.getMessage().contains("không có quyền")) {
                return ResponseEntity.status(403).body(
                    new ApiShareFileResponse(e.getMessage(), 403, null, false)
                );
            }
            
            return ResponseEntity.status(500).body(
                new ApiShareFileResponse("Lỗi khi chia sẻ file: " + e.getMessage(), 500, null, false)
            );
        }
    }

    @GetMapping("/shared-with-me")
    @Operation(summary = "Lấy danh sách file được chia sẻ với tôi", 
               description = "Lấy danh sách tất cả file mà user khác đã chia sẻ với user hiện tại")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiFileShareListResponse.class)))
    })
    public ResponseEntity<ApiFileShareListResponse> getSharedWithMe() {
        try {
            List<FileShareDTO> result = fileShareService.getSharedWithMe();
            
            ApiFileShareListResponse response = new ApiFileShareListResponse();
            response.setMessage("Thành công");
            response.setStatusCode(200);
            response.setData(result);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting shared files", e);
            return ResponseEntity.status(500).body(
                new ApiFileShareListResponse("Lỗi khi lấy danh sách file được chia sẻ: " + e.getMessage(), 500, null, false)
            );
        }
    }

    @GetMapping("/my-shared-files")
    @Operation(summary = "Lấy danh sách file tôi đã chia sẻ", 
               description = "Lấy danh sách tất cả file mà user hiện tại đã chia sẻ cho người khác")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiFileShareListResponse.class)))
    })
    public ResponseEntity<ApiFileShareListResponse> getMySharedFiles() {
        try {
            List<FileShareDTO> result = fileShareService.getMySharedFiles();
            
            ApiFileShareListResponse response = new ApiFileShareListResponse();
            response.setMessage("Thành công");
            response.setStatusCode(200);
            response.setData(result);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting my shared files", e);
            return ResponseEntity.status(500).body(
                new ApiFileShareListResponse("Lỗi khi lấy danh sách file đã chia sẻ: " + e.getMessage(), 500, null, false)
            );
        }
    }

    @GetMapping("/{attachmentId}/shares")
    @Operation(summary = "Lấy danh sách user được chia sẻ file", 
               description = "Lấy danh sách tất cả user được chia sẻ một file cụ thể (chỉ owner mới xem được)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách user thành công", 
                    content = @Content(schema = @Schema(implementation = ApiFileShareListResponse.class))),
        @ApiResponse(responseCode = "403", description = "Không có quyền xem danh sách chia sẻ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm")
    })
    public ResponseEntity<ApiFileShareListResponse> getFileShares(@PathVariable Integer attachmentId) {
        try {
            List<FileShareDTO> result = fileShareService.getFileSharesByAttachment(attachmentId);
            
            ApiFileShareListResponse response = new ApiFileShareListResponse();
            response.setMessage("Thành công");
            response.setStatusCode(200);
            response.setData(result);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(404).body(
                    new ApiFileShareListResponse(e.getMessage(), 404, null, false)
                );
            } else if (e.getMessage().contains("không có quyền")) {
                return ResponseEntity.status(403).body(
                    new ApiFileShareListResponse(e.getMessage(), 403, null, false)
                );
            }
            
            return ResponseEntity.status(500).body(
                new ApiFileShareListResponse("Lỗi khi lấy danh sách chia sẻ: " + e.getMessage(), 500, null, false)
            );
        }
    }

    @PutMapping("/shares/{shareId}")
    @Operation(summary = "Cập nhật quyền chia sẻ file", 
               description = "Cập nhật quyền truy cập và thông tin chia sẻ file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật chia sẻ thành công", 
                    content = @Content(schema = @Schema(implementation = ApiShareFileResponse.class))),
        @ApiResponse(responseCode = "403", description = "Không có quyền cập nhật chia sẻ này"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy chia sẻ file")
    })
    public ResponseEntity<ApiShareFileResponse> updateFileShare(
            @PathVariable Integer shareId, 
            @Valid @RequestBody UpdateFileShareRequest request) {
        try {
            List<FileShareDTO> result = fileShareService.updateFileShare(shareId, request);
            
            ApiShareFileResponse response = new ApiShareFileResponse();
            response.setMessage("Cập nhật chia sẻ thành công");
            response.setStatusCode(200);
            response.setData(result);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(404).body(
                    new ApiShareFileResponse(e.getMessage(), 404, null, false)
                );
            } else if (e.getMessage().contains("không có quyền")) {
                return ResponseEntity.status(403).body(
                    new ApiShareFileResponse(e.getMessage(), 403, null, false)
                );
            }
            
            return ResponseEntity.status(500).body(
                new ApiShareFileResponse("Lỗi khi cập nhật chia sẻ: " + e.getMessage(), 500, null, false)
            );
        }
    }

    @DeleteMapping("/shares/{shareId}")
    @Operation(summary = "Hủy chia sẻ file", 
               description = "Hủy chia sẻ file với user cụ thể")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hủy chia sẻ thành công"),
        @ApiResponse(responseCode = "403", description = "Không có quyền hủy chia sẻ này"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy chia sẻ file")
    })
    public ResponseEntity<ApiShareFileResponse> revokeFileShare(@PathVariable Integer shareId) {
        try {
            fileShareService.revokeFileShare(shareId);
            
            ApiShareFileResponse response = new ApiShareFileResponse();
            response.setMessage("Hủy chia sẻ thành công");
            response.setStatusCode(200);
            response.setData(null);
            response.setSuccess(true);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(404).body(
                    new ApiShareFileResponse(e.getMessage(), 404, null, false)
                );
            } else if (e.getMessage().contains("không có quyền")) {
                return ResponseEntity.status(403).body(
                    new ApiShareFileResponse(e.getMessage(), 403, null, false)
                );
            }
            
            return ResponseEntity.status(500).body(
                new ApiShareFileResponse("Lỗi khi hủy chia sẻ: " + e.getMessage(), 500, null, false)
            );
        }
    }
}
