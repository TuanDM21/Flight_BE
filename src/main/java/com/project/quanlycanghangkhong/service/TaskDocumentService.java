package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import java.util.List;

public interface TaskDocumentService {
    List<DocumentDTO> getDocumentsByTaskId(Integer taskId);
    void attachDocumentToTask(Integer taskId, Integer documentId);
    void removeDocumentFromTask(Integer taskId, Integer documentId);
    DocumentDTO updateDocumentInTask(Integer taskId, Integer documentId, DocumentDTO documentDTO);
}
