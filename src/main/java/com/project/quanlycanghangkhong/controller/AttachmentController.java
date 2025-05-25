package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.service.AttachmentService;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.service.AzurePreSignedUrlService;
import com.project.quanlycanghangkhong.dto.request.UpdateAttachmentFileNameRequest;
import com.project.quanlycanghangkhong.dto.request.GenerateUploadUrlRequest;
import com.project.quanlycanghangkhong.dto.response.presigned.PreSignedUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/attachments")
@CrossOrigin(origins = "*")
@Tag(name = "Azure Pre-signed URL File Management", description = "APIs quản lý file đính kèm sử dụng Azure Pre-signed URL")
public class AttachmentController {
    
    @Autowired
    private AttachmentService attachmentService;
    
    @Autowired
    private AzurePreSignedUrlService preSignedUrlService;

    // ==================== PRE-SIGNED URL ENDPOINTS ====================
    
    @PostMapping("/generate-upload-url")
    @Operation(summary = "Tạo pre-signed URL để upload file", 
               description = "Tạo pre-signed URL để client upload file trực tiếp lên Azure Blob Storage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo pre-signed URL thành công", 
                    content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi tạo pre-signed URL")
    })
    public ResponseEntity<ApiResponseCustom<PreSignedUrlResponse>> generateUploadUrl(
            @Valid @RequestBody GenerateUploadUrlRequest request) {
        try {
            PreSignedUrlResponse result = preSignedUrlService.generateUploadUrl(
                    request.getFileName(), 
                    request.getFileSize(), 
                    request.getContentType()
            );
            
            return ResponseEntity.ok(ApiResponseCustom.success("Tạo pre-signed URL thành công", result));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                ApiResponseCustom.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Lỗi khi tạo pre-signed URL: " + e.getMessage())
            );
        }
    }
    
    @PostMapping("/confirm-upload/{attachmentId}")
    @Operation(summary = "Xác nhận upload thành công", 
               description = "Xác nhận file đã được upload thành công qua pre-signed URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xác nhận upload thành công", 
                    content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file hoặc upload thất bại"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi xác nhận upload")
    })
    public ResponseEntity<ApiResponseCustom<AttachmentDTO>> confirmUpload(@PathVariable Integer attachmentId) {
        try {
            AttachmentDTO result = preSignedUrlService.confirmUpload(attachmentId);
            
            return ResponseEntity.ok(ApiResponseCustom.success("Xác nhận upload thành công", result));
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy") || e.getMessage().contains("chưa được upload")) {
                return ResponseEntity.status(404).body(
                    ApiResponseCustom.error(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage())
                );
            }
            
            return ResponseEntity.status(500).body(
                ApiResponseCustom.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Lỗi khi xác nhận upload: " + e.getMessage())
            );
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
                return ResponseEntity.status(404).body(
                    ApiResponseCustom.error(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage())
                );
            }
            
            return ResponseEntity.status(500).body(
                ApiResponseCustom.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Lỗi khi tạo download URL: " + e.getMessage())
            );
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
            return ResponseEntity.status(404).body(
                ApiResponseCustom.error(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy file đính kèm")
            );
        }
        return ResponseEntity.ok(ApiResponseCustom.success("Cập nhật thành công", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa file đính kèm", 
               description = "Xóa file đính kèm khỏi Azure Blob Storage và database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa file thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm"),
        @ApiResponse(responseCode = "500", description = "Lỗi server khi xóa file")
    })
    public ResponseEntity<ApiResponseCustom<Void>> deleteAttachment(@PathVariable Integer id) {
        try {
            preSignedUrlService.deleteFile(id);
            return ResponseEntity.ok(ApiResponseCustom.success("Xóa file thành công", null));
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(404).body(
                    ApiResponseCustom.error(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage())
                );
            }
            
            return ResponseEntity.status(500).body(
                ApiResponseCustom.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Lỗi khi xóa file: " + e.getMessage())
            );
        }
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả file đính kèm", 
               description = "Lấy danh sách tất cả file đính kèm đã upload")
    public ResponseEntity<ApiResponseCustom<List<AttachmentDTO>>> getAllAttachments() {
        List<AttachmentDTO> result = attachmentService.getAllAttachments();
        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết file đính kèm", 
               description = "Lấy chi tiết một file đính kèm theo ID")
    public ResponseEntity<ApiResponseCustom<AttachmentDTO>> getAttachmentById(@PathVariable Integer id) {
        AttachmentDTO result = attachmentService.getAttachmentById(id);
        if (result == null) {
            return ResponseEntity.status(404).body(
                ApiResponseCustom.error(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy file đính kèm")
            );
        }
        return ResponseEntity.ok(ApiResponseCustom.success("Thành công", result));
    }
}
