package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping
    public ResponseEntity<ApiResponseCustom<DocumentDTO>> createDocument(@RequestBody DocumentDTO dto) {
        DocumentDTO result = documentService.createDocument(dto);
        return ResponseEntity.status(201).body(ApiResponseCustom.created(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<DocumentDTO>> updateDocument(@PathVariable Integer id, @RequestBody DocumentDTO dto) {
        DocumentDTO result = documentService.updateDocument(id, dto);
        if (result == null) return ResponseEntity.status(404).body(ApiResponseCustom.error(HttpStatus.NOT_FOUND, "Không tìm thấy văn bản"));
        return ResponseEntity.ok(ApiResponseCustom.success("Cập nhật thành công", result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<Void>> deleteDocument(@PathVariable Integer id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponseCustom.success("Xoá thành công", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<DocumentDTO>> getDocumentById(@PathVariable Integer id) {
        DocumentDTO result = documentService.getDocumentById(id);
        if (result == null) return ResponseEntity.status(404).body(ApiResponseCustom.error(HttpStatus.NOT_FOUND, "Không tìm thấy văn bản"));
        return ResponseEntity.ok(ApiResponseCustom.success(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponseCustom<List<DocumentDTO>>> getAllDocuments() {
        List<DocumentDTO> result = documentService.getAllDocuments();
        return ResponseEntity.ok(ApiResponseCustom.success(result));
    }
}

