package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import java.util.List;

public interface DocumentService {
    DocumentDTO createDocument(DocumentDTO dto);
    DocumentDTO updateDocument(Integer id, DocumentDTO dto);
    void deleteDocument(Integer id);
    DocumentDTO getDocumentById(Integer id);
    List<DocumentDTO> getAllDocuments();
}
