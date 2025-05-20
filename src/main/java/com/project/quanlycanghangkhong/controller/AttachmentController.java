package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.service.AttachmentService;
import com.project.quanlycanghangkhong.dto.response.attachment.ApiAttachmentResponse;
import com.project.quanlycanghangkhong.dto.response.attachment.ApiAttachmentListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attachments")
@CrossOrigin(origins = "*")
public class AttachmentController {
    @Autowired
    private AttachmentService attachmentService;

    @PostMapping("/document/{documentId}")
    @Operation(summary = "Gắn file vào document", description = "Gắn file đính kèm vào document theo documentId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Gắn file thành công", content = @Content(schema = @Schema(implementation = ApiAttachmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy document", content = @Content(schema = @Schema(implementation = ApiAttachmentResponse.class)))
    })
    public ResponseEntity<ApiAttachmentResponse> addAttachment(@PathVariable Integer documentId, @RequestBody AttachmentDTO dto) {
        AttachmentDTO result = attachmentService.addAttachmentToDocument(documentId, dto);
        if (result == null) return ResponseEntity.status(404).body(new ApiAttachmentResponse("Không tìm thấy văn bản để gắn file", 404, null, false));
        ApiAttachmentResponse response = new ApiAttachmentResponse("Gắn file thành công", 201, result, true);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật file đính kèm", description = "Cập nhật thông tin file đính kèm")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật file thành công", content = @Content(schema = @Schema(implementation = ApiAttachmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy file đính kèm", content = @Content(schema = @Schema(implementation = ApiAttachmentResponse.class)))
    })
    public ResponseEntity<ApiAttachmentResponse> updateAttachment(@PathVariable Integer id, @RequestBody AttachmentDTO dto) {
        AttachmentDTO result = attachmentService.updateAttachment(id, dto);
        if (result == null) return ResponseEntity.status(404).body(new ApiAttachmentResponse("Không tìm thấy file đính kèm", 404, null, false));
        ApiAttachmentResponse response = new ApiAttachmentResponse("Cập nhật thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá file đính kèm", description = "Xoá file đính kèm theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xoá file thành công", content = @Content(schema = @Schema(implementation = ApiAttachmentResponse.class)))
    })
    public ResponseEntity<ApiAttachmentResponse> deleteAttachment(@PathVariable Integer id) {
        attachmentService.deleteAttachment(id);
        ApiAttachmentResponse response = new ApiAttachmentResponse("Xoá thành công", 200, null, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/document/{documentId}")
    @Operation(summary = "Lấy danh sách file đính kèm theo document", description = "Lấy tất cả file đính kèm của một document")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách file thành công", content = @Content(schema = @Schema(implementation = ApiAttachmentListResponse.class)))
    })
    public ResponseEntity<ApiAttachmentListResponse> getAttachmentsByDocument(@PathVariable Integer documentId) {
        List<AttachmentDTO> result = attachmentService.getAttachmentsByDocumentId(documentId);
        ApiAttachmentListResponse response = new ApiAttachmentListResponse("Thành công", 200, result, true);
        return ResponseEntity.ok(response);
    }
}
