package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.TaskRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.quanlycanghangkhong.dto.*;
import com.project.quanlycanghangkhong.model.*;
import com.project.quanlycanghangkhong.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TaskDocumentRepository taskDocumentRepository;

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setContent(task.getContent());
        dto.setInstructions(task.getInstructions());
        dto.setNotes(task.getNotes());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setCreatedBy(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null);
        return dto;
    }

    private Task convertToEntity(TaskDTO dto) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setContent(dto.getContent());
        task.setInstructions(dto.getInstructions());
        task.setNotes(dto.getNotes());
        task.setCreatedAt(dto.getCreatedAt());
        task.setUpdatedAt(dto.getUpdatedAt());
        if (dto.getCreatedBy() != null) {
            Optional<User> userOpt = userRepository.findById(dto.getCreatedBy());
            userOpt.ifPresent(task::setCreatedBy);
        } else {
            task.setCreatedBy(null);
        }
        return task;
    }

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = convertToEntity(taskDTO);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        Task saved = taskRepository.save(task);
        return convertToDTO(saved);
    }

    @Transactional
    @Override
    public TaskDTO createTaskWithAssignmentsAndDocuments(CreateTaskRequest request) {
        // Lấy user hiện tại từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User creator = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;

        // Tạo Task
        Task task = new Task();
        task.setContent(request.getContent());
        task.setInstructions(request.getInstructions());
        task.setNotes(request.getNotes());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        if (creator != null) task.setCreatedBy(creator);
        Task savedTask = taskRepository.save(task);

        // Tạo Assignment
        if (request.getAssignments() != null) {
            for (AssignmentRequest a : request.getAssignments()) {
                Assignment assignment = new Assignment();
                assignment.setTask(savedTask);
                assignment.setRecipientType(a.getRecipientType());
                assignment.setRecipientId(a.getRecipientId());
                assignment.setNote(a.getNote());
                assignment.setAssignedAt(LocalDateTime.now());
                assignment.setAssignedBy(creator); // Đảm bảo luôn set người giao việc
                assignment.setStatus(0);
                if (a.getDueAt() != null) {
                    assignment.setDueAt(new java.sql.Timestamp(a.getDueAt().getTime()).toLocalDateTime());
                }
                assignmentRepository.save(assignment);
            }
        }

        // Liên kết documentIds với task thông qua TaskDocument
        if (request.getDocumentIds() != null) {
            for (Integer docId : request.getDocumentIds()) {
                Document doc = documentRepository.findById(docId).orElse(null);
                if (doc != null) {
                    TaskDocument taskDocument = new TaskDocument();
                    taskDocument.setTask(savedTask);
                    taskDocument.setDocument(doc);
                    taskDocument.setCreatedAt(LocalDateTime.now());
                    taskDocumentRepository.save(taskDocument);
                }
            }
        }
        return convertToDTO(savedTask);
    }

    @Override
    public TaskDTO updateTask(Integer id, TaskDTO taskDTO) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setContent(taskDTO.getContent());
            task.setInstructions(taskDTO.getInstructions());
            task.setNotes(taskDTO.getNotes());
            task.setUpdatedAt(LocalDateTime.now());
            if (taskDTO.getCreatedBy() != null) {
                Optional<User> userOpt = userRepository.findById(taskDTO.getCreatedBy());
                userOpt.ifPresent(task::setCreatedBy);
            }
            Task updated = taskRepository.save(task);
            return convertToDTO(updated);
        }
        return null;
    }

    @Override
    public void deleteTask(Integer id) {
        taskRepository.deleteById(id);
    }

    @Override
    public TaskDTO getTaskById(Integer id) {
        return taskRepository.findById(id).map(this::convertToDTO).orElse(null);
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public TaskDetailDTO getTaskDetailById(Integer id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null) return null;
        TaskDetailDTO dto = new TaskDetailDTO();
        dto.setId(task.getId());
        dto.setContent(task.getContent());
        dto.setInstructions(task.getInstructions());
        dto.setNotes(task.getNotes());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setCreatedBy(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null);
        // Assignments
        List<AssignmentDTO> assignmentDTOs = assignmentRepository.findAll().stream()
            .filter(a -> a.getTask().getId().equals(task.getId()))
            .map(a -> {
                AssignmentDTO adto = new AssignmentDTO();
                adto.setAssignmentId(a.getAssignmentId());
                adto.setRecipientId(a.getRecipientId());
                adto.setRecipientType(a.getRecipientType());
                adto.setAssignedBy(a.getAssignedBy() != null ? a.getAssignedBy().getId() : null);
                adto.setAssignedAt(a.getAssignedAt() != null ? java.sql.Timestamp.valueOf(a.getAssignedAt()) : null);
                adto.setDueAt(a.getDueAt() != null ? java.sql.Timestamp.valueOf(a.getDueAt()) : null);
                adto.setNote(a.getNote());
                adto.setCompletedAt(a.getCompletedAt() != null ? java.sql.Timestamp.valueOf(a.getCompletedAt()) : null);
                adto.setCompletedBy(a.getCompletedBy() != null ? a.getCompletedBy().getId() : null);
                adto.setStatus(a.getStatus());
                return adto;
            }).toList();
        dto.setAssignments(assignmentDTOs);
        // Documents (qua TaskDocument)
        List<TaskDocument> taskDocuments = taskDocumentRepository.findAll().stream()
            .filter(td -> td.getTask().getId().equals(task.getId()))
            .toList();
        List<DocumentDetailDTO> documentDTOs = new ArrayList<>();
        for (TaskDocument td : taskDocuments) {
            Document doc = td.getDocument();
            DocumentDetailDTO dDto = new DocumentDetailDTO();
            dDto.setId(doc.getId());
            dDto.setDocumentType(doc.getDocumentType());
            dDto.setContent(doc.getContent());
            dDto.setNotes(doc.getNotes());
            dDto.setCreatedAt(doc.getCreatedAt());
            dDto.setUpdatedAt(doc.getUpdatedAt());
            // Attachments
            List<AttachmentDTO> attDTOs = new ArrayList<>();
            if (doc.getAttachments() != null) {
                for (Attachment att : doc.getAttachments()) {
                    AttachmentDTO attDto = new AttachmentDTO();
                    attDto.setFilePath(att.getFilePath());
                    attDto.setFileName(att.getFileName());
                    attDto.setFileSize(att.getFileSize());
                    attDTOs.add(attDto);
                }
            }
            dDto.setAttachments(attDTOs);
            documentDTOs.add(dDto);
        }
        dto.setDocuments(documentDTOs);
        return dto;
    }

    @Override
    public List<TaskDetailDTO> getAllTaskDetails() {
        return taskRepository.findAll().stream()
            .map(task -> getTaskDetailById(task.getId()))
            .toList();
    }
}
