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
import com.project.quanlycanghangkhong.dto.simplified.SimpleUserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    // ✅ DEPTH CONTROL: Overloaded method với depth limiting
    private TaskDetailDTO convertToTaskDetailDTOOptimized(Task task, int currentDepth) {
        // ✅ Convert base task info (copy từ method gốc)
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
        
        if (task.getCreatedBy() != null) {
            dto.setCreatedByUser(new UserDTO(task.getCreatedBy()));
        }
        
        // ✅ Assignments đã được fetch với JOIN - không cần query thêm
        List<AssignmentDTO> assignmentDTOs = task.getAssignments().stream()
            .map(this::convertToAssignmentDTOOptimized)
            .toList();
        dto.setAssignments(assignmentDTOs);
        
        // ✅ Load attachments riêng để tránh MultipleBagFetchException
        List<AttachmentDTO> attachmentDTOs = attachmentRepository.findByTask_IdAndIsDeletedFalse(task.getId())
            .stream()
            .map(this::convertToAttachmentDTOOptimized)
            .toList();
        dto.setAttachments(attachmentDTOs);
        
        // Set current depth
        dto.setCurrentDepth(currentDepth);
        dto.setHierarchyLevel(currentDepth);
        
        // ✅ DEPTH CONTROL: Chỉ load subtasks nếu chưa vượt quá MAX_SUBTASK_DEPTH
        if (TaskDetailDTO.canLoadSubtasksAtLevel(currentDepth)) {
            // Load subtasks với depth + 1
            List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(task.getId());
            if (!subtasks.isEmpty()) {
                List<TaskDetailDTO> subtaskDTOs = subtasks.stream()
                    .map(subtask -> {
                        // Load subtask với depth được tăng lên
                        Task subtaskWithRelations = taskRepository.findTaskWithAllRelationships(subtask.getId())
                            .orElse(subtask);
                        return convertToTaskDetailDTOOptimized(subtaskWithRelations, currentDepth + 1);
                    })
                    .toList();
                dto.setSubtasks(subtaskDTOs);
            }
            
            // Check if có subtasks ở level tiếp theo (cho hasMoreSubtasks flag)
            if (currentDepth + 1 >= TaskDetailDTO.MAX_SUBTASK_DEPTH) {
                // Kiểm tra xem có subtasks ở level sâu hơn không
                boolean hasDeepSubtasks = subtasks.stream()
                    .anyMatch(subtask -> !taskRepository.findByParentIdAndDeletedFalse(subtask.getId()).isEmpty());
                dto.setHasMoreSubtasks(hasDeepSubtasks);
            }
        } else {
            // Đã vượt quá MAX_SUBTASK_DEPTH, không load subtasks nhưng check có subtasks không
            List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(task.getId());
            dto.setHasMoreSubtasks(!subtasks.isEmpty());
            dto.setSubtasks(new ArrayList<>()); // Empty list
        }
        
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

    // ✅ Optimize recipient user loading
    private void setRecipientUserOptimized(AssignmentDTO adto, String recipientType, Integer recipientId) {
        if (recipientId == null) return;
        
        switch (recipientType.toLowerCase()) {
            case "user":
                userRepository.findById(recipientId)
                    .ifPresent(u -> adto.setRecipientUser(new UserDTO(u)));
                break;
            case "team":
                userRepository.findTeamLeadByTeamId(recipientId)
                    .ifPresent(u -> adto.setRecipientUser(new UserDTO(u)));
                break;
            case "unit":
                userRepository.findUnitLeadByUnitId(recipientId)
                    .ifPresent(u -> adto.setRecipientUser(new UserDTO(u)));
                break;
        }
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
        // Lấy user hiện tại từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return List.of();
        }
        
        Integer currentUserId = currentUser.getId();
        
        switch (type.toLowerCase()) {
            case "created":
                // 🚀 OPTIMIZED: Lấy tasks đã tạo nhưng chưa có assignment (logic cũ)
                List<Task> createdTasks = taskRepository.findCreatedTasksWithoutAssignments(currentUserId);
                
                // ✅ Trả về flat list, KHÔNG cần hierarchy level
                return createdTasks.stream()
                    .map(task -> getTaskDetailById(task.getId()))
                    .collect(Collectors.toList());
                
            case "assigned":
                // 🚀 OPTIMIZED + 🌲 HIERARCHICAL: Lấy tasks đã giao + tất cả subtasks
                List<Task> assignedTasks = taskRepository.findAssignedTasksByUserId(currentUserId);
                
                // 🔧 FIX DUPLICATE ISSUE: Chỉ lấy ROOT TASKS để tránh duplicate
                // Loại bỏ subtasks khỏi root level nếu parent của chúng cũng trong list
                List<Task> rootAssignedTasks = filterOnlyRootTasksFromAssigned(assignedTasks);
                
                // Convert to DTO với nested subtasks structure
                return rootAssignedTasks.stream()
                    .map(task -> {
                        // Load với full relationships
                        Task taskWithRelations = taskRepository.findTaskWithAllRelationships(task.getId())
                            .orElse(task);
                        return convertToTaskDetailDTOOptimized(taskWithRelations, 0);
                    })
                    .sorted((t1, t2) -> {
                        // Sort theo thời gian mới nhất
                        Task task1 = taskRepository.findById(t1.getId()).orElse(null);
                        Task task2 = taskRepository.findById(t2.getId()).orElse(null);
                        if (task1 != null && task2 != null) {
                            int updatedCompare = task2.getUpdatedAt().compareTo(task1.getUpdatedAt());
                            if (updatedCompare != 0) return updatedCompare;
                            return task2.getCreatedAt().compareTo(task1.getCreatedAt());
                        }
                        return 0;
                    })
                    .collect(Collectors.toList());
                
            case "received":
                // 🚀 OPTIMIZED + 🌲 HIERARCHICAL: Lấy tasks được giao + hierarchy levels
                List<Task> receivedTasks = new ArrayList<>();
                
                // 1. Tasks được giao trực tiếp cho user
                receivedTasks.addAll(taskRepository.findReceivedTasksByUserId(currentUserId));
                
                // 2. Tasks được giao cho team (chỉ TEAM_LEAD mới nhận)
                if (currentUser.getRole() != null && 
                    "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getTeam() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByTeamId(currentUser.getTeam().getId()));
                }
                
                // 3. Tasks được giao cho unit (chỉ UNIT_LEAD mới nhận)
                if (currentUser.getRole() != null && 
                    "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
                    currentUser.getUnit() != null) {
                    receivedTasks.addAll(taskRepository.findReceivedTasksByUnitId(currentUser.getUnit().getId()));
                }
                
                // Remove duplicates và giữ nguyên sort order
                List<Task> uniqueReceivedTasks = receivedTasks.stream()
                    .distinct()
                    .sorted((t1, t2) -> {
                        // Sort theo updatedAt DESC, sau đó createdAt DESC
                        int updatedCompare = t2.getUpdatedAt().compareTo(t1.getUpdatedAt());
                        if (updatedCompare != 0) return updatedCompare;
                        return t2.getCreatedAt().compareTo(t1.getCreatedAt());
                    })
                    .collect(Collectors.toList());
                
                // 🔧 FIX DUPLICATE ISSUE: Chỉ lấy ROOT TASKS để tránh duplicate
                List<Task> rootReceivedTasks = filterOnlyRootTasksFromAssigned(uniqueReceivedTasks);
                
                // Convert to DTO với nested subtasks structure
                return rootReceivedTasks.stream()
                    .map(task -> {
                        // Load với full relationships
                        Task taskWithRelations = taskRepository.findTaskWithAllRelationships(task.getId())
                            .orElse(task);
                        return convertToTaskDetailDTOOptimized(taskWithRelations, 0);
                    })
                    .collect(Collectors.toList());
                
            default:
                return List.of();
        }
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

    /**
     * 🔧 ANTI-DUPLICATE: Filter để chỉ lấy root tasks từ assigned tasks list
     * Loại bỏ các subtasks nếu parent task của chúng cũng có trong assigned list
     * @param assignedTasks Danh sách tất cả tasks được assigned
     * @return Chỉ root tasks (không có parent hoặc parent không trong assigned list)
     */
    private List<Task> filterOnlyRootTasksFromAssigned(List<Task> assignedTasks) {
        // Create set of assigned task IDs for fast lookup
        Set<Integer> assignedTaskIds = assignedTasks.stream()
            .map(Task::getId)
            .collect(Collectors.toSet());
        
        // Filter: chỉ giữ lại tasks mà parent không có trong assigned list
        return assignedTasks.stream()
            .filter(task -> {
                if (task.getParent() == null) {
                    // Root task (không có parent) -> always include
                    return true;
                } else {
                    // Subtask -> chỉ include nếu parent KHÔNG có trong assigned list
                    return !assignedTaskIds.contains(task.getParent().getId());
                }
            })
            .collect(Collectors.toList());
    }

    // ============== HELPER METHODS FOR HIERARCHICAL TASK MANAGEMENT ==============
    
    /**
     * 🌲 Lấy tất cả subtasks theo cấu trúc phân cấp (recursive)
    /**
     * 🌳 Lấy task hierarchy với level đúng cho type=assigned
     * FIX: Tránh trùng lặp và đảm bảo hierarchy level chính xác
     * @param assignedTasks Danh sách task được giao
     * @return Danh sách TaskDetailDTO với hierarchyLevel được set đúng
     */
    private List<TaskDetailDTO> getTaskHierarchyWithLevels(List<Task> assignedTasks) {
        // Use Map để tránh trùng lặp và lưu trữ kết quả
        Map<Integer, TaskDetailDTO> resultMap = new HashMap<>();
        Set<Integer> processedIds = new HashSet<>();
        
        // Xử lý từng assigned task
        for (Task assignedTask : assignedTasks) {
            if (!processedIds.contains(assignedTask.getId())) {
                // Tính toán level thực tế của task này (dựa trên cấu trúc parent-child)
                int actualLevel = calculateActualLevel(assignedTask);
                
                // Lấy toàn bộ hierarchy từ task này
                List<TaskDetailDTO> hierarchy = getTaskHierarchyRecursive(assignedTask, actualLevel);
                
                // Merge vào result map
                for (TaskDetailDTO task : hierarchy) {
                    if (!resultMap.containsKey(task.getId())) {
                        resultMap.put(task.getId(), task);
                        processedIds.add(task.getId());
                    }
                }
            }
        }
        
        // Convert map to list và sort: hierarchy level trước, sau đó thời gian mới nhất
        return resultMap.values().stream()
            .sorted((t1, t2) -> {
                // 1. Sort theo hierarchyLevel (0=root, 1=child, ...)
                int levelCompare = Integer.compare(t1.getHierarchyLevel(), t2.getHierarchyLevel());
                if (levelCompare != 0) return levelCompare;
                
                // 2. Cùng level thì sort theo thời gian mới nhất (updatedAt DESC, createdAt DESC)
                Task task1 = taskRepository.findById(t1.getId()).orElse(null);
                Task task2 = taskRepository.findById(t2.getId()).orElse(null);
                if (task1 != null && task2 != null) {
                    int updatedCompare = task2.getUpdatedAt().compareTo(task1.getUpdatedAt());
                    if (updatedCompare != 0) return updatedCompare;
                    return task2.getCreatedAt().compareTo(task1.getCreatedAt());
                }
                
                // Fallback: sort theo ID
                return Integer.compare(t1.getId(), t2.getId());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 🧮 Tính toán level thực tế của task trong cây hierarchy
     * @param task Task cần tính level
     * @return Level thực tế (0 = root, 1 = child, etc.)
     */
    private int calculateActualLevel(Task task) {
        int level = 0;
        Task current = task;
        
        // Đi ngược lên parent để tính level
        while (current.getParent() != null) {
            level++;
            current = current.getParent();
            
            // Tránh vòng lặp vô hạn
            if (level > 10) break; 
        }
        
        return level;
    }
    
    /**
     * 🌲 Đệ quy lấy task hierarchy với level chính xác
     * @param task Task hiện tại
     * @param level Level hiện tại trong hierarchy (0=root)
     * @return Danh sách TaskDetailDTO bao gồm task hiện tại và tất cả subtasks
     */
    private List<TaskDetailDTO> getTaskHierarchyRecursive(Task task, int level) {
        List<TaskDetailDTO> result = new ArrayList<>();
        
        // Convert task hiện tại với level (không include subtasks để tránh đệ quy vô hạn)
        TaskDetailDTO taskDetail = convertToTaskDetailDTOSimple(task);
        taskDetail.setHierarchyLevel(level);
        result.add(taskDetail);
        
        // Lấy tất cả subtasks trực tiếp
        List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(task.getId());
        
        // Đệ quy cho mỗi subtask với level tăng lên
        for (Task subtask : subtasks) {
            result.addAll(getTaskHierarchyRecursive(subtask, level + 1));
        }
        
        return result;
    }
    
    /**
     * 🔄 Convert Task to TaskDetailDTO (simple version without subtasks to avoid infinite recursion)
     * ✅ OPTIMIZED: Sử dụng assignments từ task entity thay vì query riêng
     * @param task Task entity
     * @return TaskDetailDTO không bao gồm subtasks
     */
    private TaskDetailDTO convertToTaskDetailDTOSimple(Task task) {
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
        
        // Set parent ID if exists
        if (task.getParent() != null) {
            dto.setParentId(task.getParent().getId());
        }
        
        // Set created by user
        if (task.getCreatedBy() != null) {
            dto.setCreatedByUser(new UserDTO(task.getCreatedBy()));
        }
        
        // ✅ Assignments đã được fetch với JOIN
        List<AssignmentDTO> assignmentDTOs = task.getAssignments().stream()
            .map(this::convertToAssignmentDTOOptimized)
            .toList();
        dto.setAssignments(assignmentDTOs);
        
        // ✅ Load attachments riêng để tránh MultipleBagFetchException
        List<AttachmentDTO> attachmentDTOs = attachmentRepository.findByTask_IdAndIsDeletedFalse(task.getId())
            .stream()
            .map(this::convertToAttachmentDTOOptimized)
            .toList();
        dto.setAttachments(attachmentDTOs);
        
        // NOTE: Không include subtasks để tránh vô hạn đệ quy
        dto.setSubtasks(new ArrayList<>());
        
        return dto;
    }

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
        Integer teamId = currentUser.getTeam() != null ? currentUser.getTeam().getId() : null;
        Integer unitId = currentUser.getUnit() != null ? currentUser.getUnit().getId() : null;
        
        // ✅ Sử dụng optimized repository methods với JOIN FETCH
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithAllRelationships(userId);
                break;
            case "assigned":
                tasks = taskRepository.findAssignedTasksWithAllRelationships(userId);
                break;
            case "received":
                tasks = taskRepository.findReceivedTasksWithAllRelationships(userId, teamId, unitId);
                break;
            default:
                tasks = List.of();
        }
        
        // ✅ Convert với optimized method (không có N+1)
        List<TaskDetailDTO> taskDTOs;
        if ("assigned".equals(type.toLowerCase()) || "received".equals(type.toLowerCase())) {
            // Cho assigned/received: cần hierarchy levels
            taskDTOs = getTaskHierarchyWithLevelsOptimized(tasks);
        } else {
            // Cho created: flat list với batch loading attachments
            taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        }
        
        // ✅ Count sử dụng database count queries thay vì load data
        com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.TaskCountMetadata metadata = 
            calculateTaskCountsOptimized(userId, currentUser);
            
        // Tính totalCount CHỈ từ ROOT TASKS cho type hiện tại
        int totalCount = switch (type.toLowerCase()) {
            case "created" -> metadata.getCreatedCount();
            case "assigned" -> metadata.getAssignedCount(); 
            case "received" -> metadata.getReceivedCount();
            default -> taskDTOs.size();
        };
        
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
                List.of(), 0, type, null);
        }
        
        Integer userId = currentUser.getId();
        Integer teamId = currentUser.getTeam() != null ? currentUser.getTeam().getId() : null;
        Integer unitId = currentUser.getUnit() != null ? currentUser.getUnit().getId() : null;
        
        // ✅ Sử dụng optimized repository methods với JOIN FETCH
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithAllRelationships(userId);
                break;
            case "assigned":
                tasks = taskRepository.findAssignedTasksWithAllRelationships(userId);
                break;
            case "received":
                tasks = taskRepository.findReceivedTasksWithAllRelationships(userId, teamId, unitId);
                break;
            default:
                tasks = List.of();
        }
        
        // ✅ Convert với optimized method (không có N+1)
        List<TaskDetailDTO> taskDTOs;
        if ("assigned".equals(type.toLowerCase()) || "received".equals(type.toLowerCase())) {
            // 🔧 FIX DUPLICATE ISSUE: Chỉ lấy ROOT TASKS để tránh duplicate  
            List<Task> rootTasks = filterOnlyRootTasksFromAssigned(tasks);
            
            // Convert to DTO với nested subtasks structure
            taskDTOs = rootTasks.stream()
                .map(task -> {
                    // Load với full relationships nếu cần
                    Task taskWithRelations = taskRepository.findTaskWithAllRelationships(task.getId())
                        .orElse(task);
                    return convertToTaskDetailDTOOptimized(taskWithRelations, 0);
                })
                .sorted((t1, t2) -> {
                    // Sort theo thời gian mới nhất
                    Task task1 = taskRepository.findById(t1.getId()).orElse(null);
                    Task task2 = taskRepository.findById(t2.getId()).orElse(null);
                    if (task1 != null && task2 != null) {
                        int updatedCompare = task2.getUpdatedAt().compareTo(task1.getUpdatedAt());
                        if (updatedCompare != 0) return updatedCompare;
                        return task2.getCreatedAt().compareTo(task1.getCreatedAt());
                    }
                    return 0;
                })
                .collect(Collectors.toList());
        } else {
            // Cho created: flat list với batch loading attachments
            taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        }
        
        // ✅ Count sử dụng database count queries thay vì load data
        com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.TaskCountMetadata oldMetadata = 
            calculateTaskCountsOptimized(userId, currentUser);
            
        // Convert to simplified metadata structure
        com.project.quanlycanghangkhong.dto.response.task.MyTasksData.TaskMetadata newMetadata = 
            new com.project.quanlycanghangkhong.dto.response.task.MyTasksData.TaskMetadata(
                oldMetadata.getCreatedCount(),
                oldMetadata.getAssignedCount(), 
                oldMetadata.getReceivedCount(),
                oldMetadata.getHierarchyInfo().getRootTasksCount(),
                oldMetadata.getHierarchyInfo().getSubtasksCount(),
                oldMetadata.getHierarchyInfo().getMaxLevel()
            );
            
        // Tính totalCount CHỈ từ ROOT TASKS cho type hiện tại
        int totalCount = switch (type.toLowerCase()) {
            case "created" -> newMetadata.getCreatedCount();
            case "assigned" -> newMetadata.getAssignedCount(); 
            case "received" -> newMetadata.getReceivedCount();
            default -> taskDTOs.size();
        };
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            taskDTOs, totalCount, type, newMetadata);
    }

    // ✅ Optimize hierarchy calculation
    private List<TaskDetailDTO> getTaskHierarchyWithLevelsOptimized(List<Task> parentTasks) {
        Map<Integer, TaskDetailDTO> resultMap = new HashMap<>();
        
        for (Task parentTask : parentTasks) {
            // Calculate level for parent task
            int parentLevel = calculateTaskLevel(parentTask);
            
            // Add parent with its level
            if (!resultMap.containsKey(parentTask.getId())) {
                TaskDetailDTO parentDTO = convertToTaskDetailDTOOptimized(parentTask, 0);
                parentDTO.setHierarchyLevel(parentLevel);
                resultMap.put(parentTask.getId(), parentDTO);
            }
            
            // Add all subtasks recursively
            addSubtasksToHierarchy(parentTask, parentLevel, resultMap);
        }
        
        // Sort by hierarchy level then by update time
        return resultMap.values().stream()
            .sorted((t1, t2) -> {
                int levelCompare = Integer.compare(t1.getHierarchyLevel(), t2.getHierarchyLevel());
                if (levelCompare != 0) return levelCompare;
                return t2.getUpdatedAt().compareTo(t1.getUpdatedAt());
            })
            .collect(Collectors.toList());
    }

    // ✅ Recursive subtask adding với optimal queries
    private void addSubtasksToHierarchy(Task parentTask, int parentLevel, Map<Integer, TaskDetailDTO> resultMap) {
        List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(parentTask.getId());
        
        for (Task subtask : subtasks) {
            int subtaskLevel = parentLevel + 1;
            
            if (!resultMap.containsKey(subtask.getId())) {
                // Load subtask with relationships if not cached
                Task subtaskWithRelations = taskRepository.findTaskWithAllRelationships(subtask.getId()).orElse(subtask);
                TaskDetailDTO subtaskDTO = convertToTaskDetailDTOOptimized(subtaskWithRelations, 0);
                subtaskDTO.setHierarchyLevel(subtaskLevel);
                resultMap.put(subtask.getId(), subtaskDTO);
                
                // Recursive call for deeper levels
                addSubtasksToHierarchy(subtask, subtaskLevel, resultMap);
            }
        }
    }

    // ✅ Calculate task level efficiently  
    private int calculateTaskLevel(Task task) {
        int level = 0;
        Task current = task;
        
        while (current.getParent() != null) {
            level++;
            current = current.getParent();
            if (level > 10) break; // Prevent infinite loop
        }
        
        return level;
    }

    // ✅ Optimize metadata calculation với count queries
    private com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.TaskCountMetadata calculateTaskCountsOptimized(Integer userId, User currentUser) {
        // Count created root tasks (chỉ root tasks)
        long createdCount = taskRepository.countCreatedRootTasksWithoutAssignments(userId);
        
        // Count assigned root tasks (chỉ root tasks)  
        long assignedCount = taskRepository.countAssignedRootTasksByUserId(userId);
        
        // Count received root tasks (user + team + unit)
        long receivedCount = taskRepository.countReceivedRootTasksByUserId(userId);
        
        // Add team assignments if user is TEAM_LEAD
        if (currentUser.getRole() != null && 
            "TEAM_LEAD".equals(currentUser.getRole().getRoleName()) &&
            currentUser.getTeam() != null) {
            receivedCount += taskRepository.countReceivedRootTasksByTeamId(currentUser.getTeam().getId());
        }
        
        // Add unit assignments if user is UNIT_LEAD
        if (currentUser.getRole() != null && 
            "UNIT_LEAD".equals(currentUser.getRole().getRoleName()) &&
            currentUser.getUnit() != null) {
            receivedCount += taskRepository.countReceivedRootTasksByUnitId(currentUser.getUnit().getId());
        }
        
        // Calculate hierarchy info nếu cần
        com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.HierarchyInfo hierarchyInfo = 
            new com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.HierarchyInfo(
                (int)assignedCount, 0, 0, new HashMap<>());
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.TaskCountMetadata(
            (int)createdCount, (int)assignedCount, (int)receivedCount, hierarchyInfo);
    }
    
    // ✅ BATCH LOADING: Convert multiple tasks với batch loading attachments
    private List<TaskDetailDTO> convertTasksToTaskDetailDTOsBatch(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Batch load tất cả attachments cho all tasks trong 1 query
        List<Integer> taskIds = tasks.stream().map(Task::getId).toList();
        List<Attachment> allAttachments = attachmentRepository.findByTaskIdsAndIsDeletedFalse(taskIds);
        
        // Group attachments by task ID để mapping nhanh
        Map<Integer, List<Attachment>> attachmentsByTaskId = allAttachments.stream()
            .collect(Collectors.groupingBy(att -> att.getTask().getId()));
        
        // Convert each task với attachments đã được batch load
        return tasks.stream()
            .map(task -> convertToTaskDetailDTOWithPreloadedAttachments(task, 
                attachmentsByTaskId.getOrDefault(task.getId(), new ArrayList<>())))
            .collect(Collectors.toList());
    }
    
    // ✅ Convert single task với attachments đã được preload
    private TaskDetailDTO convertToTaskDetailDTOWithPreloadedAttachments(Task task, List<Attachment> preloadedAttachments) {
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
        
        if (task.getCreatedBy() != null) {
            dto.setCreatedByUser(new UserDTO(task.getCreatedBy()));
        }
        
        // ✅ Assignments đã được fetch với JOIN
        List<AssignmentDTO> assignmentDTOs = task.getAssignments().stream()
            .map(this::convertToAssignmentDTOOptimized)
            .toList();
        dto.setAssignments(assignmentDTOs);
        
        // ✅ Sử dụng preloaded attachments thay vì query riêng
        List<AttachmentDTO> attachmentDTOs = preloadedAttachments.stream()
            .map(this::convertToAttachmentDTOOptimized)
            .toList();
        dto.setAttachments(attachmentDTOs);
        
        dto.setSubtasks(new ArrayList<>());
        
        return dto;
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
        
        // Depth control (giữ nguyên logic từ TaskDetailDTO)
        dto.setCurrentDepth(currentDepth);
        dto.setHierarchyLevel(currentDepth);
        
        // Load subtasks với depth control
        if (TaskDetailSimplifiedDTO.canLoadSubtasksAtLevel(currentDepth)) {
            List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(task.getId());
            if (!subtasks.isEmpty()) {
                List<TaskDetailSimplifiedDTO> subtaskDTOs = subtasks.stream()
                    .map(subtask -> {
                        Task subtaskWithRelations = taskRepository.findTaskWithAllRelationships(subtask.getId())
                            .orElse(subtask);
                        return convertToTaskDetailSimplifiedDTO(subtaskWithRelations, currentDepth + 1);
                    })
                    .toList();
                dto.setSubtasks(subtaskDTOs);
            }
            
            // Check if có subtasks ở level tiếp theo
            if (currentDepth + 1 >= TaskDetailSimplifiedDTO.MAX_SUBTASK_DEPTH) {
                boolean hasDeepSubtasks = subtasks.stream()
                    .anyMatch(subtask -> !taskRepository.findByParentIdAndDeletedFalse(subtask.getId()).isEmpty());
                dto.setHasMoreSubtasks(hasDeepSubtasks);
            }
        } else {
            // Đã vượt quá MAX_SUBTASK_DEPTH
            List<Task> subtasks = taskRepository.findByParentIdAndDeletedFalse(task.getId());
            dto.setHasMoreSubtasks(!subtasks.isEmpty());
            dto.setSubtasks(new ArrayList<>());
        }
        
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
     * Convert User to SimpleUserInfo (flattened)
     */
    private SimpleUserInfo convertToSimpleUserInfo(User user) {
        if (user == null) return null;
        
        SimpleUserInfo info = new SimpleUserInfo();
        info.setUserId(user.getId());
        info.setUserName(user.getName());
        info.setUserEmail(user.getEmail());
        
        // Team and role info if available
        if (user.getTeam() != null) {
            info.setTeamName(user.getTeam().getTeamName());
        }
        if (user.getRole() != null) {
            info.setRoleName(user.getRole().getRoleName());
        }
        
        return info;
    }
    
}
