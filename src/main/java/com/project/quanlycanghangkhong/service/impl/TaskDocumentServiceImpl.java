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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskDocumentServiceImpl implements TaskDocumentService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private TaskDocumentRepository taskDocumentRepository;

    @Override
    public void removeDocumentFromTask(Integer taskId, Integer documentId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        Document document = documentRepository.findById(documentId).orElseThrow();
        List<TaskDocument> taskDocuments = taskDocumentRepository.findAllByTaskAndDocument(task, document);
        for (TaskDocument td : taskDocuments) {
            taskDocumentRepository.delete(td);
        }
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

    @Override
    public List<DocumentDTO> getDocumentsByTaskId(Integer taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        List<TaskDocument> taskDocuments = taskDocumentRepository.findAll();
        return taskDocuments.stream()
                .filter(td -> td.getTask().getId().equals(taskId))
                .map(td -> DTOConverter.convertDocument(td.getDocument()))
                .collect(Collectors.toList());
    }

    @Override
    public void attachDocumentToTask(Integer taskId, Integer documentId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        Document document = documentRepository.findById(documentId).orElseThrow();
        // Kiểm tra trùng lặp trước khi tạo mới
        boolean exists = !taskDocumentRepository.findAllByTaskAndDocument(task, document).isEmpty();
        if (!exists) {
            TaskDocument taskDocument = new TaskDocument();
            taskDocument.setTask(task);
            taskDocument.setDocument(document);
            taskDocument.setCreatedAt(LocalDateTime.now());
            taskDocumentRepository.save(taskDocument);
        }
    }

    @Override
    public void bulkAttachDocumentsToTask(Integer taskId, List<Integer> documentIds) {
        for (Integer documentId : documentIds) {
            attachDocumentToTask(taskId, documentId);
        }
    }

    @Override
    public void bulkRemoveDocumentsFromTask(Integer taskId, List<Integer> documentIds) {
        for (Integer documentId : documentIds) {
            removeDocumentFromTask(taskId, documentId);
        }
    }
}
