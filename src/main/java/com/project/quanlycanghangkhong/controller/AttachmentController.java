package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.service.AttachmentService;
import com.project.quanlycanghangkhong.service.AzurePreSignedUrlService;
import com.project.quanlycanghangkhong.request.UpdateAttachmentFileNameRequest;
import com.project.quanlycanghangkhong.request.FlexibleUploadRequest;
import com.project.quanlycanghangkhong.request.ConfirmFlexibleUploadRequest;
import com.project.quanlycanghangkhong.dto.FlexiblePreSignedUrlDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
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

    // ==================== FLEXIBLE PRE-SIGNED URL ENDPOINTS ====================
    
    @PostMapping("/generate-upload-urls")
    @Operation(summary = "Tạo pre-signed URL để upload file", 
               description = "Tạo pre-signed URL để client upload file trực tiếp lên Azure Blob Storage. " +
                            "Tự động detect và xử lý cả single file (1 file) và multiple files (nhiều file)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo pre-signed URL thành công", 
                    content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi tạo pre-signed URL")
    })
    public ResponseEntity<ApiResponseCustom<FlexiblePreSignedUrlDTO>> generateUploadUrls(
            @Valid @RequestBody FlexibleUploadRequest request) {
        try {
            // Log upload request for monitoring
            logger.info("Generating upload URLs for {} files", request.getFiles().size());
            
            FlexiblePreSignedUrlDTO result = preSignedUrlService.generateFlexibleUploadUrls(request);
            
            // Log success
            logger.info("Successfully generated upload URLs");
            
            return ResponseEntity.ok(ApiResponseCustom.success(result.getMessage(), result));
            
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error in upload URL generation: {}", e.getMessage());
            return ResponseEntity.status(400).body(ApiResponseCustom.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error generating upload URLs", e);
            return ResponseEntity.status(500).body(ApiResponseCustom.internalError("Lỗi khi tạo pre-signed URL: " + e.getMessage()));
        }
    }
    
    @PostMapping("/confirm-upload")
    @Operation(summary = "Xác nhận upload thành công", 
               description = "Xác nhận file đã được upload thành công qua pre-signed URL. " +
                            "Tự động detect và xử lý cả single file và multiple files")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xác nhận upload thành công", 
                    content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file hoặc upload thất bại"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi xác nhận upload")
    })
    public ResponseEntity<ApiResponseCustom<List<AttachmentDTO>>> confirmUpload(
            @Valid @RequestBody ConfirmFlexibleUploadRequest request) {
        try {
            List<AttachmentDTO> result = preSignedUrlService.confirmFlexibleUpload(request.getAttachmentIds());
            
            String message = request.isSingleFile() ? 
                "Xác nhận upload thành công" : 
                "Xác nhận upload thành công " + result.size() + " file";
            
            return ResponseEntity.ok(ApiResponseCustom.success(message, result));
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy") || e.getMessage().contains("chưa được upload")) {
                return ResponseEntity.status(404).body(ApiResponseCustom.notFound(e.getMessage()));
            }
            
            return ResponseEntity.status(500).body(ApiResponseCustom.internalError("Lỗi khi xác nhận upload: " + e.getMessage()));
        }
    }
    
    @GetMapping("/download-url/{attachmentId}")
    @Operation(summary = "Tạo pre-signed URL để download file", 
               description = "Tạo pre-signed URL để download file từ Azure Blob Storage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo download URL thành công", 
                    content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi tạo download URL")
    })
    public ResponseEntity<ApiResponseCustom<String>> generateDownloadUrl(@PathVariable Integer attachmentId) {
        try {
            String downloadUrl = preSignedUrlService.generateDownloadUrl(attachmentId);
            
            return ResponseEntity.ok(ApiResponseCustom.success("Tạo download URL thành công", downloadUrl));
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(404).body(ApiResponseCustom.notFound(e.getMessage()));
            }
            
            return ResponseEntity.status(500).body(ApiResponseCustom.internalError("Lỗi khi tạo download URL: " + e.getMessage()));
        }
    }

    // ==================== CRUD ENDPOINTS ====================

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật tên file đính kèm", 
               description = "Chỉ cho phép cập nhật tên file đính kèm (fileName)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm")
    })
    public ResponseEntity<ApiResponseCustom<AttachmentDTO>> updateAttachment(
            @PathVariable Integer id, 
            @RequestBody UpdateAttachmentFileNameRequest request) {
        AttachmentDTO result = attachmentService.updateAttachmentFileName(id, request.getFileName());
        if (result == null) {
            return ResponseEntity.status(404).body(ApiResponseCustom.notFound("Không tìm thấy file đính kèm"));
        }
        
        return ResponseEntity.ok(ApiResponseCustom.success("Cập nhật thành công", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa file đính kèm", 
               description = "Xóa file đính kèm khỏi Azure Blob Storage và database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi xóa file")
    })
    public ResponseEntity<ApiResponseCustom<Void>> deleteAttachment(@PathVariable Integer id) {
        try {
            preSignedUrlService.deleteFile(id);
            
            return ResponseEntity.ok(ApiResponseCustom.success("Xóa file thành công", null));
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(404).body(ApiResponseCustom.notFound(e.getMessage()));
            }
            
            return ResponseEntity.status(500).body(ApiResponseCustom.internalError("Lỗi khi xóa file: " + e.getMessage()));
        }
    }

    @DeleteMapping("/bulk-delete")
    @Operation(summary = "Xóa nhiều file đính kèm", 
               description = "Xóa nhiều file đính kèm khỏi Azure Blob Storage và database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy một hoặc nhiều file đính kèm"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi xóa file")
    })
    public ResponseEntity<ApiResponseCustom<String>> bulkDeleteAttachments(
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
            
            return ResponseEntity.ok(ApiResponseCustom.success(message, message));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponseCustom.internalError("Lỗi khi xóa bulk file: " + e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả file đính kèm", 
               description = "Lấy danh sách tất cả file đính kèm đã upload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<AttachmentDTO>>> getAllAttachments() {
        List<AttachmentDTO> result = attachmentService.getAllAttachments();
        
        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết file đính kèm", 
               description = "Lấy chi tiết một file đính kèm theo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy chi tiết file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm")
    })
    public ResponseEntity<ApiResponseCustom<AttachmentDTO>> getAttachmentById(@PathVariable Integer id) {
        AttachmentDTO result = attachmentService.getAttachmentById(id);
        if (result == null) {
            return ResponseEntity.status(404).body(ApiResponseCustom.notFound("Không tìm thấy file đính kèm"));
        }
        
        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", result));
    }

    @GetMapping("/available")
    @Operation(summary = "Lấy danh sách file chưa gán vào task", 
               description = "Lấy danh sách tất cả file chưa được gán vào task nào (chỉ admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách file thành công", 
                    content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    public ResponseEntity<ApiResponseCustom<List<AttachmentDTO>>> getAvailableAttachments() {
        try {
            List<AttachmentDTO> result = attachmentService.getAvailableAttachments();
            
            return ResponseEntity.ok(ApiResponseCustom.success("Thành công", result));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(ApiResponseCustom.forbidden(e.getMessage()));
        }
    }
}
