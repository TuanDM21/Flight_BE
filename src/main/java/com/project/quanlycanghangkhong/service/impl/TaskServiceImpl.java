package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.TaskRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.project.quanlycanghangkhong.dto.*;
import com.project.quanlycanghangkhong.model.*;
import com.project.quanlycanghangkhong.repository.*;
import org.springframework.transaction.annotation.Transactional;

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
                assignment.setStatus(AssignmentStatus.ASSIGNED);
                if (a.getDueAt() != null) {
                    assignment.setDueAt(new java.sql.Timestamp(a.getDueAt().getTime()).toLocalDateTime());
                }
                assignmentRepository.save(assignment);
            }
            // ✅ FIX: Cập nhật trạng thái task sau khi tạo assignments
            updateTaskStatus(savedTask);
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
    public TaskDTO updateTask(Integer id, UpdateTaskDTO updateTaskDTO) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setContent(updateTaskDTO.getContent());
            task.setInstructions(updateTaskDTO.getInstructions());
            task.setNotes(updateTaskDTO.getNotes());
            task.setUpdatedAt(LocalDateTime.now());
            Task updated = taskRepository.save(task);
            return convertToDTO(updated);
        }
        return null;
    }

    @Override
    public void deleteTask(Integer id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setDeleted(true);
            taskRepository.save(task);
        }
    }

    @Override
    public TaskDTO getTaskById(Integer id) {
        return taskRepository.findByIdAndDeletedFalse(id).map(this::convertToDTO).orElse(null);
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAllByDeletedFalse().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public TaskDetailDTO getTaskDetailById(Integer id) {
        Task task = taskRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (task == null) return null;
        TaskDetailDTO dto = new TaskDetailDTO();
        dto.setId(task.getId());
        dto.setContent(task.getContent());
        dto.setInstructions(task.getInstructions());
        dto.setNotes(task.getNotes());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setStatus(task.getStatus()); // Mapping status enum
        if (task.getCreatedBy() != null) {
            dto.setCreatedByUser(new UserDTO(task.getCreatedBy()));
        }
        // Assignments
        List<AssignmentDTO> assignmentDTOs = assignmentRepository.findAll().stream()
            .filter(a -> a.getTask().getId().equals(task.getId()))
            .map(a -> {
                AssignmentDTO adto = new AssignmentDTO();
                adto.setAssignmentId(a.getAssignmentId());
                adto.setRecipientType(a.getRecipientType());
                adto.setRecipientId(a.getRecipientId());
                adto.setTaskId(a.getTask() != null ? a.getTask().getId() : null);
                if (a.getAssignedBy() != null) {
                    adto.setAssignedByUser(new UserDTO(a.getAssignedBy()));
                }
                adto.setAssignedAt(a.getAssignedAt() != null ? java.sql.Timestamp.valueOf(a.getAssignedAt()) : null);
                adto.setDueAt(a.getDueAt() != null ? java.sql.Timestamp.valueOf(a.getDueAt()) : null);
                adto.setNote(a.getNote());
                adto.setCompletedAt(a.getCompletedAt() != null ? java.sql.Timestamp.valueOf(a.getCompletedAt()) : null);
                if (a.getCompletedBy() != null) {
                    adto.setCompletedByUser(new UserDTO(a.getCompletedBy()));
                }
                adto.setStatus(a.getStatus());
                // recipientUser: user, team, unit
                if ("user".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null) {
                    userRepository.findById(a.getRecipientId()).ifPresent(u -> adto.setRecipientUser(new UserDTO(u)));
                } else if ("team".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null) {
                    userRepository.findTeamLeadByTeamId(a.getRecipientId()).ifPresent(u -> adto.setRecipientUser(new UserDTO(u)));
                } else if ("unit".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null) {
                    userRepository.findUnitLeadByUnitId(a.getRecipientId()).ifPresent(u -> adto.setRecipientUser(new UserDTO(u)));
                }
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
        return taskRepository.findAllByDeletedFalse().stream()
            .map(task -> getTaskDetailById(task.getId()))
            .toList();
    }

    @Override
    public List<TaskDetailDTO> getMyTasks(String type) {
        // Lấy user hiện tại từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return List.of();
        }
        
        Integer currentUserId = currentUser.getId();
        List<Task> tasks = new ArrayList<>();
        
        switch (type.toLowerCase()) {
            case "created":
                // Công việc đã tạo của tôi
                tasks = taskRepository.findAllByDeletedFalse().stream()
                    .filter(task -> task.getCreatedBy() != null && 
                           task.getCreatedBy().getId().equals(currentUserId))
                    .collect(Collectors.toList());
                break;
                
            case "assigned":
                // Công việc đã giao của tôi (tôi là assignedBy trong Assignment)
                List<Assignment> assignedByMe = assignmentRepository.findAll().stream()
                    .filter(assignment -> assignment.getAssignedBy() != null && 
                           assignment.getAssignedBy().getId().equals(currentUserId))
                    .collect(Collectors.toList());
                
                Set<Integer> assignedTaskIds = assignedByMe.stream()
                    .map(assignment -> assignment.getTask().getId())
                    .collect(Collectors.toSet());
                
                tasks = taskRepository.findAllByDeletedFalse().stream()
                    .filter(task -> assignedTaskIds.contains(task.getId()))
                    .collect(Collectors.toList());
                break;
                
            case "received":
                // Công việc tôi được giao (tôi là recipient trong Assignment)
                List<Assignment> assignedToMe = assignmentRepository.findAll().stream()
                    .filter(assignment -> {
                        if ("user".equalsIgnoreCase(assignment.getRecipientType())) {
                            // Trực tiếp giao cho user
                            return assignment.getRecipientId() != null && 
                                   assignment.getRecipientId().equals(currentUserId);
                        } else if ("team".equalsIgnoreCase(assignment.getRecipientType())) {
                            // Chỉ TEAM_LEAD mới được nhận công việc giao cho team (không bao gồm TEAM_VICE_LEAD)
                            if (currentUser.getRole() != null && 
                                "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                                currentUser.getTeam() != null &&
                                assignment.getRecipientId() != null) {
                                return currentUser.getTeam().getId().equals(assignment.getRecipientId());
                            }
                            return false;
                        } else if ("unit".equalsIgnoreCase(assignment.getRecipientType())) {
                            // Chỉ UNIT_LEAD mới được nhận công việc giao cho unit (không bao gồm UNIT_VICE_LEAD)
                            if (currentUser.getRole() != null && 
                                "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                                currentUser.getUnit() != null &&
                                assignment.getRecipientId() != null) {
                                return currentUser.getUnit().getId().equals(assignment.getRecipientId());
                            }
                            return false;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
                
                Set<Integer> receivedTaskIds = assignedToMe.stream()
                    .map(assignment -> assignment.getTask().getId())
                    .collect(Collectors.toSet());
                
                tasks = taskRepository.findAllByDeletedFalse().stream()
                    .filter(task -> receivedTaskIds.contains(task.getId()))
                    .collect(Collectors.toList());
                break;
                
            default:
                return List.of();
        }
        
        return tasks.stream()
            .map(task -> getTaskDetailById(task.getId()))
            .filter(taskDetail -> taskDetail != null)
            .collect(Collectors.toList());
    }

    // Logic cập nhật trạng thái Task dựa trên trạng thái các Assignment con
    public void updateTaskStatus(Task task) {
        List<Assignment> assignments = assignmentRepository.findAll().stream()
            .filter(a -> a.getTask().getId().equals(task.getId()))
            .collect(Collectors.toList());
        if (assignments == null || assignments.isEmpty()) {
            task.setStatus(TaskStatus.NEW);
            taskRepository.save(task);
            return;
        }
        boolean allCancelled = assignments.stream()
                .allMatch(a -> a.getStatus() == AssignmentStatus.CANCELLED);
        boolean allCompletedOrLate = assignments.stream()
                .allMatch(a -> a.getStatus() == AssignmentStatus.COMPLETED
                        || a.getStatus() == AssignmentStatus.LATE_COMPLETED);
        boolean anyLate = assignments.stream()
                .anyMatch(a -> a.getStatus() == AssignmentStatus.LATE_COMPLETED);
        boolean anyCompletedOrLate = assignments.stream()
                .anyMatch(a -> a.getStatus() == AssignmentStatus.COMPLETED
                        || a.getStatus() == AssignmentStatus.LATE_COMPLETED);
        boolean anyNotCompleted = assignments.stream()
                .anyMatch(a -> a.getStatus() != AssignmentStatus.COMPLETED && a.getStatus() != AssignmentStatus.LATE_COMPLETED && a.getStatus() != AssignmentStatus.CANCELLED);

        if (allCancelled) {
            task.setStatus(TaskStatus.CANCELLED);
        } else if (allCompletedOrLate) {
            if (anyLate) {
                task.setStatus(TaskStatus.LATE_COMPLETED);
            } else {
                task.setStatus(TaskStatus.COMPLETED);
            }
        } else if (anyCompletedOrLate && anyNotCompleted) {
            task.setStatus(TaskStatus.PARTIALLY_COMPLETED);
        } else {
            boolean anySubmittedOrReviewing = assignments.stream()
                    .anyMatch(a -> a.getStatus() == AssignmentStatus.SUBMITTED
                            || a.getStatus() == AssignmentStatus.REVIEWING);
            boolean anyInProgress = assignments.stream()
                    .anyMatch(a -> a.getStatus() == AssignmentStatus.IN_PROGRESS);
            if (anySubmittedOrReviewing) {
                task.setStatus(TaskStatus.UNDER_REVIEW);
            } else if (anyInProgress) {
                task.setStatus(TaskStatus.IN_PROGRESS);
            } else {
                task.setStatus(TaskStatus.ASSIGNED);
            }
        }
        taskRepository.save(task);
    }
}
