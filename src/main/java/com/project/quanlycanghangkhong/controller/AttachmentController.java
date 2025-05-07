package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {
    @Autowired
    private AttachmentService attachmentService;

    @PostMapping("/document/{documentId}")
    public ResponseEntity<ApiResponseCustom<AttachmentDTO>> addAttachment(@PathVariable Integer documentId, @RequestBody AttachmentDTO dto) {
        AttachmentDTO result = attachmentService.addAttachmentToDocument(documentId, dto);
        if (result == null) return ResponseEntity.status(404).body(ApiResponseCustom.error(HttpStatus.NOT_FOUND, "Không tìm thấy văn bản để gắn file"));
        return ResponseEntity.status(201).body(ApiResponseCustom.created(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<AttachmentDTO>> updateAttachment(@PathVariable Integer id, @RequestBody AttachmentDTO dto) {
        AttachmentDTO result = attachmentService.updateAttachment(id, dto);
        if (result == null) return ResponseEntity.status(404).body(ApiResponseCustom.error(HttpStatus.NOT_FOUND, "Không tìm thấy file đính kèm"));
        return ResponseEntity.ok(ApiResponseCustom.success("Cập nhật thành công", result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<Void>> deleteAttachment(@PathVariable Integer id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.ok(ApiResponseCustom.success("Xoá thành công", null));
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<ApiResponseCustom<List<AttachmentDTO>>> getAttachmentsByDocument(@PathVariable Integer documentId) {
        List<AttachmentDTO> result = attachmentService.getAttachmentsByDocumentId(documentId);
        return ResponseEntity.ok(ApiResponseCustom.success(result));
    }
}
