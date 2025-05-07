package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import com.project.quanlycanghangkhong.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentDTO> createDocument(@RequestBody DocumentDTO dto) {
        return ResponseEntity.ok(documentService.createDocument(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(@PathVariable Integer id, @RequestBody DocumentDTO dto) {
        return ResponseEntity.ok(documentService.updateDocument(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Integer id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Integer id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }
}

