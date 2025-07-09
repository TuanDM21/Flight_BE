package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import com.project.quanlycanghangkhong.service.DocumentService;
import com.project.quanlycanghangkhong.dto.response.document.ApiDocumentResponse;
import com.project.quanlycanghangkhong.dto.response.document.ApiDocumentListResponse;
import com.project.quanlycanghangkhong.dto.response.document.ApiBulkDeleteDocumentsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.project.quanlycanghangkhong.dto.CreateDocumentRequest;
import com.project.quanlycanghangkhong.dto.UpdateDocumentRequest;
import com.project.quanlycanghangkhong.dto.request.BulkDeleteDocumentsRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    // === BUSINESS LOGIC THAY ĐỔI: Document không còn quản lý attachment ===
    // AttachmentService đã được loại bỏ khỏi DocumentController

    @PostMapping
    @Operation(summary = "Tạo document", description = "Tạo mới một document")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo document thành công", content = @Content(schema = @Schema(implementation = ApiDocumentResponse.class)))
    })
    public ResponseEntity<ApiDocumentResponse> createDocument(@RequestBody CreateDocumentRequest request) {
        DocumentDTO result = documentService.createDocument(request);
        ApiDocumentResponse response = new ApiDocumentResponse("Tạo thành công", 201, result, true);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật document", description = "Cập nhật thông tin document")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật document thành công", content = @Content(schema = @Schema(implementation = ApiDocumentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy document", content = @Content(schema = @Schema(implementation = ApiDocumentResponse.class)))
    })
    public ResponseEntity<ApiDocumentResponse> updateDocument(@PathVariable Integer id, @RequestBody UpdateDocumentRequest request) {
        DocumentDTO result = documentService.updateDocument(id, request);
        if (result == null) return ResponseEntity.status(404).body(new ApiDocumentResponse("Không tìm thấy văn bản", 404, null, false));
        ApiDocumentResponse response = new ApiDocumentResponse("Cập nhật thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá document", description = "Xoá một document theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xoá document thành công", content = @Content(schema = @Schema(implementation = ApiDocumentResponse.class)))
    })
    public ResponseEntity<ApiDocumentResponse> deleteDocument(@PathVariable Integer id) {
        documentService.deleteDocument(id);
        ApiDocumentResponse response = new ApiDocumentResponse("Xoá thành công", 200, null, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết document", description = "Lấy chi tiết một document theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy document thành công", content = @Content(schema = @Schema(implementation = ApiDocumentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy document", content = @Content(schema = @Schema(implementation = ApiDocumentResponse.class)))
    })
    public ResponseEntity<ApiDocumentResponse> getDocumentById(@PathVariable Integer id) {
        DocumentDTO result = documentService.getDocumentById(id);
        if (result == null) return ResponseEntity.status(404).body(new ApiDocumentResponse("Không tìm thấy văn bản", 404, null, false));
        ApiDocumentResponse response = new ApiDocumentResponse("Thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách document", description = "Lấy tất cả document")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách document thành công", content = @Content(schema = @Schema(implementation = ApiDocumentListResponse.class)))
    })
    public ResponseEntity<ApiDocumentListResponse> getAllDocuments() {
        List<DocumentDTO> result = documentService.getAllDocuments();
        ApiDocumentListResponse response = new ApiDocumentListResponse("Thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }

    // @PostMapping("/bulk")
    // @Operation(summary = "Bulk insert documents", description = "Tạo nhiều document cùng lúc")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "201", description = "Tạo documents thành công", content = @Content(schema = @Schema(implementation = ApiDocumentListResponse.class)))
    // })
    // public ResponseEntity<ApiDocumentListResponse> bulkInsertDocuments(@RequestBody List<DocumentDTO> dtos) {
    //     List<DocumentDTO> result = documentService.bulkInsertDocuments(dtos);
    //     ApiDocumentListResponse response = new ApiDocumentListResponse("Tạo thành công", 201, result, true);
    //     return ResponseEntity.status(201).body(response);
    // }

    @DeleteMapping("/bulk-delete")
    @Operation(summary = "Xóa nhiều document", description = "Xóa nhiều document cùng lúc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa thành công", content = @Content(schema = @Schema(implementation = ApiBulkDeleteDocumentsResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = @Content(schema = @Schema(implementation = ApiBulkDeleteDocumentsResponse.class)))
    })
    public ResponseEntity<ApiBulkDeleteDocumentsResponse> bulkDeleteDocuments(@Valid @RequestBody BulkDeleteDocumentsRequest request) {
        try {
            if (request.getDocumentIds() == null || request.getDocumentIds().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ApiBulkDeleteDocumentsResponse("Danh sách document ID không được để trống", 400, null, false)
                );
            }

            documentService.bulkDeleteDocuments(request.getDocumentIds());
            
            String message = "Đã xóa thành công " + request.getDocumentIds().size() + " document";
            return ResponseEntity.ok(new ApiBulkDeleteDocumentsResponse(message, 200, message, true));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                new ApiBulkDeleteDocumentsResponse("Lỗi khi xóa document: " + e.getMessage(), 500, null, false)
            );
        }
    }

    // === BUSINESS LOGIC THAY ĐỔI: Document không còn quản lý attachment ===
    // Toàn bộ attachment giờ được gán trực tiếp vào task thông qua TaskController
    // API gán/gỡ attachment vào document đã được loại bỏ
    
    /*
    @PostMapping("/{documentId}/attachments/assign")
    @Operation(summary = "Gán nhiều file đính kèm vào document", description = "Gán các attachment đã upload vào document theo documentId")
    public ResponseEntity<ApiResponseCustom<Void>> assignAttachmentsToDocument(
            @PathVariable Integer documentId,
            @RequestBody AttachmentAssignRequest request) {
        attachmentService.assignAttachmentsToDocument(documentId, request);
        return ResponseEntity.ok(new ApiResponseCustom<>("Gán file thành công", 200, null, true));
    }

    @PatchMapping("/{documentId}/attachments/remove")
    @Operation(summary = "Gỡ nhiều file đính kèm khỏi document", description = "Gỡ các attachment khỏi document theo documentId")
    public ResponseEntity<ApiResponseCustom<Void>> removeAttachmentsFromDocument(
            @PathVariable Integer documentId,
            @RequestBody AttachmentAssignRequest request) {
        attachmentService.removeAttachmentsFromDocument(documentId, request);
        return ResponseEntity.ok(new ApiResponseCustom<>("Gỡ file thành công", 200, null, true));
    }
    */
}

