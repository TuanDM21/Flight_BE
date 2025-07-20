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
    private AttachmentRepository attachmentRepository;

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setContent(task.getContent());
        dto.setInstructions(task.getInstructions());
        dto.setNotes(task.getNotes());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setCreatedBy(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null);
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        return dto;
    }

    private Task convertToEntity(TaskDTO dto) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setContent(dto.getContent());
        task.setInstructions(dto.getInstructions());
        task.setNotes(dto.getNotes());
        task.setCreatedAt(dto.getCreatedAt());
        task.setUpdatedAt(dto.getUpdatedAt());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
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
    /**
     * Tạo task với assignment và attachment trực tiếp
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Thay thế hoàn toàn logic dựa trên document bằng attachment trực tiếp
     */
    public TaskDTO createTaskWithAssignmentsAndAttachments(CreateTaskRequest request) {
        // Lấy user hiện tại từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User creator = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;

        // Tạo Task
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setContent(request.getContent());
        task.setInstructions(request.getInstructions());
        task.setNotes(request.getNotes());
        task.setPriority(request.getPriority() != null ? request.getPriority() : com.project.quanlycanghangkhong.model.TaskPriority.NORMAL);
        task.setStatus(com.project.quanlycanghangkhong.model.TaskStatus.OPEN); // ✅ Đảm bảo status được set
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        if (creator != null) task.setCreatedBy(creator);
        Task savedTask = taskRepository.save(task);

        // MỚI: Gán attachment trực tiếp vào task (THAY THẾ hoàn toàn logic document)
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            List<Attachment> attachments = attachmentRepository.findAllByIdIn(request.getAttachmentIds());
            for (Attachment attachment : attachments) {
                if (!attachment.isDeleted()) {
                    attachment.setTask(savedTask);
                }
            }
            attachmentRepository.saveAll(attachments);
        }

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
                assignment.setStatus(AssignmentStatus.WORKING);
                if (a.getDueAt() != null) {
                    assignment.setDueAt(new java.sql.Timestamp(a.getDueAt().getTime()).toLocalDateTime());
                }
                assignmentRepository.save(assignment);
            }
            // ✅ FIX: Cập nhật trạng thái task sau khi tạo assignments
            updateTaskStatus(savedTask);
        }

        return convertToDTO(savedTask);
    }

    @Override
    @Transactional
    public TaskDTO updateTask(Integer id, UpdateTaskDTO updateTaskDTO) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            
            // Cập nhật thông tin cơ bản
            if (updateTaskDTO.getTitle() != null) {
                task.setTitle(updateTaskDTO.getTitle());
            }
            task.setContent(updateTaskDTO.getContent());
            task.setInstructions(updateTaskDTO.getInstructions());
            task.setNotes(updateTaskDTO.getNotes());
            if (updateTaskDTO.getPriority() != null) {
                task.setPriority(updateTaskDTO.getPriority());
            }
            task.setUpdatedAt(LocalDateTime.now());
            
            // MỚI: Cập nhật attachment list
            if (updateTaskDTO.getAttachmentIds() != null) {
                updateTaskAttachments(task, updateTaskDTO.getAttachmentIds());
            }
            // null = không thay đổi attachment, chỉ cập nhật nội dung
            
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
    @Transactional
    public void bulkDeleteTasks(List<Integer> taskIds) {
        for (Integer taskId : taskIds) {
            Optional<Task> optionalTask = taskRepository.findById(taskId);
            if (optionalTask.isPresent()) {
                Task task = optionalTask.get();
                task.setDeleted(true);
                taskRepository.save(task);
            }
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
        dto.setTitle(task.getTitle());
        dto.setContent(task.getContent());
        dto.setInstructions(task.getInstructions());
        dto.setNotes(task.getNotes());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setStatus(task.getStatus()); // Mapping status enum
        dto.setPriority(task.getPriority());
        
        // NEW: Set parent ID if exists
        if (task.getParent() != null) {
            dto.setParentId(task.getParent().getId());
        }
        
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
        
        // NEW: Direct attachments
        List<AttachmentDTO> attachmentDTOs = new ArrayList<>();
        List<Attachment> directAttachments = attachmentRepository.findByTask_IdAndIsDeletedFalse(task.getId());
        for (Attachment att : directAttachments) {
            AttachmentDTO attDto = new AttachmentDTO();
            attDto.setId(att.getId());
            attDto.setFilePath(att.getFilePath());
            attDto.setFileName(att.getFileName());
            attDto.setFileSize(att.getFileSize());
            attDto.setCreatedAt(att.getCreatedAt());
            if (att.getUploadedBy() != null) {
                attDto.setUploadedBy(new UserDTO(att.getUploadedBy()));
            }
            attachmentDTOs.add(attDto);
        }
        dto.setAttachments(attachmentDTOs);
        
        // NEW: Subtasks
        List<TaskDetailDTO> subtaskDTOs = new ArrayList<>();
        List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(task.getId());
        for (Task subtask : subtasks) {
            TaskDetailDTO subtaskDto = getTaskDetailById(subtask.getId()); // Recursive call
            if (subtaskDto != null) {
                subtaskDTOs.add(subtaskDto);
            }
        }
        dto.setSubtasks(subtaskDTOs);
        
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

    // ✅ LOGIC MỚI - ĐƠN GIẢN: Cập nhật trạng thái Task dựa trên trạng thái các Assignment con
    public void updateTaskStatus(Task task) {
        List<Assignment> assignments = assignmentRepository.findAll().stream()
            .filter(a -> a.getTask().getId().equals(task.getId()))
            .collect(Collectors.toList());
            
        // Không có assignment nào → OPEN
        if (assignments == null || assignments.isEmpty()) {
            task.setStatus(TaskStatus.OPEN);
            taskRepository.save(task);
            return;
        }
        
        // Tất cả assignments đều DONE → COMPLETED  
        boolean allDone = assignments.stream()
                .allMatch(a -> a.getStatus() == AssignmentStatus.DONE);
                
        // Có ít nhất 1 assignment WORKING → IN_PROGRESS
        boolean anyWorking = assignments.stream()
                .anyMatch(a -> a.getStatus() == AssignmentStatus.WORKING);
        
        if (allDone) {
            task.setStatus(TaskStatus.COMPLETED);
        } else if (anyWorking) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            // Tất cả assignments đều CANCELLED → OPEN (task có thể assign lại)
            task.setStatus(TaskStatus.OPEN);
        }
        
        taskRepository.save(task);
    }

    // MÔ HÌNH ADJACENCY LIST: Triển khai các method subtask
    @Override
    @Transactional
    /**
     * Tạo subtask trong mô hình Adjacency List
     * MÔ HÌNH ADJACENCY LIST: Tạo task con với parent_id tham chiếu
     */
    public TaskDTO createSubtask(Integer parentId, CreateSubtaskRequest request) {
        // Lấy task cha
        Task parentTask = taskRepository.findByIdAndDeletedFalse(parentId).orElse(null);
        if (parentTask == null) {
            throw new RuntimeException("Không tìm thấy task cha: " + parentId);
        }

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User creator = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;

        // Create subtask
        Task subtask = new Task();
        subtask.setTitle(request.getTitle());
        subtask.setContent(request.getContent());
        subtask.setInstructions(request.getInstructions());
        subtask.setNotes(request.getNotes());
        subtask.setPriority(request.getPriority() != null ? request.getPriority() : com.project.quanlycanghangkhong.model.TaskPriority.NORMAL);
        subtask.setParent(parentTask);
        subtask.setCreatedAt(LocalDateTime.now());
        subtask.setUpdatedAt(LocalDateTime.now());
        if (creator != null) subtask.setCreatedBy(creator);
        Task savedSubtask = taskRepository.save(subtask);

        // Assign direct attachments if provided
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            List<Attachment> attachments = attachmentRepository.findAllByIdIn(request.getAttachmentIds());
            for (Attachment attachment : attachments) {
                if (!attachment.isDeleted()) {
                    attachment.setTask(savedSubtask);
                }
            }
            attachmentRepository.saveAll(attachments);
        }

        // Create assignments for subtask
        if (request.getAssignments() != null) {
            for (AssignmentRequest a : request.getAssignments()) {
                Assignment assignment = new Assignment();
                assignment.setTask(savedSubtask);
                assignment.setRecipientType(a.getRecipientType());
                assignment.setRecipientId(a.getRecipientId());
                assignment.setNote(a.getNote());
                assignment.setAssignedAt(LocalDateTime.now());
                assignment.setAssignedBy(creator);
                assignment.setStatus(AssignmentStatus.WORKING);
                if (a.getDueAt() != null) {
                    assignment.setDueAt(new java.sql.Timestamp(a.getDueAt().getTime()).toLocalDateTime());
                }
                assignmentRepository.save(assignment);
            }
            updateTaskStatus(savedSubtask);
        }

        return convertToDTO(savedSubtask);
    }

    @Override
    public List<TaskDetailDTO> getSubtasks(Integer parentId) {
        List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(parentId);
        return subtasks.stream()
            .map(task -> getTaskDetailById(task.getId()))
            .filter(taskDetail -> taskDetail != null)
            .collect(Collectors.toList());
    }

    @Override
    public List<TaskDetailDTO> getRootTasks() {
        List<Task> rootTasks = taskRepository.findByParentIsNullAndDeletedFalse();
        return rootTasks.stream()
            .map(task -> getTaskDetailById(task.getId()))
            .filter(taskDetail -> taskDetail != null)
            .collect(Collectors.toList());
    }

    // === ATTACHMENT MANAGEMENT ===
    // Đã loại bỏ assignAttachmentsToTask và removeAttachmentsFromTask
    // Attachment chỉ được quản lý thông qua createTask và updateTask
    
    /*
    // ❌ KHÔNG CẦN: Đã thay thế bằng logic trong createTask và updateTask
    @Override
    @Transactional
    public void assignAttachmentsToTask(Integer taskId, List<Integer> attachmentIds) {
        Task task = taskRepository.findByIdAndDeletedFalse(taskId).orElse(null);
        if (task == null) {
            throw new RuntimeException("Task not found: " + taskId);
        }

        List<Attachment> attachments = attachmentRepository.findAllByIdIn(attachmentIds);
        for (Attachment attachment : attachments) {
            if (!attachment.isDeleted()) {
                attachment.setTask(task);
            }
        }
        attachmentRepository.saveAll(attachments);
    }

    @Override
    @Transactional
    public void removeAttachmentsFromTask(Integer taskId, List<Integer> attachmentIds) {
        List<Attachment> attachments = attachmentRepository.findAllByIdIn(attachmentIds);
        for (Attachment attachment : attachments) {
            if (attachment.getTask() != null && attachment.getTask().getId().equals(taskId)) {
                attachment.setTask(null);
            }
        }
        attachmentRepository.saveAll(attachments);
    }
    */

    @Override
    public List<AttachmentDTO> getTaskAttachments(Integer taskId) {
        List<Attachment> attachments = attachmentRepository.findByTask_IdAndIsDeletedFalse(taskId);
        return attachments.stream()
            .map(att -> {
                AttachmentDTO dto = new AttachmentDTO();
                dto.setId(att.getId());
                dto.setFilePath(att.getFilePath());
                dto.setFileName(att.getFileName());
                dto.setFileSize(att.getFileSize());
                dto.setCreatedAt(att.getCreatedAt());
                if (att.getUploadedBy() != null) {
                    dto.setUploadedBy(new UserDTO(att.getUploadedBy()));
                }
                return dto;
            })
            .collect(Collectors.toList());
    }

    // ============== SEARCH & FILTER IMPLEMENTATIONS ==============

    @Override
    public List<TaskDetailDTO> searchTasksByTitle(String title) {
        List<Task> tasks = taskRepository.findByTitleContainingIgnoreCaseAndDeletedFalse(title);
        return tasks.stream()
            .map(task -> getTaskDetailById(task.getId()))
            .filter(taskDetail -> taskDetail != null)
            .collect(Collectors.toList());
    }

    @Override
    public List<TaskDetailDTO> getTasksByPriority(com.project.quanlycanghangkhong.model.TaskPriority priority) {
        List<Task> tasks = taskRepository.findByPriorityAndDeletedFalse(priority);
        return tasks.stream()
            .map(task -> getTaskDetailById(task.getId()))
            .filter(taskDetail -> taskDetail != null)
            .collect(Collectors.toList());
    }

    @Override
    public List<TaskDetailDTO> searchTasks(String keyword) {
        List<Task> tasks = taskRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDeletedFalse(keyword, keyword);
        return tasks.stream()
            .map(task -> getTaskDetailById(task.getId()))
            .filter(taskDetail -> taskDetail != null)
            .collect(Collectors.toList());
    }

    /**
     * Cập nhật danh sách attachment của task
     * Logic: 
     * - null: không thay đổi 
     * - empty list: xóa hết attachment
     * - có giá trị: replace toàn bộ attachment list
     * @param task Task cần cập nhật
     * @param attachmentIds Danh sách attachment ID mới
     */
    private void updateTaskAttachments(Task task, List<Integer> attachmentIds) {
        // Lấy danh sách attachment hiện tại của task
        List<Attachment> currentAttachments = attachmentRepository.findByTask_IdAndIsDeletedFalse(task.getId());
        
        // Gỡ tất cả attachment hiện tại khỏi task
        for (Attachment attachment : currentAttachments) {
            attachment.setTask(null);
        }
        attachmentRepository.saveAll(currentAttachments);
        
        // Nếu có attachment mới, gán vào task
        if (!attachmentIds.isEmpty()) {
            List<Attachment> newAttachments = attachmentRepository.findAllByIdIn(attachmentIds);
            for (Attachment attachment : newAttachments) {
                if (!attachment.isDeleted()) {
                    attachment.setTask(task);
                }
            }
            attachmentRepository.saveAll(newAttachments);
        }
        // Nếu attachmentIds empty = xóa hết attachment (đã làm ở trên)
    }
}
