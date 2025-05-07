package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.DocumentDTO;
import com.project.quanlycanghangkhong.dto.DTOConverter;
import com.project.quanlycanghangkhong.model.Document;
import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.model.TaskDocument;
import com.project.quanlycanghangkhong.repository.DocumentRepository;
import com.project.quanlycanghangkhong.repository.TaskDocumentRepository;
import com.project.quanlycanghangkhong.repository.TaskRepository;
import com.project.quanlycanghangkhong.service.TaskDocumentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class TaskDocumentServiceImpl implements TaskDocumentService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private TaskDocumentRepository taskDocumentRepository;

    @Override
    public DocumentDTO attachDocumentToTask(Integer taskId, DocumentDTO documentDTO) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        Document document;
        if (documentDTO.getId() != null) {
            document = documentRepository.findById(documentDTO.getId()).orElseThrow();
        } else {
            document = new Document();
            document.setDocumentType(documentDTO.getDocumentType());
            document.setContent(documentDTO.getContent());
            document.setNotes(documentDTO.getNotes());
            document.setCreatedAt(LocalDateTime.now());
            document.setUpdatedAt(LocalDateTime.now());
            document = documentRepository.save(document);
        }
        TaskDocument taskDocument = new TaskDocument();
        taskDocument.setTask(task);
        taskDocument.setDocument(document);
        taskDocument.setCreatedAt(LocalDateTime.now());
        taskDocumentRepository.save(taskDocument);
        return DTOConverter.convertDocument(document);
    }

    @Override
    public void removeDocumentFromTask(Integer taskId, Integer documentId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        Document document = documentRepository.findById(documentId).orElseThrow();
        TaskDocument taskDocument = taskDocumentRepository.findByTaskAndDocument(task, document).orElseThrow();
        taskDocumentRepository.delete(taskDocument);
    }

    @Override
    public DocumentDTO updateDocumentInTask(Integer taskId, Integer documentId, DocumentDTO documentDTO) {
        Document document = documentRepository.findById(documentId).orElseThrow();
        document.setDocumentType(documentDTO.getDocumentType());
        document.setContent(documentDTO.getContent());
        document.setNotes(documentDTO.getNotes());
        document.setUpdatedAt(LocalDateTime.now());
        document = documentRepository.save(document);
        return DTOConverter.convertDocument(document);
    }
}
