package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import com.project.quanlycanghangkhong.service.DocumentService;
import com.project.quanlycanghangkhong.dto.response.document.ApiDocumentResponse;
import com.project.quanlycanghangkhong.dto.response.document.ApiDocumentListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping
    @Operation(summary = "Tạo document", description = "Tạo mới một document")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo document thành công", content = @Content(schema = @Schema(implementation = ApiDocumentResponse.class)))
    })
    public ResponseEntity<ApiDocumentResponse> createDocument(@RequestBody DocumentDTO dto) {
        DocumentDTO result = documentService.createDocument(dto);
        ApiDocumentResponse response = new ApiDocumentResponse("Tạo thành công", 201, result, true);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật document", description = "Cập nhật thông tin document")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật document thành công", content = @Content(schema = @Schema(implementation = ApiDocumentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy document", content = @Content(schema = @Schema(implementation = ApiDocumentResponse.class)))
    })
    public ResponseEntity<ApiDocumentResponse> updateDocument(@PathVariable Integer id, @RequestBody DocumentDTO dto) {
        DocumentDTO result = documentService.updateDocument(id, dto);
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
}

