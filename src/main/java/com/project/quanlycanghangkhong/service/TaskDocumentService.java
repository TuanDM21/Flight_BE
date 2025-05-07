package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.DocumentDTO;

public interface TaskDocumentService {
    DocumentDTO attachDocumentToTask(Integer taskId, DocumentDTO documentDTO);
    void removeDocumentFromTask(Integer taskId, Integer documentId);
    DocumentDTO updateDocumentInTask(Integer taskId, Integer documentId, DocumentDTO documentDTO);
}
