package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.TaskRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.TaskService;

// ✅ PRIORITY 3: Simplified DTOs imports
import com.project.quanlycanghangkhong.dto.simplified.TaskDetailSimplifiedDTO;
import com.project.quanlycanghangkhong.dto.simplified.SimpleAssignmentDTO;
import com.project.quanlycanghangkhong.dto.simplified.SimpleAttachmentDTO;

// ✅ Pagination imports
import com.project.quanlycanghangkhong.dto.response.task.PaginationInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
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

    @Autowired
    private com.project.quanlycanghangkhong.repository.TeamRepository teamRepository;

    @Autowired
    private com.project.quanlycanghangkhong.repository.UnitRepository unitRepository;

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
            if (updateTaskDTO.getContent() != null) {
                task.setContent(updateTaskDTO.getContent());
            }
            if (updateTaskDTO.getInstructions() != null) {
                task.setInstructions(updateTaskDTO.getInstructions());
            }
            if (updateTaskDTO.getNotes() != null) {
                task.setNotes(updateTaskDTO.getNotes());
            }
            if (updateTaskDTO.getPriority() != null) {
                task.setPriority(updateTaskDTO.getPriority());
            }
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
        // ✅ Sử dụng JOIN FETCH để load tất cả relationships trong 1 query
        Task task = taskRepository.findTaskWithAllRelationships(id).orElse(null);
        if (task == null) return null;
        
        // ✅ Sử dụng depth-controlled version bắt đầu từ depth 0
        return convertToTaskDetailDTOOptimized(task, 0);
    }

    // ✅ PRIORITY 3: New method using Simplified DTOs
    public TaskDetailSimplifiedDTO getTaskDetailSimplifiedById(Integer id) {
        Task task = taskRepository.findTaskWithAllRelationships(id).orElse(null);
        if (task == null) return null;
        
        return convertToTaskDetailSimplifiedDTO(task, 0);
    }

    // ✅ OPTIMIZED: Convert Task to TaskDetailDTO as normal task (no hierarchy)
    private TaskDetailDTO convertToTaskDetailDTOOptimized(Task task, int currentDepth) {
        // ✅ Convert base task info
        TaskDetailDTO dto = new TaskDetailDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setContent(task.getContent());
        dto.setInstructions(task.getInstructions());
        dto.setNotes(task.getNotes());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        
        // Keep parentId for reference only (no nesting)
        if (task.getParent() != null) {
            dto.setParentId(task.getParent().getId());
        }
        
        // ✅ FLAT LIST: No need to set hierarchy fields as they are @JsonIgnore
        // These fields won't appear in JSON response anymore
        
        if (task.getCreatedBy() != null) {
            dto.setCreatedByUser(new UserDTO(task.getCreatedBy()));
        }
        
        // ✅ Load assignments riêng để tránh lazy loading issues
        List<Assignment> assignments = assignmentRepository.findByTaskId(task.getId());
        List<AssignmentDTO> assignmentDTOs = assignments.stream()
            .map(this::convertToAssignmentDTOOptimized)
            .toList();
        dto.setAssignments(assignmentDTOs);
        
        // ✅ Load attachments riêng để tránh MultipleBagFetchException
        List<AttachmentDTO> attachmentDTOs = attachmentRepository.findByTask_IdAndIsDeletedFalse(task.getId())
            .stream()
            .map(this::convertToAttachmentDTOOptimized)
            .toList();
        dto.setAttachments(attachmentDTOs);
        
        return dto;
    }

    // ✅ Helper method convert Assignment without additional queries
    private AssignmentDTO convertToAssignmentDTOOptimized(Assignment a) {
        AssignmentDTO adto = new AssignmentDTO();
        adto.setAssignmentId(a.getAssignmentId());
        adto.setRecipientType(a.getRecipientType());
        adto.setRecipientId(a.getRecipientId());
        adto.setTaskId(a.getTask() != null ? a.getTask().getId() : null);
        
        // ✅ AssignedBy đã được fetch với JOIN
        if (a.getAssignedBy() != null) {
            adto.setAssignedByUser(new UserDTO(a.getAssignedBy()));
        }
        
        adto.setAssignedAt(a.getAssignedAt() != null ? java.sql.Timestamp.valueOf(a.getAssignedAt()) : null);
        adto.setDueAt(a.getDueAt() != null ? java.sql.Timestamp.valueOf(a.getDueAt()) : null);
        adto.setNote(a.getNote());
        adto.setCompletedAt(a.getCompletedAt() != null ? java.sql.Timestamp.valueOf(a.getCompletedAt()) : null);
        
        // ✅ CompletedBy đã được fetch với JOIN
        if (a.getCompletedBy() != null) {
            adto.setCompletedByUser(new UserDTO(a.getCompletedBy()));
        }
        
        adto.setStatus(a.getStatus());
        
        // ⚠️ Recipient user cần query riêng - tối ưu bằng cache hoặc batch query
        setRecipientUserOptimized(adto, a.getRecipientType(), a.getRecipientId());
        
        return adto;
    }

    // ✅ Helper method convert Attachment
    private AttachmentDTO convertToAttachmentDTOOptimized(Attachment att) {
        AttachmentDTO attDto = new AttachmentDTO();
        attDto.setId(att.getId());
        attDto.setFilePath(att.getFilePath());
        attDto.setFileName(att.getFileName());
        attDto.setFileSize(att.getFileSize());
        attDto.setCreatedAt(att.getCreatedAt());
        attDto.setSharedCount(0); // Set default value for sharedCount
        
        if (att.getUploadedBy() != null) {
            attDto.setUploadedBy(new UserDTO(att.getUploadedBy()));
        }
        
        return attDto;
    }

    // ✅ Optimize recipient user loading with team/unit info
    private void setRecipientUserOptimized(AssignmentDTO adto, String recipientType, Integer recipientId) {
        if (recipientId == null) return;
        
        switch (recipientType.toLowerCase()) {
            case "user":
                userRepository.findById(recipientId)
                    .ifPresent(u -> adto.setRecipientUser(new UserDTO(u)));
                break;
            case "team":
                teamRepository.findById(recipientId).ifPresent(team -> {
                    adto.setRecipientTeamName(team.getTeamName());
                    userRepository.findTeamLeadByTeamId(team.getId()).ifPresent(teamLead -> {
                        adto.setRecipientTeamLead(new UserDTO(teamLead));
                    });
                });
                break;
            case "unit":
                unitRepository.findById(recipientId).ifPresent(unit -> {
                    adto.setRecipientUnitName(unit.getUnitName());
                    userRepository.findUnitLeadByUnitId(unit.getId()).ifPresent(unitLead -> {
                        adto.setRecipientUnitLead(new UserDTO(unitLead));
                    });
                });
                break;
        }
        // Note: This method is used for single conversions - batch conversion uses pre-loaded data
    }

    @Override
    public List<TaskDetailDTO> getAllTaskDetails() {
        // ✅ Tối ưu: Load tasks với batch processing để tránh N+1
        List<Task> tasks = taskRepository.findAllByDeletedFalse();
        return tasks.stream()
            .map(task -> {
                // Load task với relationships nếu chưa được fetch
                Task taskWithRelations = taskRepository.findTaskWithAllRelationships(task.getId()).orElse(task);
                return convertToTaskDetailDTOOptimized(taskWithRelations, 0);
            })
            .toList();
    }

    @Override
    public List<TaskDetailDTO> getMyTasks(String type) {
        // Get current user from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return List.of();
        }
        
        Integer currentUserId = currentUser.getId();
        
        // ✅ SIMPLIFIED: Get tasks without complex subtask/root filtering
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithoutAssignments(currentUserId);
                break;
            case "assigned":
                tasks = taskRepository.findAssignedTasksByUserId(currentUserId);
                break;
            case "received":
                List<Task> receivedTasks = new ArrayList<>();
                receivedTasks.addAll(taskRepository.findReceivedTasksByUserId(currentUserId));
                
                if (currentUser.getRole() != null && 
                    "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getTeam() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByTeamId(currentUser.getTeam().getId()));
                }
                
                if (currentUser.getRole() != null && 
                    "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getUnit() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(currentUser.getUnit().getId()));
                }
                
                tasks = receivedTasks.stream().distinct().collect(Collectors.toList());
                break;
            default:
                tasks = List.of();
        }
        
        if (tasks.isEmpty()) {
            return List.of();
        }
        
        // ✅ Convert all tasks to DTOs using simple batch conversion
        return convertTasksToTaskDetailDTOsBatch(tasks);
    }

    // ✅ LOGIC MỚI - ĐƠN GIẢN: Cập nhật trạng thái Task dựa trên trạng thái các Assignment con
    public void updateTaskStatus(Task task) {
        // ✅ Sử dụng repository method thay vì findAll + filter
        List<Assignment> assignments = assignmentRepository.findByTaskId(task.getId());
            
        // Không có assignment nào → OPEN
        if (assignments == null || assignments.isEmpty()) {
            task.setStatus(TaskStatus.OPEN);
            taskRepository.save(task);
            return;
        }
        
        // Check for overdue assignments first (highest priority)
        LocalDateTime now = LocalDateTime.now();
        boolean hasOverdueAssignments = assignments.stream()
                .anyMatch(a -> a.getDueAt() != null && 
                              a.getDueAt().isBefore(now) && 
                              a.getStatus() != AssignmentStatus.DONE);
        
        // Tất cả assignments đều DONE → COMPLETED  
        boolean allDone = assignments.stream()
                .allMatch(a -> a.getStatus() == AssignmentStatus.DONE);
                
        // Có ít nhất 1 assignment WORKING → IN_PROGRESS
        boolean anyWorking = assignments.stream()
                .anyMatch(a -> a.getStatus() == AssignmentStatus.WORKING);
        
        // Priority logic: OVERDUE > COMPLETED > IN_PROGRESS > OPEN
        if (hasOverdueAssignments && !allDone) {
            task.setStatus(TaskStatus.OVERDUE);
        } else if (allDone) {
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

    @Override
    public List<TaskDetailDTO> getTaskSubtree(Integer taskId) {
        List<TaskDetailDTO> result = new ArrayList<>();
        
        // Lấy task gốc trước
        TaskDetailDTO rootTask = getTaskDetailById(taskId);
        if (rootTask == null) {
            return result; // Trả về list rỗng nếu không tìm thấy task
        }
        
        // Thêm task gốc vào kết quả
        result.add(rootTask);
        
        // Recursively lấy tất cả subtask
        collectSubtasks(taskId, result);
        
        return result;
    }
    
    /**
     * Helper method để recursively collect tất cả subtask
     * @param parentId ID của task cha
     * @param result List để chứa kết quả
     */
    private void collectSubtasks(Integer parentId, List<TaskDetailDTO> result) {
        List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(parentId);
        
        for (Task subtask : subtasks) {
            TaskDetailDTO subtaskDetail = getTaskDetailById(subtask.getId());
            if (subtaskDetail != null) {
                result.add(subtaskDetail);
                // Recursively lấy subtask của subtask này
                collectSubtasks(subtask.getId(), result);
            }
        }
    }

    @Override
    public com.project.quanlycanghangkhong.dto.response.task.TaskTreeDTO getTaskSubtreeHierarchical(Integer taskId) {
        // Lấy task gốc
        TaskDetailDTO rootTask = getTaskDetailById(taskId);
        if (rootTask == null) {
            return null;
        }
        
        // Tạo TaskTreeDTO từ task gốc
        com.project.quanlycanghangkhong.dto.response.task.TaskTreeDTO rootTreeTask = 
            new com.project.quanlycanghangkhong.dto.response.task.TaskTreeDTO(rootTask, 0);
        
        // Recursively build subtree
        buildTaskTree(rootTreeTask, taskId, 1);
        
        return rootTreeTask;
    }
    
    /**
     * Helper method để recursively build task tree với cấu trúc nested
     * @param parentTreeTask Task cha trong cấu trúc tree
     * @param parentId ID của task cha trong database
     * @param level Level hiện tại trong tree
     */
    private void buildTaskTree(com.project.quanlycanghangkhong.dto.response.task.TaskTreeDTO parentTreeTask, 
                              Integer parentId, Integer level) {
        List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(parentId);
        
        for (Task subtask : subtasks) {
            TaskDetailDTO subtaskDetail = getTaskDetailById(subtask.getId());
            if (subtaskDetail != null) {
                // Tạo TaskTreeDTO cho subtask
                com.project.quanlycanghangkhong.dto.response.task.TaskTreeDTO subtaskTreeDTO = 
                    new com.project.quanlycanghangkhong.dto.response.task.TaskTreeDTO(subtaskDetail, level);
                
                // Thêm vào parent
                parentTreeTask.addSubtask(subtaskTreeDTO);
                
                // Recursively build cho subtask này
                buildTaskTree(subtaskTreeDTO, subtask.getId(), level + 1);
            }
        }
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

    @Override
    public List<com.project.quanlycanghangkhong.dto.simplified.SimpleAttachmentDTO> getTaskAttachmentsSimplified(Integer taskId) {
        List<Attachment> attachments = attachmentRepository.findByTask_IdAndIsDeletedFalse(taskId);
        return attachments.stream()
            .map(att -> {
                com.project.quanlycanghangkhong.dto.simplified.SimpleAttachmentDTO dto = new com.project.quanlycanghangkhong.dto.simplified.SimpleAttachmentDTO();
                dto.setId(att.getId());
                dto.setFilePath(att.getFilePath());
                dto.setFileName(att.getFileName());
                dto.setFileSize(att.getFileSize());
                dto.setCreatedAt(att.getCreatedAt());
                dto.setIsDeleted(att.isDeleted());
                
                // Flattened user info instead of nested UserDTO
                if (att.getUploadedBy() != null) {
                    dto.setUploadedByUserId(att.getUploadedBy().getId());
                    dto.setUploadedByUserName(att.getUploadedBy().getName());
                    dto.setUploadedByUserEmail(att.getUploadedBy().getEmail());
                }
                
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<AttachmentDTO> addAttachmentsToTask(Integer taskId, List<Integer> attachmentIds) {
        // Kiểm tra task có tồn tại không
        Task task = taskRepository.findByIdAndDeletedFalse(taskId).orElse(null);
        if (task == null) {
            throw new RuntimeException("Không tìm thấy task với ID: " + taskId);
        }
        
        // Lấy danh sách attachment
        List<Attachment> attachments = attachmentRepository.findAllByIdIn(attachmentIds);
        List<AttachmentDTO> result = new ArrayList<>();
        
        for (Attachment attachment : attachments) {
            if (!attachment.isDeleted()) {
                // Kiểm tra attachment chưa được gán vào task nào khác
                if (attachment.getTask() == null) {
                    attachment.setTask(task);
                    attachmentRepository.save(attachment);
                    result.add(convertToAttachmentDTOOptimized(attachment));
                } else {
                    throw new RuntimeException("Attachment với ID " + attachment.getId() + " đã được gán vào task khác");
                }
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public int removeAttachmentsFromTask(Integer taskId, List<Integer> attachmentIds) {
        // Kiểm tra task có tồn tại không
        Task task = taskRepository.findByIdAndDeletedFalse(taskId).orElse(null);
        if (task == null) {
            throw new RuntimeException("Không tìm thấy task với ID: " + taskId);
        }
        
        // Lấy danh sách attachment
        List<Attachment> attachments = attachmentRepository.findAllByIdIn(attachmentIds);
        int removedCount = 0;
        
        for (Attachment attachment : attachments) {
            if (!attachment.isDeleted() && attachment.getTask() != null && 
                attachment.getTask().getId().equals(taskId)) {
                // Chỉ remove nếu attachment thực sự thuộc về task này
                attachment.setTask(null);
                attachmentRepository.save(attachment);
                removedCount++;
            }
        }
        
        return removedCount;
    }

    // ============== SEARCH & FILTER IMPLEMENTATIONS ==============

    @Override
    public List<TaskDetailDTO> searchTasksByTitle(String title) {
        // 🚀 OPTIMIZED: Use keyword search method for title search as well
        if (title == null || title.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Task> tasks = taskRepository.findByKeywordOptimized(title.trim());
        
        // Filter to only include tasks where title contains the search term
        List<Task> titleMatchedTasks = tasks.stream()
            .filter(task -> task.getTitle().toLowerCase().contains(title.toLowerCase()))
            .collect(Collectors.toList());
        
        // ✅ Use batch conversion for better performance
        return convertTasksToTaskDetailDTOsBatch(titleMatchedTasks);
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
        // 🚀 ULTRA OPTIMIZED: Use new optimized keyword search with native query
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Task> tasks = taskRepository.findByKeywordOptimized(keyword.trim());
        
        // ✅ Use batch conversion instead of individual getTaskDetailById calls
        return convertTasksToTaskDetailDTOsBatch(tasks);
    }

    // ============== TASK COUNT AND RESPONSE METHODS ==============

    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse getMyTasksWithCount(String type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse(
                "User không tìm thấy", 401, List.of(), 0, type, false, null);
        }
        
        Integer userId = currentUser.getId();
        
        // ✅ SIMPLIFIED: Get tasks without complex subtask/root filtering
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithoutAssignments(userId);
                break;
            case "assigned":
                tasks = taskRepository.findAssignedTasksByUserId(userId);
                break;
            case "received":
                List<Task> receivedTasks = new ArrayList<>();
                receivedTasks.addAll(taskRepository.findReceivedTasksByUserId(userId));
                
                // Add team tasks if user is team lead
                if (currentUser.getRole() != null && 
                    "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getTeam() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByTeamId(currentUser.getTeam().getId()));
                }
                
                // Add unit tasks if user is unit lead
                if (currentUser.getRole() != null && 
                    "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getUnit() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(currentUser.getUnit().getId()));
                }
                
                // Remove duplicates and sort
                tasks = receivedTasks.stream()
                    .distinct()
                    .sorted((t1, t2) -> {
                        int updatedCompare = t2.getUpdatedAt().compareTo(t1.getUpdatedAt());
                        if (updatedCompare != 0) return updatedCompare;
                        return t2.getCreatedAt().compareTo(t1.getCreatedAt());
                    })
                    .collect(Collectors.toList());
                break;
            default:
                tasks = List.of();
        }
        
        // ✅ Convert all tasks to DTOs (no filtering)
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        // ✅ Count using actual returned tasks
        com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.TaskCountMetadata metadata = 
            calculateTaskCountsOptimized(userId, currentUser);
            
        // Use actual task count instead of metadata count
        int totalCount = taskDTOs.size();
        
        String message = String.format("Thành công (%d tasks)", taskDTOs.size());
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse(
            message, 200, taskDTOs, totalCount, type, true, metadata);
    }

    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithCountStandardized(String type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of());
        }
        
        Integer userId = currentUser.getId();
        
        // ✅ SIMPLIFIED: Get tasks without complex subtask/root filtering  
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithoutAssignments(userId);
                break;
            case "assigned":
                tasks = taskRepository.findAssignedTasksByUserId(userId);
                break;
            case "received":
                List<Task> receivedTasks = new ArrayList<>();
                receivedTasks.addAll(taskRepository.findReceivedTasksByUserId(userId));
                
                if (currentUser.getRole() != null && 
                    "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getTeam() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByTeamId(currentUser.getTeam().getId()));
                }
                
                if (currentUser.getRole() != null && 
                    "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getUnit() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(currentUser.getUnit().getId()));
                }
                
                tasks = receivedTasks.stream().distinct().collect(Collectors.toList());
                break;
            default:
                tasks = List.of();
        }
        
        // ✅ Convert all tasks to DTOs (no filtering)
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            taskDTOs);
    }

    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithCountStandardized(String type, String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of());
        }
        
        Integer userId = currentUser.getId();
        
        // ✅ SIMPLIFIED: Get tasks without complex subtask/root filtering
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithoutAssignments(userId);
                break;
            case "assigned":
                tasks = taskRepository.findAssignedTasksByUserId(userId);
                break;
            case "received":
                List<Task> receivedTasks = new ArrayList<>();
                receivedTasks.addAll(taskRepository.findReceivedTasksByUserId(userId));
                
                if (currentUser.getRole() != null && 
                    "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getTeam() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByTeamId(currentUser.getTeam().getId()));
                }
                
                if (currentUser.getRole() != null && 
                    "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getUnit() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(currentUser.getUnit().getId()));
                }
                
                tasks = receivedTasks.stream().distinct().collect(Collectors.toList());
                break;
            default:
                tasks = List.of();
        }
        
        // ✅ Apply status filter cho type=assigned và type=received using TaskStatusMapper
        if (type.matches("assigned|received") && status != null && !status.trim().isEmpty()) {
            java.util.function.Predicate<Task> statusFilter = com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
            tasks = tasks.stream()
                .filter(statusFilter)
                .collect(java.util.stream.Collectors.toList());
        }
        
        // ✅ Convert all tasks to DTOs (no filtering)
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            taskDTOs);
    }

    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithCountStandardizedAndPagination(String type, String status, Integer page, Integer size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of(), new PaginationInfo(page != null ? page : 0, size != null ? size : 20, 0));
        }
        
        Integer userId = currentUser.getId();
        
        // Set default pagination values
        int currentPage = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        
        // ✅ SIMPLIFIED: Get tasks without complex subtask/root filtering
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithoutAssignments(userId);
                break;
            case "assigned":
                tasks = taskRepository.findAssignedTasksByUserId(userId);
                break;
            case "received":
                List<Task> receivedTasks = new ArrayList<>();
                receivedTasks.addAll(taskRepository.findReceivedTasksByUserId(userId));
                
                if (currentUser.getRole() != null && 
                    "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getTeam() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByTeamId(currentUser.getTeam().getId()));
                }
                
                if (currentUser.getRole() != null && 
                    "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getUnit() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(currentUser.getUnit().getId()));
                }
                
                tasks = receivedTasks.stream().distinct().collect(Collectors.toList());
                break;
            default:
                tasks = List.of();
        }
        
        // ✅ Apply status filter cho type=assigned và type=received using TaskStatusMapper
        if (type.matches("assigned|received") && status != null && !status.trim().isEmpty()) {
            java.util.function.Predicate<Task> statusFilter = com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
            tasks = tasks.stream()
                .filter(statusFilter)
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Convert to DTO BEFORE pagination
        List<TaskDetailDTO> allTaskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        // ✅ Apply PAGINATION after conversion
        List<TaskDetailDTO> paginatedTaskDTOs;
        int fromIndex = currentPage * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allTaskDTOs.size());
        
        if (fromIndex < allTaskDTOs.size()) {
            paginatedTaskDTOs = allTaskDTOs.subList(fromIndex, toIndex);
        } else {
            paginatedTaskDTOs = List.of(); // Empty if page is beyond available data
        }
        
        // Create pagination info  
        PaginationInfo paginationInfo = new PaginationInfo(currentPage, pageSize, allTaskDTOs.size());
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            paginatedTaskDTOs, paginationInfo);
    }

    /**
     * 🚀 ULTRA OPTIMIZED: Get my tasks with batch loading - Performance target <500ms
     * @param type Task type (created, assigned, received)
     * @return MyTasksData with batch-loaded relationships
     */
    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithCountStandardizedUltraFast(String type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of());
        }
        
        Integer userId = currentUser.getId();
        
        // ✅ SIMPLIFIED: Get tasks without complex subtask/root filtering
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithoutAssignments(userId);
                break;
            case "assigned":
                tasks = taskRepository.findAssignedTasksByUserId(userId);
                break;
            case "received":
                List<Task> receivedTasks = new ArrayList<>();
                receivedTasks.addAll(taskRepository.findReceivedTasksByUserId(userId));
                
                if (currentUser.getRole() != null && 
                    "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getTeam() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByTeamId(currentUser.getTeam().getId()));
                }
                
                if (currentUser.getRole() != null && 
                    "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getUnit() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(currentUser.getUnit().getId()));
                }
                
                tasks = receivedTasks.stream().distinct().collect(Collectors.toList());
                break;
            default:
                tasks = List.of();
        }
        
        if (tasks.isEmpty()) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of());
        }
        
        // ✅ Convert all tasks to DTOs using simple batch conversion
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        // Use actual task count
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            taskDTOs);
    }
    
    /**
     * 🚀 ULTRA FAST: Convert task to DTO using pre-loaded relationships - Zero additional queries
     */
    private TaskDetailDTO convertToTaskDetailDTOUltraFast(Task task, 
                                                         List<Assignment> assignments,
                                                         List<Attachment> attachments,
                                                         Map<Integer, User> usersById,
                                                         Map<Integer, com.project.quanlycanghangkhong.model.Team> teamsById,
                                                         Map<Integer, com.project.quanlycanghangkhong.model.Unit> unitsById,
                                                         Map<Integer, User> teamLeadsById,
                                                         Map<Integer, User> unitLeadsById) {
        TaskDetailDTO dto = new TaskDetailDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setContent(task.getContent());
        dto.setInstructions(task.getInstructions());
        dto.setNotes(task.getNotes());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        
        if (task.getParent() != null) {
            dto.setParentId(task.getParent().getId());
        }
        
        // ✅ FIX: Use pre-loaded user data instead of task.getCreatedBy() which might be incomplete
        if (task.getCreatedBy() != null) {
            User fullCreatedByUser = usersById.get(task.getCreatedBy().getId());
            if (fullCreatedByUser != null) {
                dto.setCreatedByUser(new UserDTO(fullCreatedByUser));
            } else {
                // Fallback: load user individually if not in batch
                userRepository.findById(task.getCreatedBy().getId())
                    .ifPresent(user -> dto.setCreatedByUser(new UserDTO(user)));
            }
        }
        
        // Convert assignments using pre-loaded data
        List<AssignmentDTO> assignmentDTOs = assignments.stream()
            .map(a -> convertToAssignmentDTOUltraFast(a, usersById, teamsById, unitsById, teamLeadsById, unitLeadsById))
            .toList();
        dto.setAssignments(assignmentDTOs);
        
        // Convert attachments using pre-loaded data
        List<AttachmentDTO> attachmentDTOs = attachments.stream()
            .map(this::convertToAttachmentDTOOptimized)
            .toList();
        dto.setAttachments(attachmentDTOs);
        
        return dto;
    }
    
    /**
     * 🚀 ULTRA FAST: Convert assignment to DTO using pre-loaded user data - Zero additional queries
     */
    private AssignmentDTO convertToAssignmentDTOUltraFast(Assignment a, 
                                                         Map<Integer, User> usersById,
                                                         Map<Integer, com.project.quanlycanghangkhong.model.Team> teamsById,
                                                         Map<Integer, com.project.quanlycanghangkhong.model.Unit> unitsById,
                                                         Map<Integer, User> teamLeadsById,
                                                         Map<Integer, User> unitLeadsById) {
        AssignmentDTO adto = new AssignmentDTO();
        adto.setAssignmentId(a.getAssignmentId());
        adto.setRecipientType(a.getRecipientType());
        adto.setRecipientId(a.getRecipientId());
        adto.setTaskId(a.getTask() != null ? a.getTask().getId() : null);
        
        // ✅ Use pre-loaded assignedBy user data instead of lazy loading
        if (a.getAssignedBy() != null && a.getAssignedBy().getId() != null) {
            User assignedByUser = usersById.get(a.getAssignedBy().getId());
            if (assignedByUser != null) {
                adto.setAssignedByUser(new UserDTO(assignedByUser));
            } else if (a.getAssignedBy().getId() != null) {
                // Fallback: create minimal UserDTO with just ID
                UserDTO minimalUser = new UserDTO();
                minimalUser.setId(a.getAssignedBy().getId());
                adto.setAssignedByUser(minimalUser);
            }
        }
        
        adto.setAssignedAt(a.getAssignedAt() != null ? java.sql.Timestamp.valueOf(a.getAssignedAt()) : null);
        adto.setDueAt(a.getDueAt() != null ? java.sql.Timestamp.valueOf(a.getDueAt()) : null);
        adto.setNote(a.getNote());
        adto.setCompletedAt(a.getCompletedAt() != null ? java.sql.Timestamp.valueOf(a.getCompletedAt()) : null);
        
        // ✅ Use pre-loaded completedBy user data instead of lazy loading  
        if (a.getCompletedBy() != null && a.getCompletedBy().getId() != null) {
            User completedByUser = usersById.get(a.getCompletedBy().getId());
            if (completedByUser != null) {
                adto.setCompletedByUser(new UserDTO(completedByUser));
            } else if (a.getCompletedBy().getId() != null) {
                // Fallback: create minimal UserDTO with just ID
                UserDTO minimalUser = new UserDTO();
                minimalUser.setId(a.getCompletedBy().getId());
                adto.setCompletedByUser(minimalUser);
            }
        }
        
        adto.setStatus(a.getStatus());
        
        // ✅ Populate recipient information using pre-loaded data - Zero additional queries
        if (a.getRecipientId() != null) {
            switch (a.getRecipientType().toLowerCase()) {
                case "user":
                    User recipientUser = usersById.get(a.getRecipientId());
                    if (recipientUser != null) {
                        adto.setRecipientUser(new UserDTO(recipientUser));
                    }
                    break;
                case "team":
                    // Use pre-loaded team information
                    com.project.quanlycanghangkhong.model.Team team = teamsById.get(a.getRecipientId());
                    if (team != null) {
                        adto.setRecipientTeamName(team.getTeamName());
                        // Use pre-loaded team lead
                        User teamLead = teamLeadsById.get(a.getRecipientId());
                        if (teamLead != null) {
                            adto.setRecipientTeamLead(new UserDTO(teamLead));
                        }
                    }
                    break;
                case "unit":
                    // Use pre-loaded unit information  
                    com.project.quanlycanghangkhong.model.Unit unit = unitsById.get(a.getRecipientId());
                    if (unit != null) {
                        adto.setRecipientUnitName(unit.getUnitName());
                        // Use pre-loaded unit lead
                        User unitLead = unitLeadsById.get(a.getRecipientId());
                        if (unitLead != null) {
                            adto.setRecipientUnitLead(new UserDTO(unitLead));
                        }
                    }
                    break;
            }
        }
        
        return adto;
    }
    
    // ===================================================================
    // ✅ PRIORITY 3: SIMPLIFIED DTOs CONVERSION METHODS
    // ===================================================================
    
    /**
     * Convert Task to TaskDetailSimplifiedDTO với depth control
     */
    private TaskDetailSimplifiedDTO convertToTaskDetailSimplifiedDTO(Task task, int currentDepth) {
        TaskDetailSimplifiedDTO dto = new TaskDetailSimplifiedDTO();
        
        // Basic task info
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setContent(task.getContent());
        dto.setInstructions(task.getInstructions());
        dto.setNotes(task.getNotes());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        
        // Parent info
        if (task.getParent() != null) {
            dto.setParentId(task.getParent().getId());
        }
        
        // Flattened createdBy user info (thay thế nested UserDTO)
        if (task.getCreatedBy() != null) {
            dto.setCreatedByUserId(task.getCreatedBy().getId());
            dto.setCreatedByUserName(task.getCreatedBy().getName());
            dto.setCreatedByUserEmail(task.getCreatedBy().getEmail());
            // Team info if available
            if (task.getCreatedBy().getTeam() != null) {
                dto.setCreatedByTeamName(task.getCreatedBy().getTeam().getTeamName());
            }
        }
        
        // Simplified assignments (thay thế nested AssignmentDTO)
        List<SimpleAssignmentDTO> simpleAssignments = task.getAssignments().stream()
            .map(this::convertToSimpleAssignmentDTO)
            .toList();
        dto.setAssignments(simpleAssignments);
        
        // Simplified attachments (thay thế nested AttachmentDTO)
        List<SimpleAttachmentDTO> simpleAttachments = attachmentRepository.findByTask_IdAndIsDeletedFalse(task.getId())
            .stream()
            .map(this::convertToSimpleAttachmentDTO)
            .toList();
        dto.setAttachments(simpleAttachments);
        
        return dto;
    }
    
    /**
     * Convert Assignment to SimpleAssignmentDTO (flattened)
     */
    private SimpleAssignmentDTO convertToSimpleAssignmentDTO(Assignment assignment) {
        SimpleAssignmentDTO dto = new SimpleAssignmentDTO();
        
        dto.setAssignmentId(assignment.getAssignmentId());
        dto.setTaskId(assignment.getTask() != null ? assignment.getTask().getId() : null);
        dto.setRecipientType(assignment.getRecipientType());
        dto.setRecipientId(assignment.getRecipientId());
        dto.setAssignedAt(assignment.getAssignedAt());
        dto.setDueAt(assignment.getDueAt());
        dto.setCompletedAt(assignment.getCompletedAt());
        dto.setStatus(assignment.getStatus());
        dto.setNote(assignment.getNote());
        
        // Flattened assignedBy user info
        if (assignment.getAssignedBy() != null) {
            dto.setAssignedByUserId(assignment.getAssignedBy().getId());
            dto.setAssignedByUserName(assignment.getAssignedBy().getName());
            dto.setAssignedByUserEmail(assignment.getAssignedBy().getEmail());
        }
        
        // Flattened completedBy user info
        if (assignment.getCompletedBy() != null) {
            dto.setCompletedByUserId(assignment.getCompletedBy().getId());
            dto.setCompletedByUserName(assignment.getCompletedBy().getName());
            dto.setCompletedByUserEmail(assignment.getCompletedBy().getEmail());
        }
        
        // Flattened recipient user info (chỉ khi recipientType = 'user')
        // Note: Cần thêm logic để resolve recipient user từ recipientId
        if ("user".equals(assignment.getRecipientType()) && assignment.getRecipientId() != null) {
            // TODO: Load recipient user info từ userRepository nếu cần
            // User recipientUser = userRepository.findById(assignment.getRecipientId()).orElse(null);
            // if (recipientUser != null) {
            //     dto.setRecipientUserName(recipientUser.getName());
            //     dto.setRecipientUserEmail(recipientUser.getEmail());
            // }
        }
        
        return dto;
    }
    
    /**
     * Convert Attachment to SimpleAttachmentDTO (flattened)
     */
    private SimpleAttachmentDTO convertToSimpleAttachmentDTO(Attachment attachment) {
        SimpleAttachmentDTO dto = new SimpleAttachmentDTO();
        
        dto.setId(attachment.getId());
        dto.setFilePath(attachment.getFilePath());
        dto.setFileName(attachment.getFileName());
        dto.setFileSize(attachment.getFileSize());
        // dto.setFileType(attachment.getContentType()); // Field không tồn tại, bỏ qua
        dto.setCreatedAt(attachment.getCreatedAt());
        // dto.setSharedCount(attachment.getSharedCount()); // Field không tồn tại, set default
        dto.setSharedCount(0);
        // dto.setIsShared(attachment.getIsShared()); // Field không tồn tại, set default  
        dto.setIsShared(false);
        // dto.setIsDeleted(attachment.getIsDeleted()); // Field không tồn tại, set default
        dto.setIsDeleted(false);
        
        // Flattened uploadedBy user info
        if (attachment.getUploadedBy() != null) {
            dto.setUploadedByUserId(attachment.getUploadedBy().getId());
            dto.setUploadedByUserName(attachment.getUploadedBy().getName());
            dto.setUploadedByUserEmail(attachment.getUploadedBy().getEmail());
        }
        
        return dto;
    }
    
    /**
     * Helper method để apply pagination vào MyTasksData response
     * @param originalData Data gốc không có pagination
     * @param page Số trang (bắt đầu từ 0)
     * @param size Số lượng items per page
     * @return MyTasksData đã được phân trang với PaginationInfo
     */
    private com.project.quanlycanghangkhong.dto.response.task.MyTasksData applyPaginationToMyTasksData(
            com.project.quanlycanghangkhong.dto.response.task.MyTasksData originalData, int page, int size) {
        
        if (originalData == null || originalData.getTasks() == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of(), new PaginationInfo(page, size, 0));
        }
        
        List<TaskDetailDTO> allTasks = originalData.getTasks();
        int totalCount = allTasks.size();
        
        // Apply pagination
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, allTasks.size());
        
        List<TaskDetailDTO> paginatedTasks;
        if (fromIndex < allTasks.size()) {
            paginatedTasks = allTasks.subList(fromIndex, toIndex);
        } else {
            paginatedTasks = List.of(); // Empty if page is beyond available data
        }
        
        // Create pagination info
        PaginationInfo paginationInfo = new PaginationInfo(page, size, totalCount);
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            paginatedTasks, paginationInfo);
    }
    
    /**
     * 🚀 BATCH CONVERSION: Convert list of tasks to TaskDetailDTOs with batch loading
     * Replaces individual getTaskDetailById calls to eliminate N+1 queries
     */
    private List<TaskDetailDTO> convertTasksToTaskDetailDTOsBatch(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return List.of();
        }
        
        // Extract task IDs for batch loading
        List<Integer> taskIds = tasks.stream().map(Task::getId).toList();
        
        // Batch load assignments for all tasks
        List<Assignment> allAssignments = assignmentRepository.findByTaskIdInOptimized(taskIds);
        Map<Integer, List<Assignment>> assignmentsByTaskId = allAssignments.stream()
            .collect(Collectors.groupingBy(a -> a.getTask().getId()));
        
        // Batch load attachments for all tasks  
        List<Attachment> allAttachments = attachmentRepository.findByTaskIdInAndIsDeletedFalse(taskIds);
        Map<Integer, List<Attachment>> attachmentsByTaskId = allAttachments.stream()
            .collect(Collectors.groupingBy(a -> a.getTask().getId()));
        
        // ✅ Batch load ALL user IDs needed (recipients + assignedBy + completedBy + uploadedBy)
        Set<Integer> allUserIds = new HashSet<>();
        
        // Recipient user IDs
        Set<Integer> allRecipientUserIds = allAssignments.stream()
            .filter(a -> "user".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null)
            .map(Assignment::getRecipientId)
            .collect(Collectors.toSet());
        allUserIds.addAll(allRecipientUserIds);
        
        // AssignedBy user IDs
        Set<Integer> assignedByUserIds = allAssignments.stream()
            .filter(a -> a.getAssignedBy() != null && a.getAssignedBy().getId() != null)
            .map(a -> a.getAssignedBy().getId())
            .collect(Collectors.toSet());
        allUserIds.addAll(assignedByUserIds);
        
        // CompletedBy user IDs  
        Set<Integer> completedByUserIds = allAssignments.stream()
            .filter(a -> a.getCompletedBy() != null && a.getCompletedBy().getId() != null)
            .map(a -> a.getCompletedBy().getId())
            .collect(Collectors.toSet());
        allUserIds.addAll(completedByUserIds);
        
        // UploadedBy user IDs from attachments
        Set<Integer> uploadedByUserIds = allAttachments.stream()
            .filter(a -> a.getUploadedBy() != null && a.getUploadedBy().getId() != null)
            .map(a -> a.getUploadedBy().getId())
            .collect(Collectors.toSet());
        allUserIds.addAll(uploadedByUserIds);
        
        // Batch load team and unit IDs
        Set<Integer> allTeamIds = allAssignments.stream()
            .filter(a -> "team".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null)
            .map(Assignment::getRecipientId)
            .collect(Collectors.toSet());
            
        Set<Integer> allUnitIds = allAssignments.stream()
            .filter(a -> "unit".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null)
            .map(Assignment::getRecipientId)
            .collect(Collectors.toSet());

        // ✅ MEGA BATCH LOAD: Load ALL users in one query (recipients + assignedBy + completedBy + uploadedBy)
        Map<Integer, User> usersById = allUserIds.isEmpty() ? 
            Map.of() : 
            userRepository.findAllById(allUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        // Batch load teams
        Map<Integer, com.project.quanlycanghangkhong.model.Team> teamsById = allTeamIds.isEmpty() ?
            Map.of() :
            teamRepository.findAllById(allTeamIds).stream()
                .collect(Collectors.toMap(com.project.quanlycanghangkhong.model.Team::getId, t -> t));
        
        // Batch load units  
        Map<Integer, com.project.quanlycanghangkhong.model.Unit> unitsById = allUnitIds.isEmpty() ?
            Map.of() :
            unitRepository.findAllById(allUnitIds).stream()
                .collect(Collectors.toMap(com.project.quanlycanghangkhong.model.Unit::getId, u -> u));
        
        // Batch load team leads - ✅ OPTIMIZED: Single query for all teams
        Map<Integer, User> teamLeadsById = allTeamIds.isEmpty() ?
            Map.of() :
            userRepository.findTeamLeadsByTeamIds(new ArrayList<>(allTeamIds)).stream()
                .collect(Collectors.toMap(
                    user -> user.getTeam() != null ? user.getTeam().getId() : null,
                    user -> user
                ))
                .entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        // Batch load unit leads - ✅ OPTIMIZED: Single query for all units
        Map<Integer, User> unitLeadsById = allUnitIds.isEmpty() ?
            Map.of() :
            userRepository.findUnitLeadsByUnitIds(new ArrayList<>(allUnitIds)).stream()
                .collect(Collectors.toMap(
                    user -> user.getUnit() != null ? user.getUnit().getId() : null,
                    user -> user
                ))
                .entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Convert all tasks using pre-loaded data
        return tasks.stream()
            .map(task -> convertToTaskDetailDTOUltraFast(task, 
                assignmentsByTaskId.getOrDefault(task.getId(), List.of()),
                attachmentsByTaskId.getOrDefault(task.getId(), List.of()),
                usersById, teamsById, unitsById, teamLeadsById, unitLeadsById))
            .collect(Collectors.toList());
    }
    
    /**
     * 🚀 OPTIMIZED: Calculate task counts using direct database queries
     */
    private com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.TaskCountMetadata calculateTaskCountsOptimized(Integer userId, User currentUser) {
        Integer teamId = currentUser.getTeam() != null ? currentUser.getTeam().getId() : null;
        Integer unitId = currentUser.getUnit() != null ? currentUser.getUnit().getId() : null;
        
        // Use repository count methods instead of loading all data
        int createdCount = taskRepository.countCreatedTasksWithoutAssignments(userId);
        int assignedCount = taskRepository.countAssignedTasksByUserId(userId);
        int receivedCount = taskRepository.countReceivedTasksByUserId(userId, teamId, unitId);
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.TaskCountMetadata(
            createdCount, assignedCount, receivedCount);
    }
    
    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithAdvancedSearch(
            String type, String status, String keyword, String startTime, String endTime,
            java.util.List<String> priorities, java.util.List<String> recipientTypes, java.util.List<Integer> recipientIds) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of());
        }
        
        Integer userId = currentUser.getId();
        
        // Parse dates and priorities
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        try {
            if (startTime != null && !startTime.isEmpty()) {
                startDateTime = LocalDate.parse(startTime).atStartOfDay();
            }
            if (endTime != null && !endTime.isEmpty()) {
                endDateTime = LocalDate.parse(endTime).atTime(23, 59, 59);
            }
        } catch (Exception e) {
            // Invalid date format, continue without date filter
        }
        
        List<com.project.quanlycanghangkhong.model.TaskPriority> priorityEnums = new ArrayList<>();
        if (priorities != null) {
            for (String priority : priorities) {
                try {
                    priorityEnums.add(com.project.quanlycanghangkhong.model.TaskPriority.valueOf(priority.toUpperCase()));
                } catch (Exception e) {
                    // Invalid priority, skip
                }
            }
        }
        
        // Handle different types
        if ("assigned".equals(type)) {
            return handleAssignedTasksAdvancedSearch(userId, status, keyword, startDateTime, endDateTime, 
                priorityEnums, recipientTypes, recipientIds);
        } else if ("created".equals(type)) {
            return handleCreatedTasksAdvancedSearch(userId, keyword, startDateTime, endDateTime, priorityEnums);
        } else if ("received".equals(type)) {
            return handleReceivedTasksAdvancedSearch(currentUser, status, keyword, startDateTime, endDateTime, priorityEnums);
        } else {
            // Fallback to standard method
            return getMyTasksWithCountStandardized(type, status);
        }
    }
    
    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithAdvancedSearchAndPagination(
            String type, String status, String keyword, String startTime, String endTime,
            java.util.List<String> priorities, java.util.List<String> recipientTypes, java.util.List<Integer> recipientIds,
            Integer page, Integer size) {
        
        // Get advanced search results first
        com.project.quanlycanghangkhong.dto.response.task.MyTasksData searchResult = 
            getMyTasksWithAdvancedSearch(type, status, keyword, startTime, endTime, 
                priorities, recipientTypes, recipientIds);
        
        // Apply pagination
        return applyPaginationToMyTasksData(searchResult, page != null ? page : 0, size != null ? size : 20);
    }
    
    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData searchMyTasksAdvanced(
            com.project.quanlycanghangkhong.dto.request.AdvancedSearchRequest searchRequest) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of());
        }
        
        // Get type from request (default to "assigned" for backward compatibility)
        String type = searchRequest.getType() != null ? searchRequest.getType().toLowerCase() : "assigned";
        
        // Validate type
        if (!type.matches("created|assigned|received")) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of());
        }
        
        // Handle different types with pagination
        return switch (type) {
            case "created" -> handleCreatedTasksAdvancedSearchWithPagination(currentUser, searchRequest);
            case "assigned" -> handleAssignedTasksAdvancedSearchWithPagination(currentUser, searchRequest);
            case "received" -> handleReceivedTasksAdvancedSearchWithPagination(currentUser, searchRequest);
            default -> new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of());
        };
    }
    
    // Helper methods for advanced search by type
    private com.project.quanlycanghangkhong.dto.response.task.MyTasksData handleAssignedTasksAdvancedSearch(
            Integer userId, String status, String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime,
            List<com.project.quanlycanghangkhong.model.TaskPriority> priorityEnums,
            List<String> recipientTypes, List<Integer> recipientIds) {
        
        // ✅ SIMPLIFIED: Get assigned tasks without complex native queries
        List<Task> tasks = taskRepository.findAssignedTasksByUserId(userId);
        
        // Apply status filter using TaskStatusMapper
        if (status != null && !status.trim().isEmpty()) {
            java.util.function.Predicate<Task> statusFilter = com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
            tasks = tasks.stream()
                .filter(statusFilter)
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Apply keyword search
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim().toLowerCase();
            tasks = tasks.stream()
                .filter(task -> task.getId().toString().contains(searchKeyword) ||
                               task.getTitle().toLowerCase().contains(searchKeyword) ||
                               (task.getContent() != null && task.getContent().toLowerCase().contains(searchKeyword)) ||
                               (task.getInstructions() != null && task.getInstructions().toLowerCase().contains(searchKeyword)) ||
                               (task.getNotes() != null && task.getNotes().toLowerCase().contains(searchKeyword)))
                .collect(Collectors.toList());
        }
        
        // Apply time range filter
        if (startDateTime != null || endDateTime != null) {
            tasks = tasks.stream()
                .filter(task -> {
                    if (startDateTime != null && task.getCreatedAt().isBefore(startDateTime)) return false;
                    if (endDateTime != null && task.getCreatedAt().isAfter(endDateTime)) return false;
                    return true;
                })
                .collect(Collectors.toList());
        }
        
        // Apply priority filter
        if (priorityEnums != null && !priorityEnums.isEmpty()) {
            tasks = tasks.stream()
                .filter(task -> priorityEnums.contains(task.getPriority()))
                .collect(Collectors.toList());
        }
        
        // Convert to DTOs
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            taskDTOs);
    }
    
    private com.project.quanlycanghangkhong.dto.response.task.MyTasksData handleCreatedTasksAdvancedSearch(
            Integer userId, String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime,
            List<com.project.quanlycanghangkhong.model.TaskPriority> priorityEnums) {
        
        // Get created tasks
        List<Task> tasks = taskRepository.findCreatedTasksWithoutAssignments(userId);
        
        // Apply keyword search
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim().toLowerCase();
            tasks = tasks.stream()
                .filter(task -> task.getId().toString().contains(searchKeyword) ||
                               task.getTitle().toLowerCase().contains(searchKeyword) ||
                               (task.getContent() != null && task.getContent().toLowerCase().contains(searchKeyword)) ||
                               (task.getInstructions() != null && task.getInstructions().toLowerCase().contains(searchKeyword)) ||
                               (task.getNotes() != null && task.getNotes().toLowerCase().contains(searchKeyword)))
                .collect(Collectors.toList());
        }
        
        // Apply time range filter
        if (startDateTime != null || endDateTime != null) {
            tasks = tasks.stream()
                .filter(task -> {
                    if (startDateTime != null && task.getCreatedAt().isBefore(startDateTime)) return false;
                    if (endDateTime != null && task.getCreatedAt().isAfter(endDateTime)) return false;
                    return true;
                })
                .collect(Collectors.toList());
        }
        
        // Apply priority filter
        if (priorityEnums != null && !priorityEnums.isEmpty()) {
            tasks = tasks.stream()
                .filter(task -> priorityEnums.contains(task.getPriority()))
                .collect(Collectors.toList());
        }
        
        // Convert to DTOs
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            taskDTOs);
    }
    
    private com.project.quanlycanghangkhong.dto.response.task.MyTasksData handleReceivedTasksAdvancedSearch(
            User currentUser, String status, String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime,
            List<com.project.quanlycanghangkhong.model.TaskPriority> priorityEnums) {
        
        Integer userId = currentUser.getId();
        
        // ✅ SIMPLIFIED: Get received tasks without complex native queries
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(taskRepository.findReceivedTasksByUserId(userId));
        
        if (currentUser.getRole() != null && 
            "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
            currentUser.getTeam() != null) {
            tasks.addAll(taskRepository.findReceivedTasksByTeamId(currentUser.getTeam().getId()));
        }
        
        if (currentUser.getRole() != null && 
            "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
            currentUser.getUnit() != null) {
            tasks.addAll(taskRepository.findReceivedTasksByUnitId(currentUser.getUnit().getId()));
        }
        
        tasks = tasks.stream().distinct().collect(Collectors.toList());
        
        // Apply keyword search
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim().toLowerCase();
            tasks = tasks.stream()
                .filter(task -> task.getId().toString().contains(searchKeyword) ||
                               task.getTitle().toLowerCase().contains(searchKeyword) ||
                               (task.getContent() != null && task.getContent().toLowerCase().contains(searchKeyword)) ||
                               (task.getInstructions() != null && task.getInstructions().toLowerCase().contains(searchKeyword)) ||
                               (task.getNotes() != null && task.getNotes().toLowerCase().contains(searchKeyword)))
                .collect(Collectors.toList());
        }
        
        // Apply time range filter
        if (startDateTime != null || endDateTime != null) {
            tasks = tasks.stream()
                .filter(task -> {
                    if (startDateTime != null && task.getCreatedAt().isBefore(startDateTime)) return false;
                    if (endDateTime != null && task.getCreatedAt().isAfter(endDateTime)) return false;
                    return true;
                })
                .collect(Collectors.toList());
        }
        
        // Apply priority filter
        if (priorityEnums != null && !priorityEnums.isEmpty()) {
            tasks = tasks.stream()
                .filter(task -> priorityEnums.contains(task.getPriority()))
                .collect(Collectors.toList());
        }
        
        // ✅ Apply status filter using TaskStatusMapper for received tasks
        if (status != null && !status.trim().isEmpty()) {
            java.util.function.Predicate<Task> statusFilter = com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
            tasks = tasks.stream()
                .filter(statusFilter)
                .collect(Collectors.toList());
        }
        
        // Convert to DTOs
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            taskDTOs);
    }
    
    private com.project.quanlycanghangkhong.dto.response.task.MyTasksData handleCreatedTasksAdvancedSearchWithPagination(
            User currentUser, com.project.quanlycanghangkhong.dto.request.AdvancedSearchRequest searchRequest) {
        // Implementation similar to handleCreatedTasksAdvancedSearch but with pagination
        // For now, delegate to the non-paginated version
        return handleCreatedTasksAdvancedSearch(currentUser.getId(), 
            searchRequest.getKeyword(), null, null, null);
    }
    
    private com.project.quanlycanghangkhong.dto.response.task.MyTasksData handleAssignedTasksAdvancedSearchWithPagination(
            User currentUser, com.project.quanlycanghangkhong.dto.request.AdvancedSearchRequest searchRequest) {
        // Implementation similar to handleAssignedTasksAdvancedSearch but with pagination
        // For now, delegate to the non-paginated version
        return handleAssignedTasksAdvancedSearch(currentUser.getId(), 
            null, searchRequest.getKeyword(), null, null, null, null, null);
    }
    
    private com.project.quanlycanghangkhong.dto.response.task.MyTasksData handleReceivedTasksAdvancedSearchWithPagination(
            User currentUser, com.project.quanlycanghangkhong.dto.request.AdvancedSearchRequest searchRequest) {
        // Implementation similar to handleReceivedTasksAdvancedSearch but with pagination
        // For now, delegate to the non-paginated version
        return handleReceivedTasksAdvancedSearch(currentUser, 
            searchRequest.getFilter(), searchRequest.getKeyword(), null, null, null);
    }
    
    // ============== DATABASE-LEVEL PAGINATION METHODS (OPTIMIZED) ==============
    
    /**
     * 🚀 DATABASE PAGINATION: Get my tasks with database-level pagination (1-based)
     */
    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithCountStandardizedAndPaginationOptimized(
            String type, String status, Integer page, Integer size) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of(), new com.project.quanlycanghangkhong.dto.response.task.PaginationInfo(1, 20, 0));
        }
        
        Integer userId = currentUser.getId();
        
        // ✅ Normalize 1-based pagination parameters
        int[] normalizedParams = com.project.quanlycanghangkhong.dto.response.task.PaginationInfo.normalizePageParams(page, size);
        int currentPage = normalizedParams[0]; // 1-based
        int pageSize = normalizedParams[1];
        
        // ✅ Calculate database offset (0-based for LIMIT/OFFSET)
        int offset = com.project.quanlycanghangkhong.dto.response.task.PaginationInfo.calculateOffset(currentPage, pageSize);
        org.springframework.data.domain.Pageable pageable = 
            org.springframework.data.domain.PageRequest.of(offset / pageSize, pageSize);
        
        // ✅ DATABASE-LEVEL PAGINATION: Get tasks with LIMIT/OFFSET
        List<Task> tasks;
        long totalCount;
        
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithPagination(userId, pageable);
                totalCount = taskRepository.countCreatedTasksWithoutAssignments(userId);
                break;
            case "assigned":
                tasks = taskRepository.findAssignedTasksWithPagination(userId, pageable);
                totalCount = taskRepository.countAssignedTasksByUserId(userId);
                break;
            case "received":
                Integer teamId = (currentUser.getRole() != null && 
                    "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getTeam() != null) ? currentUser.getTeam().getId() : null;
                Integer unitId = (currentUser.getRole() != null && 
                    "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getUnit() != null) ? currentUser.getUnit().getId() : null;
                    
                tasks = taskRepository.findReceivedTasksWithPagination(userId, teamId, unitId, pageable);
                totalCount = taskRepository.countReceivedTasksByUserId(userId, teamId, unitId);
                break;
            default:
                tasks = List.of();
                totalCount = 0;
        }
        
        // ✅ Apply status filter AFTER database pagination using TaskStatusMapper
        if (type.matches("assigned|received") && status != null && !status.trim().isEmpty()) {
            java.util.function.Predicate<Task> statusFilter = com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
            tasks = tasks.stream()
                .filter(statusFilter)
                .collect(java.util.stream.Collectors.toList());
            // ⚠️ IMPORTANT: Recalculate totalCount to match filtered results for accurate pagination
            // This sacrifices some performance for UI accuracy
            totalCount = getFilteredTaskCount(type, status, userId, currentUser);
        }
        
        // ✅ Convert to DTOs (only paginated data)
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        // ✅ Create pagination info (1-based) - now accurate after status filtering
        com.project.quanlycanghangkhong.dto.response.task.PaginationInfo paginationInfo = 
            new com.project.quanlycanghangkhong.dto.response.task.PaginationInfo(currentPage, pageSize, totalCount);
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            taskDTOs, paginationInfo);
    }
    
    /**
     * 🚀 DATABASE PAGINATION: Advanced search with database-level pagination (1-based)
     */
    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithAdvancedSearchAndPaginationOptimized(
            String type, String status, String keyword, String startTime, String endTime,
            java.util.List<String> priorities, java.util.List<String> recipientTypes, java.util.List<Integer> recipientIds,
            Integer page, Integer size) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of(), new com.project.quanlycanghangkhong.dto.response.task.PaginationInfo(1, 20, 0));
        }
        
        Integer userId = currentUser.getId();
        
        // ✅ Import and use TaskStatusMapper
        java.util.function.Predicate<Task> statusFilter = com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
        
        // ✅ Normalize 1-based pagination parameters
        int[] normalizedParams = com.project.quanlycanghangkhong.dto.response.task.PaginationInfo.normalizePageParams(page, size);
        int currentPage = normalizedParams[0]; // 1-based
        int pageSize = normalizedParams[1];
        
        // ✅ Calculate database offset (0-based for LIMIT/OFFSET)
        int offset = com.project.quanlycanghangkhong.dto.response.task.PaginationInfo.calculateOffset(currentPage, pageSize);
        org.springframework.data.domain.Pageable pageable = 
            org.springframework.data.domain.PageRequest.of(offset / pageSize, pageSize);
        
        // ✅ Parse advanced search parameters
        LocalDateTime tempStartDateTime = null;
        LocalDateTime tempEndDateTime = null;
        
        try {
            if (startTime != null && !startTime.isEmpty()) {
                tempStartDateTime = LocalDate.parse(startTime).atStartOfDay();
            }
        } catch (Exception e) {
            // Invalid start date format, keep null
        }
        
        try {
            if (endTime != null && !endTime.isEmpty()) {
                tempEndDateTime = LocalDate.parse(endTime).atTime(23, 59, 59);
            }
        } catch (Exception e) {
            // Invalid end date format, keep null
        }
        
        final LocalDateTime finalStartDateTime = tempStartDateTime;
        final LocalDateTime finalEndDateTime = tempEndDateTime;
        
        List<com.project.quanlycanghangkhong.model.TaskPriority> priorityEnums = new ArrayList<>();
        if (priorities != null) {
            for (String priority : priorities) {
                try {
                    priorityEnums.add(com.project.quanlycanghangkhong.model.TaskPriority.valueOf(priority.toUpperCase()));
                } catch (Exception e) {
                    // Invalid priority, skip
                }
            }
        }
        
        // ✅ Create final variables for lambda expressions
        final String finalKeyword = keyword;
        
        // ✅ DATABASE-LEVEL ADVANCED SEARCH with PAGINATION
        List<Task> tasks;
        long totalCount;
        
        if ("assigned".equals(type.toLowerCase())) {
            tasks = taskRepository.findAssignedTasksWithAdvancedSearchAndPagination(
                userId, keyword, finalStartDateTime, finalEndDateTime, priorityEnums, 
                recipientTypes != null ? recipientTypes : List.of(), 
                recipientIds != null ? recipientIds : List.of(), 
                pageable);
            totalCount = taskRepository.countAssignedTasksWithAdvancedSearchMulti(
                userId, keyword, finalStartDateTime, finalEndDateTime, priorityEnums,
                recipientTypes != null ? recipientTypes : List.of(), 
                recipientIds != null ? recipientIds : List.of());
        } else if ("received".equals(type.toLowerCase())) {
            // ✅ IMPLEMENT ADVANCED SEARCH FOR RECEIVED TYPE
            Integer teamId = (currentUser.getRole() != null && 
                "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                currentUser.getTeam() != null) ? currentUser.getTeam().getId() : null;
            Integer unitId = (currentUser.getRole() != null && 
                "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                currentUser.getUnit() != null) ? currentUser.getUnit().getId() : null;
                
            // Get all received tasks first, then apply advanced filtering
            org.springframework.data.domain.Pageable unboundedPageable = 
                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE);
            List<Task> allReceivedTasks = taskRepository.findReceivedTasksWithPagination(userId, teamId, unitId, unboundedPageable);
            
            // Apply advanced search filters
            tasks = allReceivedTasks.stream()
                .filter(task -> {
                    // Keyword filter (search in title, content, instructions, notes, task ID)
                    if (finalKeyword != null && !finalKeyword.trim().isEmpty()) {
                        String searchKeyword = finalKeyword.trim().toLowerCase();
                        boolean matchesKeyword = task.getId().toString().contains(searchKeyword) ||
                                               task.getTitle().toLowerCase().contains(searchKeyword) ||
                                               (task.getContent() != null && task.getContent().toLowerCase().contains(searchKeyword)) ||
                                               (task.getInstructions() != null && task.getInstructions().toLowerCase().contains(searchKeyword)) ||
                                               (task.getNotes() != null && task.getNotes().toLowerCase().contains(searchKeyword));
                        if (!matchesKeyword) return false;
                    }
                    
                    // Time range filter
                    if (finalStartDateTime != null && task.getCreatedAt().isBefore(finalStartDateTime)) return false;
                    if (finalEndDateTime != null && task.getCreatedAt().isAfter(finalEndDateTime)) return false;
                    
                    // Priority filter
                    if (!priorityEnums.isEmpty() && !priorityEnums.contains(task.getPriority())) return false;
                    
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
                
            totalCount = tasks.size();
            
            // Apply pagination manually
            int startIndex = offset;
            int endIndex = Math.min(startIndex + pageSize, tasks.size());
            tasks = startIndex < tasks.size() ? tasks.subList(startIndex, endIndex) : List.of();
            
        } else if ("created".equals(type.toLowerCase())) {
            // Get all created tasks first, then apply advanced filtering  
            List<Task> allCreatedTasks = taskRepository.findCreatedTasksWithoutAssignments(userId);
            
            // Apply advanced search filters
            tasks = allCreatedTasks.stream()
                .filter(task -> {
                    // Keyword filter
                    if (finalKeyword != null && !finalKeyword.trim().isEmpty()) {
                        String searchKeyword = finalKeyword.trim().toLowerCase();
                        boolean matchesKeyword = task.getId().toString().contains(searchKeyword) ||
                                               task.getTitle().toLowerCase().contains(searchKeyword) ||
                                               (task.getContent() != null && task.getContent().toLowerCase().contains(searchKeyword)) ||
                                               (task.getInstructions() != null && task.getInstructions().toLowerCase().contains(searchKeyword)) ||
                                               (task.getNotes() != null && task.getNotes().toLowerCase().contains(searchKeyword));
                        if (!matchesKeyword) return false;
                    }
                    
                    // Time range filter
                    if (finalStartDateTime != null && task.getCreatedAt().isBefore(finalStartDateTime)) return false;
                    if (finalEndDateTime != null && task.getCreatedAt().isAfter(finalEndDateTime)) return false;
                    
                    // Priority filter
                    if (!priorityEnums.isEmpty() && !priorityEnums.contains(task.getPriority())) return false;
                    
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
                
            totalCount = tasks.size();
            
            // Apply pagination manually
            int startIndex = offset;
            int endIndex = Math.min(startIndex + pageSize, tasks.size());
            tasks = startIndex < tasks.size() ? tasks.subList(startIndex, endIndex) : List.of();
            
        } else {
            // Unknown type
            tasks = List.of();
            totalCount = 0;
        }
        
        // ✅ Apply status filter using TaskStatusMapper
        if (status != null && !status.trim().isEmpty()) {
            tasks = tasks.stream()
                .filter(statusFilter)
                .collect(java.util.stream.Collectors.toList());
        }
        
        // ✅ Convert to DTOs (only paginated data)
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        // ✅ Create pagination info (1-based)
        com.project.quanlycanghangkhong.dto.response.task.PaginationInfo paginationInfo = 
            new com.project.quanlycanghangkhong.dto.response.task.PaginationInfo(currentPage, pageSize, totalCount);
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            taskDTOs, paginationInfo);
    }
    
    /**
     * 🎯 Helper method: Calculate accurate total count after status filtering
     * Used to fix pagination totalElements mismatch when status filter is applied
     */
    private long getFilteredTaskCount(String type, String status, Integer userId, User currentUser) {
        if (status == null || status.trim().isEmpty()) {
            // No status filter, return original database count
            switch (type.toLowerCase()) {
                case "assigned":
                    return taskRepository.countAssignedTasksByUserId(userId);
                case "received":
                    Integer teamId = (currentUser.getRole() != null && 
                        "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                        currentUser.getTeam() != null) ? currentUser.getTeam().getId() : null;
                    Integer unitId = (currentUser.getRole() != null && 
                        "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                        currentUser.getUnit() != null) ? currentUser.getUnit().getId() : null;
                    return taskRepository.countReceivedTasksByUserId(userId, teamId, unitId);
                default:
                    return 0;
            }
        }
        
        // Get ALL tasks of this type (without pagination) and apply status filter
        List<Task> allTasks;
        switch (type.toLowerCase()) {
            case "assigned":
                allTasks = taskRepository.findAssignedTasksByUserId(userId);
                break;
            case "received":
                // Use the same logic as the paginated method for consistency
                Integer teamId = (currentUser.getRole() != null && 
                    "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getTeam() != null) ? currentUser.getTeam().getId() : null;
                Integer unitId = (currentUser.getRole() != null && 
                    "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getUnit() != null) ? currentUser.getUnit().getId() : null;
                
                // Use a non-paginated version that uses the same query logic
                org.springframework.data.domain.Pageable unboundedPageable = 
                    org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE);
                allTasks = taskRepository.findReceivedTasksWithPagination(userId, teamId, unitId, unboundedPageable);
                break;
            default:
                return 0;
        }
        
        // Apply status filter and count
        java.util.function.Predicate<Task> statusFilter = com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
        return allTasks.stream()
            .filter(statusFilter)
            .count();
    }
}
