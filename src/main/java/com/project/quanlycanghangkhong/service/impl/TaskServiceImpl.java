package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.TaskRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.TaskService;

// ✅ PRIORITY 3: Simplified DTOs imports

// ✅ Pagination imports
import com.project.quanlycanghangkhong.dto.PaginationInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.project.quanlycanghangkhong.dto.*;
import com.project.quanlycanghangkhong.model.*;
import com.project.quanlycanghangkhong.repository.*;
import com.project.quanlycanghangkhong.request.CreateTaskRequest;
import com.project.quanlycanghangkhong.request.CreateSubtaskRequest;
import com.project.quanlycanghangkhong.request.AssignmentRequest;
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

    @Autowired
    private TaskTypeRepository taskTypeRepository;

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
        
        // Convert TaskType to TaskTypeDTO
        if (task.getTaskType() != null) {
            TaskTypeDTO taskTypeDTO = new TaskTypeDTO();
            taskTypeDTO.setId(task.getTaskType().getId());
            taskTypeDTO.setName(task.getTaskType().getName());
            dto.setTaskType(taskTypeDTO);
        }
        
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
        
        // Convert TaskTypeDTO to TaskType
        if (dto.getTaskType() != null && dto.getTaskType().getId() != null) {
            Optional<TaskType> taskTypeOpt = taskTypeRepository.findById(dto.getTaskType().getId());
            taskTypeOpt.ifPresent(task::setTaskType);
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
        
        // Set TaskType nếu có
        if (request.getTaskTypeId() != null) {
            Optional<TaskType> taskTypeOpt = taskTypeRepository.findById(request.getTaskTypeId());
            taskTypeOpt.ifPresent(task::setTaskType);
        }
        
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
            
            // Cập nhật TaskType nếu có
            if (updateTaskDTO.getTaskTypeId() != null) {
                Optional<TaskType> taskTypeOpt = taskTypeRepository.findById(updateTaskDTO.getTaskTypeId());
                if (taskTypeOpt.isPresent()) {
                    task.setTaskType(taskTypeOpt.get());
                } else {
                    task.setTaskType(null); // Remove TaskType if ID not found
                }
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
        
        // Set TaskType
        if (task.getTaskType() != null) {
            TaskTypeDTO taskTypeDTO = new TaskTypeDTO();
            taskTypeDTO.setId(task.getTaskType().getId());
            taskTypeDTO.setName(task.getTaskType().getName());
            dto.setTaskType(taskTypeDTO);
        }
        
        // Keep parentId for reference only (no nesting)
        if (task.getParent() != null) {
            dto.setParentId(task.getParent().getId());
        }
        
        // ✅ Check if this task has subtasks
        boolean hasSubtasks = taskRepository.findByParentIdAndDeletedFalse(task.getId()).size() > 0;
        dto.setHasSubtask(hasSubtasks);
        
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
                    // Also add tasks assigned to units under this team
                    List<Unit> unitsInTeam = unitRepository.findByTeam_Id(currentUser.getTeam().getId());
                    for (Unit unit : unitsInTeam) {
                        receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(unit.getId()));
                    }
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

    // ✅ UPDATED BUSINESS LOGIC: Cập nhật trạng thái Task dựa trên trạng thái các Assignment con
    // - Nếu có ít nhất 1 assignment quá hạn → OVERDUE (kể cả khi assignments khác đã hủy)
    // - Nếu tất cả assignments đều DONE → COMPLETED  
    // - Nếu có ít nhất 1 assignment WORKING → IN_PROGRESS
    // - Ngược lại → OPEN
    public void updateTaskStatus(Task task) {
        // ✅ Sử dụng repository method thay vì findAll + filter
        List<Assignment> assignments = assignmentRepository.findByTaskId(task.getId());
            
        // Không có assignment nào → OPEN
        if (assignments == null || assignments.isEmpty()) {
            task.setStatus(TaskStatus.OPEN);
            taskRepository.save(task);
            return;
        }
        
        // Check assignment statuses for task status logic
        LocalDateTime now = LocalDateTime.now();
        
        // Tất cả assignments đều DONE → COMPLETED  
        boolean allDone = assignments.stream()
                .allMatch(a -> a.getStatus() == AssignmentStatus.DONE);
                
        // Có ít nhất 1 assignment WORKING → IN_PROGRESS
        boolean anyWorking = assignments.stream()
                .anyMatch(a -> a.getStatus() == AssignmentStatus.WORKING);
        
        // ✅ UPDATED BUSINESS RULE: Task OVERDUE nếu có ít nhất 1 assignment quá hạn 
        // (kể cả khi các assignment khác đã bị hủy)
        boolean hasOverdueAssignment = assignments.stream()
                .anyMatch(a -> a.getDueAt() != null && 
                              a.getDueAt().isBefore(now) && 
                              a.getStatus() != AssignmentStatus.DONE &&
                              a.getStatus() != AssignmentStatus.CANCELLED);
                              
        // ✅ Kiểm tra có assignment quá hạn trong trường hợp các assignment khác đã hủy
        boolean hasOverdueEvenWithCancelled = assignments.stream()
                .anyMatch(a -> a.getStatus() == AssignmentStatus.OVERDUE || 
                              (a.getDueAt() != null && 
                               a.getDueAt().isBefore(now) && 
                               a.getStatus() == AssignmentStatus.WORKING));
        
        // UPDATED Priority logic: OVERDUE (có ít nhất 1 overdue) > COMPLETED > IN_PROGRESS > OPEN
        if (hasOverdueAssignment || hasOverdueEvenWithCancelled) {
            task.setStatus(TaskStatus.OVERDUE);
        } else if (allDone) {
            task.setStatus(TaskStatus.COMPLETED);
        } else if (anyWorking) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            // Tất cả assignments đều CANCELLED hoặc mixed states → OPEN (task có thể assign lại)
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
    public List<TaskSubtreeDTO> getTaskSubtreeAsSubtreeDTO(Integer taskId) {
        List<TaskSubtreeDTO> result = new ArrayList<>();
        
        // Lấy task gốc trước
        TaskSubtreeDTO rootTask = convertTaskDetailToSubtreeDTO(getTaskDetailById(taskId));
        if (rootTask == null) {
            return result; // Trả về list rỗng nếu không tìm thấy task
        }
        
        // Thêm task gốc vào kết quả
        result.add(rootTask);
        
        // Recursively lấy tất cả subtask
        collectSubtasksAsSubtreeDTO(taskId, result);
        
        return result;
    }
    
    /**
     * Helper method để recursively collect tất cả subtask as TaskSubtreeDTO
     * @param parentId ID của task cha
     * @param result List để chứa kết quả
     */
    private void collectSubtasksAsSubtreeDTO(Integer parentId, List<TaskSubtreeDTO> result) {
        List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(parentId);
        
        for (Task subtask : subtasks) {
            TaskDetailDTO subtaskDetail = getTaskDetailById(subtask.getId());
            if (subtaskDetail != null) {
                TaskSubtreeDTO subtaskSubtreeDTO = convertTaskDetailToSubtreeDTO(subtaskDetail);
                // Subtasks không có taskType - đặt về null
                subtaskSubtreeDTO.setTaskType(null);
                result.add(subtaskSubtreeDTO);
                // Recursively lấy subtask của subtask này
                collectSubtasksAsSubtreeDTO(subtask.getId(), result);
            }
        }
    }
    
    /**
     * Helper method để convert TaskDetailDTO thành TaskSubtreeDTO
     * @param taskDetailDTO TaskDetailDTO source
     * @return TaskSubtreeDTO hoặc null nếu input null
     */
    private TaskSubtreeDTO convertTaskDetailToSubtreeDTO(TaskDetailDTO taskDetailDTO) {
        if (taskDetailDTO == null) {
            return null;
        }
        
        TaskSubtreeDTO dto = new TaskSubtreeDTO();
        
        // Copy all fields from TaskDetailDTO to TaskSubtreeDTO
        dto.setId(taskDetailDTO.getId());
        dto.setTitle(taskDetailDTO.getTitle());
        dto.setContent(taskDetailDTO.getContent());
        dto.setInstructions(taskDetailDTO.getInstructions());
        dto.setNotes(taskDetailDTO.getNotes());
        dto.setCreatedAt(taskDetailDTO.getCreatedAt());
        dto.setUpdatedAt(taskDetailDTO.getUpdatedAt());
        dto.setStatus(taskDetailDTO.getStatus());
        dto.setPriority(taskDetailDTO.getPriority());
        dto.setParentId(taskDetailDTO.getParentId());
        dto.setCreatedByUser(taskDetailDTO.getCreatedByUser());
        dto.setAssignments(taskDetailDTO.getAssignments());
        dto.setAttachments(taskDetailDTO.getAttachments());
        
        // Copy taskType - subtasks sẽ có taskType null theo business logic
        // (logic này được handle ở level cao hơn trong buildTaskTree)
        dto.setTaskType(taskDetailDTO.getTaskType());
        
        return dto;
    }

    @Override
    public com.project.quanlycanghangkhong.dto.TaskTreeDTO getTaskSubtreeHierarchical(Integer taskId) {
        // Lấy task gốc
        TaskDetailDTO rootTask = getTaskDetailById(taskId);
        if (rootTask == null) {
            return null;
        }
        
        // Tạo TaskTreeDTO từ task gốc
        com.project.quanlycanghangkhong.dto.TaskTreeDTO rootTreeTask = 
            new com.project.quanlycanghangkhong.dto.TaskTreeDTO(rootTask, 0);
        
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
    private void buildTaskTree(com.project.quanlycanghangkhong.dto.TaskTreeDTO parentTreeTask, 
                              Integer parentId, Integer level) {
        List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(parentId);
        
        for (Task subtask : subtasks) {
            TaskDetailDTO subtaskDetail = getTaskDetailById(subtask.getId());
            if (subtaskDetail != null) {
                // Tạo TaskTreeDTO cho subtask
                com.project.quanlycanghangkhong.dto.TaskTreeDTO subtaskTreeDTO = 
                    new com.project.quanlycanghangkhong.dto.TaskTreeDTO(subtaskDetail, level);
                
                // Subtasks không có taskType - đặt về null
                subtaskTreeDTO.setTaskType(null);
                
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
                    // Also add tasks assigned to units under this team
                    List<Unit> unitsInTeam = unitRepository.findByTeam_Id(currentUser.getTeam().getId());
                    for (Unit unit : unitsInTeam) {
                        receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(unit.getId()));
                    }
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
    public com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithCountStandardized(String type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.MyTasksData(
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
                    // Also add tasks assigned to units under this team
                    List<Unit> unitsInTeam = unitRepository.findByTeam_Id(currentUser.getTeam().getId());
                    for (Unit unit : unitsInTeam) {
                        receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(unit.getId()));
                    }
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
        
        return new com.project.quanlycanghangkhong.dto.MyTasksData(
            taskDTOs);
    }

    @Override
    public com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithCountStandardized(String type, String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.MyTasksData(
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
        
        return new com.project.quanlycanghangkhong.dto.MyTasksData(
            taskDTOs);
    }

    @Override
    public com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithCountStandardizedAndPagination(String type, String status, Integer page, Integer size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.MyTasksData(
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
                    // Also add tasks assigned to units under this team
                    List<Unit> unitsInTeam = unitRepository.findByTeam_Id(currentUser.getTeam().getId());
                    for (Unit unit : unitsInTeam) {
                        receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(unit.getId()));
                    }
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
        
        return new com.project.quanlycanghangkhong.dto.MyTasksData(
            paginatedTaskDTOs, paginationInfo);
    }

    /**
     * 🚀 ULTRA OPTIMIZED: Get my tasks with batch loading - Performance target <500ms
     * @param type Task type (created, assigned, received)
     * @return MyTasksData with batch-loaded relationships
     */
    @Override
    public com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithCountStandardizedUltraFast(String type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.MyTasksData(
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
                    // Also add tasks assigned to units under this team
                    List<Unit> unitsInTeam = unitRepository.findByTeam_Id(currentUser.getTeam().getId());
                    for (Unit unit : unitsInTeam) {
                        receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(unit.getId()));
                    }
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
            return new com.project.quanlycanghangkhong.dto.MyTasksData(
                List.of());
        }
        
        // ✅ Convert all tasks to DTOs using simple batch conversion
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        // Use actual task count
        return new com.project.quanlycanghangkhong.dto.MyTasksData(
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
                                                         Map<Integer, User> unitLeadsById,
                                                         Boolean hasSubtask) {
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
        
        // Set TaskType
        if (task.getTaskType() != null) {
            TaskTypeDTO taskTypeDTO = new TaskTypeDTO();
            taskTypeDTO.setId(task.getTaskType().getId());
            taskTypeDTO.setName(task.getTaskType().getName());
            dto.setTaskType(taskTypeDTO);
        }
        
        if (task.getParent() != null) {
            dto.setParentId(task.getParent().getId());
        }
        
        // ✅ Set hasSubtask using pre-loaded data
        dto.setHasSubtask(hasSubtask);
        
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
    // ===================================================================
    
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
        
        // ✅ Batch load subtask counts for all tasks to determine hasSubtask
        Map<Integer, Boolean> hasSubtaskByTaskId = new HashMap<>();
        if (!taskIds.isEmpty()) {
            List<Object[]> subtaskCounts = taskRepository.countSubtasksByParentIds(taskIds);
            for (Object[] row : subtaskCounts) {
                Integer parentId = (Integer) row[0];
                Long count = (Long) row[1];
                hasSubtaskByTaskId.put(parentId, count > 0);
            }
            // Set false for tasks that don't have any subtasks
            for (Integer taskId : taskIds) {
                hasSubtaskByTaskId.putIfAbsent(taskId, false);
            }
        }
        
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
                usersById, teamsById, unitsById, teamLeadsById, unitLeadsById,
                hasSubtaskByTaskId.getOrDefault(task.getId(), false)))
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
    
    
    // ============== DATABASE-LEVEL PAGINATION METHODS (OPTIMIZED) ==============
    
    /**
     * 🚀 DATABASE PAGINATION: Get my tasks with database-level pagination (1-based)
     */
    @Override
    public com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithCountStandardizedAndPaginationOptimized(
            String type, String status, Integer page, Integer size) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.MyTasksData(
                List.of(), new com.project.quanlycanghangkhong.dto.PaginationInfo(1, 20, 0));
        }
        
        Integer userId = currentUser.getId();
        
        // ✅ Normalize 1-based pagination parameters
        int[] normalizedParams = com.project.quanlycanghangkhong.dto.PaginationInfo.normalizePageParams(page, size);
        int currentPage = normalizedParams[0]; // 1-based
        int pageSize = normalizedParams[1];
        
        // ✅ Calculate database offset (0-based for LIMIT/OFFSET)
        int offset = com.project.quanlycanghangkhong.dto.PaginationInfo.calculateOffset(currentPage, pageSize);
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
            totalCount = getFilteredTaskCountOptimized(type, status, userId, currentUser);
        }
        
        // ✅ Convert to DTOs (only paginated data)
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        // ✅ Create pagination info (1-based) - now accurate after status filtering
        com.project.quanlycanghangkhong.dto.PaginationInfo paginationInfo = 
            new com.project.quanlycanghangkhong.dto.PaginationInfo(currentPage, pageSize, totalCount);
        
        return new com.project.quanlycanghangkhong.dto.MyTasksData(
            taskDTOs, paginationInfo);
    }
    
    /**
     * 🚀 DATABASE PAGINATION: Advanced search with database-level pagination (1-based)
     */
    @Override
    public com.project.quanlycanghangkhong.dto.MyTasksData getMyTasksWithAdvancedSearchAndPaginationOptimized(
            String type, String status, String keyword, String startTime, String endTime,
            java.util.List<String> priorities, java.util.List<String> recipientTypes, java.util.List<Integer> recipientIds,
            java.util.List<Integer> taskTypeIds, Integer page, Integer size) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.MyTasksData(
                List.of(), new com.project.quanlycanghangkhong.dto.PaginationInfo(1, 20, 0));
        }
        
        Integer userId = currentUser.getId();
        
        // ✅ Normalize 1-based pagination parameters
        int[] normalizedParams = com.project.quanlycanghangkhong.dto.PaginationInfo.normalizePageParams(page, size);
        int currentPage = normalizedParams[0]; // 1-based
        int pageSize = normalizedParams[1];
        
        // ✅ Calculate database offset (0-based for LIMIT/OFFSET)
        int offset = com.project.quanlycanghangkhong.dto.PaginationInfo.calculateOffset(currentPage, pageSize);
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
                taskTypeIds != null ? taskTypeIds : List.of(),
                pageable);
            totalCount = taskRepository.countAssignedTasksWithAdvancedSearchMulti(
                userId, keyword, finalStartDateTime, finalEndDateTime, priorityEnums,
                recipientTypes != null ? recipientTypes : List.of(), 
                recipientIds != null ? recipientIds : List.of(),
                taskTypeIds != null ? taskTypeIds : List.of());
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
                    
                    // TaskType filter
                    if (taskTypeIds != null && !taskTypeIds.isEmpty()) {
                        if (task.getTaskType() == null || !taskTypeIds.contains(task.getTaskType().getId())) {
                            return false;
                        }
                    }
                    
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
                    
                    // TaskType filter
                    if (taskTypeIds != null && !taskTypeIds.isEmpty()) {
                        if (task.getTaskType() == null || !taskTypeIds.contains(task.getTaskType().getId())) {
                            return false;
                        }
                    }
                    
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
        
        // ✅ Apply status filter cho type=assigned và type=received using TaskStatusMapper
        if (type.matches("assigned|received") && status != null && !status.trim().isEmpty()) {
            java.util.function.Predicate<Task> statusFilter = com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
            tasks = tasks.stream()
                .filter(statusFilter)
                .collect(java.util.stream.Collectors.toList());
        }
        
        // ✅ Convert to DTOs (only paginated data)
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        // ✅ Create pagination info (1-based)
        com.project.quanlycanghangkhong.dto.PaginationInfo paginationInfo = 
            new com.project.quanlycanghangkhong.dto.PaginationInfo(currentPage, pageSize, totalCount);
        
        return new com.project.quanlycanghangkhong.dto.MyTasksData(
            taskDTOs, paginationInfo);
    }
    
    /**
     * 🏢 COMPANY TASKS: Get tasks with advanced search, pagination, role-based permissions and recipient search
     */
    @Override
    public com.project.quanlycanghangkhong.dto.MyTasksData getCompanyTasksWithAdvancedSearchAndPagination(
            String status, String keyword, String startTime, String endTime, 
            java.util.List<String> priorities, java.util.List<String> recipientTypes, 
            java.util.List<Integer> recipientIds, java.util.List<Integer> taskTypeIds, Integer page, Integer size) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.MyTasksData(
                List.of(), new com.project.quanlycanghangkhong.dto.PaginationInfo(1, 20, 0));
        }
        
        // Normalize pagination parameters
        int[] normalizedParams = com.project.quanlycanghangkhong.dto.PaginationInfo.normalizePageParams(page, size);
        int currentPage = normalizedParams[0]; // 1-based
        int pageSize = normalizedParams[1];
        
        // Parse time filters
        java.time.LocalDateTime tempStartDateTime = null;
        java.time.LocalDateTime tempEndDateTime = null;
        
        try {
            if (startTime != null && !startTime.isEmpty()) {
                tempStartDateTime = java.time.LocalDate.parse(startTime).atStartOfDay();
            }
        } catch (Exception e) {
            // Invalid start date format, keep null
        }
        
        try {
            if (endTime != null && !endTime.isEmpty()) {
                tempEndDateTime = java.time.LocalDate.parse(endTime).atTime(23, 59, 59);
            }
        } catch (Exception e) {
            // Invalid end date format, keep null
        }
        
        final java.time.LocalDateTime finalStartDateTime = tempStartDateTime;
        final java.time.LocalDateTime finalEndDateTime = tempEndDateTime;
        
        // Parse priority filters
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
        
        // Get all tasks based on role
        List<Task> allTasks = getTasksBasedOnRole(currentUser);
        
        // Apply advanced search filters
        final String finalKeyword = keyword;
        List<Task> filteredTasks = allTasks.stream()
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
                
                // TaskType filter
                if (taskTypeIds != null && !taskTypeIds.isEmpty()) {
                    if (task.getTaskType() == null || !taskTypeIds.contains(task.getTaskType().getId())) {
                        return false;
                    }
                }
                
                // Recipient filter (similar to assigned type in /my API)
                if (recipientTypes != null && !recipientTypes.isEmpty() && recipientIds != null && !recipientIds.isEmpty()) {
                    List<Assignment> taskAssignments = assignmentRepository.findByTaskId(task.getId());
                    boolean hasMatchingRecipient = false;
                    
                    for (int i = 0; i < recipientTypes.size(); i++) {
                        String targetType = recipientTypes.get(i);
                        Integer targetId = recipientIds.get(i);
                        
                        for (Assignment assignment : taskAssignments) {
                            if (targetType.equalsIgnoreCase(assignment.getRecipientType()) && 
                                targetId.equals(assignment.getRecipientId())) {
                                hasMatchingRecipient = true;
                                break;
                            }
                        }
                        if (hasMatchingRecipient) break;
                    }
                    
                    if (!hasMatchingRecipient) return false;
                }
                
                return true;
            })
            .collect(java.util.stream.Collectors.toList());
        
        // Apply status filter if specified
        if (status != null && !status.trim().isEmpty()) {
            java.util.function.Predicate<Task> statusFilter = 
                com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
            filteredTasks = filteredTasks.stream()
                .filter(statusFilter)
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Apply pagination
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, filteredTasks.size());
        List<Task> paginatedTasks = startIndex < filteredTasks.size() ? 
            filteredTasks.subList(startIndex, endIndex) : List.of();
        
        // Convert to DTOs
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(paginatedTasks);
        
        // Create pagination info
        com.project.quanlycanghangkhong.dto.PaginationInfo paginationInfo = 
            new com.project.quanlycanghangkhong.dto.PaginationInfo(currentPage, pageSize, filteredTasks.size());
        
        return new com.project.quanlycanghangkhong.dto.MyTasksData(taskDTOs, paginationInfo);
    }
    
    // ============== UNIT TASKS METHODS (ROLE-BASED PERMISSIONS) ==============
    
    /**
     * 🏢 UNIT TASKS: Get all tasks with role-based permissions
     */
    @Override
    public com.project.quanlycanghangkhong.dto.MyTasksData getUnitTasks(String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.MyTasksData(
                List.of(), new com.project.quanlycanghangkhong.dto.PaginationInfo(1, 20, 0));
        }
        
        // Get all tasks based on role
        List<Task> allTasks = getTasksBasedOnRole(currentUser);
        
        // Apply status filter if specified
        if (status != null && !status.trim().isEmpty()) {
            java.util.function.Predicate<Task> statusFilter = 
                com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
            allTasks = allTasks.stream()
                .filter(statusFilter)
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Convert to DTOs
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(allTasks);
        
        // Create pagination info
        com.project.quanlycanghangkhong.dto.PaginationInfo paginationInfo = 
            new com.project.quanlycanghangkhong.dto.PaginationInfo(1, taskDTOs.size(), taskDTOs.size());
        
        return new com.project.quanlycanghangkhong.dto.MyTasksData(taskDTOs, paginationInfo);
    }
    
    /**
     * 🏢 UNIT TASKS: Get tasks with pagination and role-based permissions
     */
    @Override
    public com.project.quanlycanghangkhong.dto.MyTasksData getUnitTasksWithPagination(String status, Integer page, Integer size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.MyTasksData(
                List.of(), new com.project.quanlycanghangkhong.dto.PaginationInfo(1, 20, 0));
        }
        
        // Normalize pagination parameters
        int[] normalizedParams = com.project.quanlycanghangkhong.dto.PaginationInfo.normalizePageParams(page, size);
        int currentPage = normalizedParams[0]; // 1-based
        int pageSize = normalizedParams[1];
        
        // Get all tasks based on role
        List<Task> allTasks = getTasksBasedOnRole(currentUser);
        
        // Apply status filter if specified
        if (status != null && !status.trim().isEmpty()) {
            java.util.function.Predicate<Task> statusFilter = 
                com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
            allTasks = allTasks.stream()
                .filter(statusFilter)
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Apply pagination
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allTasks.size());
        List<Task> paginatedTasks = startIndex < allTasks.size() ? 
            allTasks.subList(startIndex, endIndex) : List.of();
        
        // Convert to DTOs
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(paginatedTasks);
        
        // Create pagination info
        com.project.quanlycanghangkhong.dto.PaginationInfo paginationInfo = 
            new com.project.quanlycanghangkhong.dto.PaginationInfo(currentPage, pageSize, allTasks.size());
        
        return new com.project.quanlycanghangkhong.dto.MyTasksData(taskDTOs, paginationInfo);
    }
    
    /**
     * 🏢 UNIT TASKS: Get tasks with advanced search, pagination and role-based permissions
     */
    @Override
    public com.project.quanlycanghangkhong.dto.MyTasksData getUnitTasksWithAdvancedSearchAndPagination(
            String status, String keyword, String startTime, String endTime, 
            java.util.List<String> priorities, Integer page, Integer size) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.MyTasksData(
                List.of(), new com.project.quanlycanghangkhong.dto.PaginationInfo(1, 20, 0));
        }
        
        // Normalize pagination parameters
        int[] normalizedParams = com.project.quanlycanghangkhong.dto.PaginationInfo.normalizePageParams(page, size);
        int currentPage = normalizedParams[0]; // 1-based
        int pageSize = normalizedParams[1];
        
        // Parse time filters
        java.time.LocalDateTime tempStartDateTime = null;
        java.time.LocalDateTime tempEndDateTime = null;
        
        try {
            if (startTime != null && !startTime.isEmpty()) {
                tempStartDateTime = java.time.LocalDate.parse(startTime).atStartOfDay();
            }
        } catch (Exception e) {
            // Invalid start date format, keep null
        }
        
        try {
            if (endTime != null && !endTime.isEmpty()) {
                tempEndDateTime = java.time.LocalDate.parse(endTime).atTime(23, 59, 59);
            }
        } catch (Exception e) {
            // Invalid end date format, keep null
        }
        
        final java.time.LocalDateTime finalStartDateTime = tempStartDateTime;
        final java.time.LocalDateTime finalEndDateTime = tempEndDateTime;
        
        // Parse priority filters
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
        
        // Get all tasks based on role
        List<Task> allTasks = getTasksBasedOnRole(currentUser);
        
        // Apply advanced search filters
        final String finalKeyword = keyword;
        List<Task> filteredTasks = allTasks.stream()
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
        
        // Apply status filter if specified
        if (status != null && !status.trim().isEmpty()) {
            java.util.function.Predicate<Task> statusFilter = 
                com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
            filteredTasks = filteredTasks.stream()
                .filter(statusFilter)
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Apply pagination
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, filteredTasks.size());
        List<Task> paginatedTasks = startIndex < filteredTasks.size() ? 
            filteredTasks.subList(startIndex, endIndex) : List.of();
        
        // Convert to DTOs
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(paginatedTasks);
        
        // Create pagination info
        com.project.quanlycanghangkhong.dto.PaginationInfo paginationInfo = 
            new com.project.quanlycanghangkhong.dto.PaginationInfo(currentPage, pageSize, filteredTasks.size());
        
        return new com.project.quanlycanghangkhong.dto.MyTasksData(taskDTOs, paginationInfo);
    }
    
    // ============== HELPER METHODS ==============
    
    /**
     * Get tasks based on user role
     */
    private List<Task> getTasksBasedOnRole(User currentUser) {
        String roleName = currentUser.getRole() != null ? currentUser.getRole().getRoleName() : null;
        
        // ADMIN, DIRECTOR, VICE_DIRECTOR see all tasks
        if ("ADMIN".equals(roleName) || "DIRECTOR".equals(roleName) || "VICE_DIRECTOR".equals(roleName)) {
            return taskRepository.findAllByDeletedFalse();
        } else {
            // Other roles see only team tasks (assigned to their team)
            return getTeamTasks(currentUser);
        }
    }
    
    /**
     * Get team tasks (tasks assigned TO the team AND to units under the team)
     */
    private List<Task> getTeamTasks(User currentUser) {
        if (currentUser.getTeam() != null) {
            List<Task> teamTasks = new ArrayList<>();
            
            // Add tasks assigned directly to the team
            teamTasks.addAll(taskRepository.findReceivedTasksByTeamId(currentUser.getTeam().getId()));
            
            // Add tasks assigned to units under this team
            List<Unit> unitsInTeam = unitRepository.findByTeam_Id(currentUser.getTeam().getId());
            for (Unit unit : unitsInTeam) {
                teamTasks.addAll(taskRepository.findReceivedTasksByUnitId(unit.getId()));
            }
            
            // Remove duplicates using stream distinct
            return teamTasks.stream().distinct().collect(java.util.stream.Collectors.toList());
        }
        return List.of();
    }
    
    /**
     * Get filtered task count for pagination accuracy
     */
    private long getFilteredTaskCountOptimized(String type, String status, Integer userId, User currentUser) {
        // Get all tasks for the type first
        List<Task> allTasks;
        switch (type.toLowerCase()) {
            case "created":
                allTasks = taskRepository.findCreatedTasksWithoutAssignments(userId);
                break;
            case "assigned":
                allTasks = taskRepository.findAssignedTasksByUserId(userId);
                break;
            case "received":
                Integer teamId = (currentUser.getRole() != null && 
                    "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getTeam() != null) ? currentUser.getTeam().getId() : null;
                Integer unitId = (currentUser.getRole() != null && 
                    "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getUnit() != null) ? currentUser.getUnit().getId() : null;
                
                List<Task> receivedTasks = new ArrayList<>();
                receivedTasks.addAll(taskRepository.findReceivedTasksByUserId(userId));
                if (teamId != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByTeamId(teamId));
                    // Also add tasks assigned to units under this team
                    List<Unit> unitsInTeam = unitRepository.findByTeam_Id(teamId);
                    for (Unit unit : unitsInTeam) {
                        receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(unit.getId()));
                    }
                }
                if (unitId != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(unitId));
                }
                allTasks = receivedTasks.stream().distinct().collect(java.util.stream.Collectors.toList());
                break;
            default:
                allTasks = List.of();
        }
        
        // Apply status filter and count
        if (status != null && !status.trim().isEmpty()) {
            java.util.function.Predicate<Task> statusFilter = 
                com.project.quanlycanghangkhong.util.TaskStatusMapper.getStatusFilter(status);
            return allTasks.stream()
                .filter(statusFilter)
                .count();
        }
        
        return allTasks.size();
    }
}
