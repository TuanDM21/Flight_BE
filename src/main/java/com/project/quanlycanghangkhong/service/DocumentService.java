package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import com.project.quanlycanghangkhong.dto.CreateDocumentRequest;
import com.project.quanlycanghangkhong.dto.UpdateDocumentRequest;
import java.util.List;

public interface DocumentService {
    DocumentDTO createDocument(CreateDocumentRequest request);
    DocumentDTO updateDocument(Integer id, UpdateDocumentRequest request);
    void deleteDocument(Integer id);
    DocumentDTO getDocumentById(Integer id);
    List<DocumentDTO> getAllDocuments();
    List<DocumentDTO> bulkInsertDocuments(List<DocumentDTO> dtos);
    void bulkDeleteDocuments(List<Integer> ids);
}
