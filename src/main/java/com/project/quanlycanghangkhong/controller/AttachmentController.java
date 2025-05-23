package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.service.AttachmentService;
import com.project.quanlycanghangkhong.dto.response.attachment.ApiAttachmentResponse;
import com.project.quanlycanghangkhong.dto.response.attachment.ApiAttachmentListResponse;
import com.project.quanlycanghangkhong.service.AzureBlobService;
import com.project.quanlycanghangkhong.dto.request.UpdateAttachmentFileNameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/attachments")
@CrossOrigin(origins = "*")
public class AttachmentController {
    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AzureBlobService azureBlobService;

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật tên file đính kèm", description = "Chỉ cho phép cập nhật tên file đính kèm (fileName)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật file thành công", content = @Content(schema = @Schema(implementation = ApiAttachmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm", content = @Content(schema = @Schema(implementation = ApiAttachmentResponse.class)))
    })
    public ResponseEntity<ApiAttachmentResponse> updateAttachment(@PathVariable Integer id, @RequestBody UpdateAttachmentFileNameRequest request) {
        AttachmentDTO result = attachmentService.updateAttachmentFileName(id, request.getFileName());
        if (result == null) return ResponseEntity.status(404).body(new ApiAttachmentResponse("Không tìm thấy file đính kèm", 404, null, false));
        ApiAttachmentResponse response = new ApiAttachmentResponse("Cập nhật thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá file đính kèm", description = "Xoá file đính kèm trên Azure Blob và database theo id")
    public ResponseEntity<ApiAttachmentResponse> deleteAttachment(@PathVariable Integer id) {
        azureBlobService.deleteAttachmentAndBlob(id);
        ApiAttachmentResponse response = new ApiAttachmentResponse("Xoá thành công", 200, null, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả file đính kèm", description = "Lấy tất cả file đính kèm đã upload")
    public ResponseEntity<ApiAttachmentListResponse> getAllAttachments() {
        List<AttachmentDTO> result = attachmentService.getAllAttachments();
        ApiAttachmentListResponse response = new ApiAttachmentListResponse("Thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết file đính kèm", description = "Lấy chi tiết một file đính kèm theo id")
    public ResponseEntity<ApiAttachmentResponse> getAttachmentById(@PathVariable Integer id) {
        AttachmentDTO result = attachmentService.getAttachmentById(id);
        if (result == null) return ResponseEntity.status(404).body(new ApiAttachmentResponse("Không tìm thấy file đính kèm", 404, null, false));
        ApiAttachmentResponse response = new ApiAttachmentResponse("Thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/upload-multi", consumes = "multipart/form-data")
    @Operation(summary = "Upload nhiều file lên Azure Blob Storage", description = "Upload nhiều file và trả về thông tin file đính kèm")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Upload file thành công", content = @Content(schema = @Schema(implementation = ApiAttachmentListResponse.class)))
    })
    public ResponseEntity<ApiAttachmentListResponse> uploadMultipleFiles(
            @RequestPart("files") MultipartFile[] files) throws Exception {
        List<AttachmentDTO> result = azureBlobService.uploadFiles(files);
        ApiAttachmentListResponse response = new ApiAttachmentListResponse("Upload thành công", 201, result, true);
        return ResponseEntity.status(201).body(response);
    }

    
}
