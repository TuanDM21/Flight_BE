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
import com.project.quanlycanghangkhong.dto.response.task.MyTasksData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
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
        Integer teamId = currentUser.getTeam() != null ? currentUser.getTeam().getId() : null;
        Integer unitId = currentUser.getUnit() != null ? currentUser.getUnit().getId() : null;
        
        // ✅ Sử dụng ULTRA FAST native queries để đạt <1s performance
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithoutAssignments(userId);
                break;
            case "assigned":
                // 🚀 ULTRA FAST: Use native query for maximum performance
                List<Object[]> assignedResults = taskRepository.findAssignedTasksUltraFast(userId);
                tasks = convertNativeResultsToTasks(assignedResults);
                break;
            case "received":
                // 🚀 ULTRA FAST: Use native query for maximum performance
                List<Object[]> receivedResults = taskRepository.findReceivedTasksUltraFast(userId, teamId, unitId);
                tasks = convertNativeResultsToTasks(receivedResults);
                break;
            default:
                tasks = List.of();
        }
        
        // ✅ Convert với optimized method (không có N+1)
        List<TaskDetailDTO> taskDTOs;
        // Cho tất cả types: flat list với batch loading attachments
        taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
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
        
        // ✅ Sử dụng ULTRA FAST native queries để đạt <1s performance
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithoutAssignments(userId);
                break;
            case "assigned":
                // 🚀 ULTRA FAST: Use native query for maximum performance
                List<Object[]> assignedResults = taskRepository.findAssignedTasksUltraFast(userId);
                tasks = convertNativeResultsToTasks(assignedResults);
                break;
            case "received":
                // 🚀 ULTRA FAST: Use native query for maximum performance
                List<Object[]> receivedResults = taskRepository.findReceivedTasksUltraFast(userId, teamId, unitId);
                tasks = convertNativeResultsToTasks(receivedResults);
                break;
            default:
                tasks = List.of();
        }
        
        // ✅ Simplified conversion - no complex nested loading for better performance
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        // ✅ Count sử dụng database count queries thay vì load data
        com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.TaskCountMetadata oldMetadata = 
            calculateTaskCountsOptimized(userId, currentUser);
            
        // Convert to simplified metadata structure (only basic counts)
        com.project.quanlycanghangkhong.dto.response.task.MyTasksData.TaskMetadata newMetadata = 
            new com.project.quanlycanghangkhong.dto.response.task.MyTasksData.TaskMetadata(
                oldMetadata.getCreatedCount(),
                oldMetadata.getAssignedCount(), 
                oldMetadata.getReceivedCount()
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

    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithCountStandardized(String type, String filter) {
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
        
        // 🚀 ULTRA FAST: Sử dụng native queries cho assigned/received, relationships cho created
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                // Created tasks ít hơn, có thể dùng relationships
                tasks = taskRepository.findCreatedTasksWithAllRelationships(userId);
                break;
            case "assigned":
                // 🚀 ULTRA FAST: Use native query for maximum performance
                List<Object[]> assignedResults = taskRepository.findAssignedTasksUltraFast(userId);
                tasks = convertNativeResultsToTasks(assignedResults);
                break;
            case "received":
                // 🚀 ULTRA FAST: Use native query for maximum performance
                List<Object[]> receivedResults = taskRepository.findReceivedTasksUltraFast(userId, teamId, unitId);
                tasks = convertNativeResultsToTasks(receivedResults);
                break;
            default:
                tasks = List.of();
        }
        
        // ✅ Apply filter chỉ cho type=assigned
        if ("assigned".equals(type.toLowerCase()) && filter != null) {
            tasks = filterAssignedTasks(tasks, filter);
        }
        
        // � SIMPLIFIED CONVERSION: No complex nested loading for performance
        List<TaskDetailDTO> taskDTOs = convertTasksToTaskDetailDTOsBatch(tasks);
        
        // ✅ Count sử dụng database count queries thay vì load data
        com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.TaskCountMetadata oldMetadata = 
            calculateTaskCountsOptimized(userId, currentUser);
            
        // Convert to simplified metadata structure (only basic counts)
        com.project.quanlycanghangkhong.dto.response.task.MyTasksData.TaskMetadata newMetadata = 
            new com.project.quanlycanghangkhong.dto.response.task.MyTasksData.TaskMetadata(
                oldMetadata.getCreatedCount(),
                oldMetadata.getAssignedCount(), 
                oldMetadata.getReceivedCount()
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

    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithCountStandardizedAndPagination(String type, String filter, Integer page, Integer size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of(), 0, type, null, new PaginationInfo(page != null ? page : 0, size != null ? size : 20, 0));
        }
        
        Integer userId = currentUser.getId();
        Integer teamId = currentUser.getTeam() != null ? currentUser.getTeam().getId() : null;
        Integer unitId = currentUser.getUnit() != null ? currentUser.getUnit().getId() : null;
        
        // Set default pagination values
        int currentPage = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        
        // 🚀 ULTRA FAST: Sử dụng native queries cho assigned/received, relationships cho created
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                // Created tasks ít hơn, có thể dùng relationships
                tasks = taskRepository.findCreatedTasksWithAllRelationships(userId);
                break;
            case "assigned":
                // 🚀 ULTRA FAST: Use native query for maximum performance
                List<Object[]> assignedResults = taskRepository.findAssignedTasksUltraFast(userId);
                tasks = convertNativeResultsToTasks(assignedResults);
                break;
            case "received":
                // 🚀 ULTRA FAST: Use native query for maximum performance
                List<Object[]> receivedResults = taskRepository.findReceivedTasksUltraFast(userId, teamId, unitId);
                tasks = convertNativeResultsToTasks(receivedResults);
                break;
            default:
                tasks = List.of();
        }
        
        // ✅ Apply filter chỉ cho type=assigned
        if ("assigned".equals(type.toLowerCase()) && filter != null) {
            tasks = filterAssignedTasks(tasks, filter);
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
        
        // ✅ Count sử dụng database count queries thay vì load data (for total count)
        com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse.TaskCountMetadata oldMetadata = 
            calculateTaskCountsOptimized(userId, currentUser);
            
        // Convert to simplified metadata structure (only basic counts)
        com.project.quanlycanghangkhong.dto.response.task.MyTasksData.TaskMetadata newMetadata = 
            new com.project.quanlycanghangkhong.dto.response.task.MyTasksData.TaskMetadata(
                oldMetadata.getCreatedCount(),
                oldMetadata.getAssignedCount(), 
                oldMetadata.getReceivedCount()
            );
            
        // Tính totalCount CHỈ từ ROOT TASKS cho type hiện tại hoặc sau filter
        int totalCount;
        if ("assigned".equals(type.toLowerCase()) && filter != null) {
            // Nếu có filter, count từ filtered results
            totalCount = paginatedTaskDTOs.size();
        } else {
            // Không có filter, dùng metadata count
            totalCount = switch (type.toLowerCase()) {
                case "created" -> newMetadata.getCreatedCount();
                case "assigned" -> newMetadata.getAssignedCount(); 
                case "received" -> newMetadata.getReceivedCount();
                default -> paginatedTaskDTOs.size();
            };
        }
        
        // Create pagination info
        PaginationInfo paginationInfo = new PaginationInfo(currentPage, pageSize, totalCount);
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            paginatedTaskDTOs, totalCount, type, newMetadata, paginationInfo);
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
                List.of(), 0, type, null);
        }
        
        Integer userId = currentUser.getId();
        Integer teamId = currentUser.getTeam() != null ? currentUser.getTeam().getId() : null;
        Integer unitId = currentUser.getUnit() != null ? currentUser.getUnit().getId() : null;
        
        // 🚀 Step 1: Get tasks using ultra-fast native queries
        List<Task> tasks;
        switch (type.toLowerCase()) {
            case "created":
                tasks = taskRepository.findCreatedTasksWithoutAssignments(userId);
                break;
            case "assigned":
                List<Object[]> assignedResults = taskRepository.findAssignedTasksUltraFast(userId);
                tasks = convertNativeResultsToTasks(assignedResults);
                break;
            case "received":
                List<Object[]> receivedResults = taskRepository.findReceivedTasksUltraFast(userId, teamId, unitId);
                tasks = convertNativeResultsToTasks(receivedResults);
                break;
            default:
                tasks = List.of();
        }
        
        if (tasks.isEmpty()) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of(), 0, type, null);
        }
        
        // 🚀 Step 2: Batch load all relationships in 2 queries only
        List<Integer> taskIds = tasks.stream().map(Task::getId).toList();
        
        // Batch load assignments (1 query for all tasks)
        List<Assignment> allAssignments = assignmentRepository.findByTaskIdInOptimized(taskIds);
        Map<Integer, List<Assignment>> assignmentsByTaskId = allAssignments.stream()
            .collect(Collectors.groupingBy(a -> a.getTask().getId()));
        
        // Batch load attachments (1 query for all tasks)  
        List<Attachment> allAttachments = attachmentRepository.findByTaskIdInAndIsDeletedFalse(taskIds);
        Map<Integer, List<Attachment>> attachmentsByTaskId = allAttachments.stream()
            .collect(Collectors.groupingBy(a -> a.getTask().getId()));
        
        // 🚀 Step 3: Batch load all users (recipient users + created by users)
        Set<Integer> allRecipientUserIds = allAssignments.stream()
            .filter(a -> "user".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null)
            .map(Assignment::getRecipientId)
            .collect(Collectors.toSet());
        
        // ✅ FIX: Also batch load all createdBy users 
        Set<Integer> allCreatedByUserIds = tasks.stream()
            .filter(t -> t.getCreatedBy() != null)
            .map(t -> t.getCreatedBy().getId())
            .collect(Collectors.toSet());
        
        // Combine all user IDs for single query
        Set<Integer> allUserIds = new HashSet<>();
        allUserIds.addAll(allRecipientUserIds);
        allUserIds.addAll(allCreatedByUserIds);
        
        Map<Integer, User> usersById = allUserIds.isEmpty() ? 
            Map.of() : 
            userRepository.findAllById(allUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        // 🚀 Step 4: Convert to DTOs using pre-loaded data (no additional queries)
        List<TaskDetailDTO> taskDTOs = tasks.stream()
            .map(task -> convertToTaskDetailDTOUltraFast(task, 
                assignmentsByTaskId.getOrDefault(task.getId(), List.of()),
                attachmentsByTaskId.getOrDefault(task.getId(), List.of()),
                usersById))
            .collect(Collectors.toList());
        
        // Calculate counts - only count root tasks
        long totalCount = taskDTOs.stream()
            .filter(task -> task.getParentId() == null)
            .count();
        
        return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
            taskDTOs, (int) totalCount, type, null);
    }
    
    /**
     * 🚀 ULTRA FAST: Convert task to DTO using pre-loaded relationships - Zero additional queries
     */
    private TaskDetailDTO convertToTaskDetailDTOUltraFast(Task task, 
                                                         List<Assignment> assignments,
                                                         List<Attachment> attachments,
                                                         Map<Integer, User> usersById) {
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
            .map(a -> convertToAssignmentDTOUltraFast(a, usersById))
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
    private AssignmentDTO convertToAssignmentDTOUltraFast(Assignment a, Map<Integer, User> usersById) {
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
        
        // Use pre-loaded user data - zero additional queries
        if ("user".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null) {
            User recipientUser = usersById.get(a.getRecipientId());
            if (recipientUser != null) {
                adto.setRecipientUser(new UserDTO(recipientUser));
            }
        }
        // Note: For team/unit recipients, we could batch load team leads/unit leads too if needed
        
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
    
    // ✅ ULTRA OPTIMIZED: Convert native query results to Task entities (minimal loading)
    private List<Task> convertNativeResultsToTasks(List<Object[]> nativeResults) {
        return nativeResults.stream().map(result -> {
            Task task = new Task();
            task.setId(((Number) result[0]).intValue());
            task.setTitle((String) result[1]);
            task.setContent((String) result[2]);
            task.setStatus(com.project.quanlycanghangkhong.model.TaskStatus.valueOf((String) result[3]));
            task.setPriority(com.project.quanlycanghangkhong.model.TaskPriority.valueOf((String) result[4]));
            
            // Fix: Convert Timestamp to LocalDateTime properly
            if (result[5] instanceof java.sql.Timestamp) {
                task.setCreatedAt(((java.sql.Timestamp) result[5]).toLocalDateTime());
            }
            if (result[6] instanceof java.sql.Timestamp) {
                task.setUpdatedAt(((java.sql.Timestamp) result[6]).toLocalDateTime());
            }
            
            // Minimal user loading - chỉ set ID để tránh N+1
            if (result[7] != null) {
                User createdBy = new User();
                createdBy.setId(((Number) result[7]).intValue());
                task.setCreatedBy(createdBy);
            }
            
            // Minimal parent loading
            if (result[8] != null) {
                Task parent = new Task();
                parent.setId(((Number) result[8]).intValue());
                task.setParent(parent);
            }
            
            // ✅ FIX: Set instructions and notes from native query results
            task.setInstructions((String) result[9]);
            task.setNotes((String) result[10]);
            
            // Assignments và attachments sẽ được load riêng trong batch
            task.setAssignments(new ArrayList<>());
            
            return task;
        }).collect(Collectors.toList());
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
                List.of(), 0, originalData != null ? originalData.getType() : "unknown", null, 
                new PaginationInfo(page, size, 0));
        }
        
        List<TaskDetailDTO> allTasks = originalData.getTasks();
        int totalCount = originalData.getTotalCount();
        
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
            paginatedTasks, totalCount, originalData.getType(), originalData.getMetadata(), paginationInfo);
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
        
        // Batch load all recipient users
        Set<Integer> allRecipientUserIds = allAssignments.stream()
            .filter(a -> "user".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null)
            .map(Assignment::getRecipientId)
            .collect(Collectors.toSet());
        
        Map<Integer, User> usersById = allRecipientUserIds.isEmpty() ? 
            Map.of() : 
            userRepository.findAllById(allRecipientUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        // Convert all tasks using pre-loaded data
        return tasks.stream()
            .map(task -> convertToTaskDetailDTOUltraFast(task, 
                assignmentsByTaskId.getOrDefault(task.getId(), List.of()),
                attachmentsByTaskId.getOrDefault(task.getId(), List.of()),
                usersById))
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
    
    /**
     * 🚀 FILTER: Apply filters to assigned tasks
     */
    private List<Task> filterAssignedTasks(List<Task> tasks, String filter) {
        if (filter == null || filter.isEmpty()) {
            return tasks;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        return switch (filter.toLowerCase()) {
            case "completed" -> tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.toList());
                
            case "pending" -> tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS || task.getStatus() == TaskStatus.OPEN)
                .collect(Collectors.toList());
                
            case "urgent" -> tasks.stream()
                .filter(task -> task.getPriority() == com.project.quanlycanghangkhong.model.TaskPriority.URGENT)
                .collect(Collectors.toList());
                
            case "overdue" -> tasks.stream()
                .filter(task -> {
                    // Check if task has overdue assignments
                    List<Assignment> assignments = assignmentRepository.findByTaskId(task.getId());
                    return assignments.stream().anyMatch(a -> 
                        a.getDueAt() != null && 
                        a.getDueAt().isBefore(now) && 
                        a.getStatus() != AssignmentStatus.DONE
                    );
                })
                .collect(Collectors.toList());
                
            default -> tasks;
        };
    }

    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithAdvancedSearch(
            String type, String filter, String keyword, String startTime, String endTime,
            java.util.List<String> priorities, java.util.List<String> recipientTypes, java.util.List<Integer> recipientIds) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User currentUser = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        
        if (currentUser == null) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of(), 0, type, null);
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
            return handleAssignedTasksAdvancedSearch(userId, filter, keyword, startDateTime, endDateTime, 
                priorityEnums, recipientTypes, recipientIds);
        } else if ("created".equals(type)) {
            return handleCreatedTasksAdvancedSearch(userId, keyword, startDateTime, endDateTime, priorityEnums);
        } else if ("received".equals(type)) {
            return handleReceivedTasksAdvancedSearch(currentUser, keyword, startDateTime, endDateTime, priorityEnums);
        } else {
            // Fallback to standard method
            return getMyTasksWithCountStandardized(type, filter);
        }
    }
    
    @Override
    public com.project.quanlycanghangkhong.dto.response.task.MyTasksData getMyTasksWithAdvancedSearchAndPagination(
            String type, String filter, String keyword, String startTime, String endTime,
            java.util.List<String> priorities, java.util.List<String> recipientTypes, java.util.List<Integer> recipientIds,
            Integer page, Integer size) {
        
        // Get advanced search results first
        com.project.quanlycanghangkhong.dto.response.task.MyTasksData searchResult = 
            getMyTasksWithAdvancedSearch(type, filter, keyword, startTime, endTime, 
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
                List.of(), 0, searchRequest.getType() != null ? searchRequest.getType() : "assigned", null);
        }
        
        // Get type from request (default to "assigned" for backward compatibility)
        String type = searchRequest.getType() != null ? searchRequest.getType().toLowerCase() : "assigned";
        
        // Validate type
        if (!type.matches("created|assigned|received")) {
            return new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of(), 0, type, null);
        }
        
        // Handle different types with pagination
        return switch (type) {
            case "created" -> handleCreatedTasksAdvancedSearchWithPagination(currentUser, searchRequest);
            case "assigned" -> handleAssignedTasksAdvancedSearchWithPagination(currentUser, searchRequest);
            case "received" -> handleReceivedTasksAdvancedSearchWithPagination(currentUser, searchRequest);
            default -> new com.project.quanlycanghangkhong.dto.response.task.MyTasksData(
                List.of(), 0, type, null);
        };
    }
    
    // Helper methods for advanced search by type
    private com.project.quanlycanghangkhong.dto.response.task.MyTasksData handleAssignedTasksAdvancedSearch(
            Integer userId, String filter, String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime,
            List<com.project.quanlycanghangkhong.model.TaskPriority> priorityEnums,
            List<String> recipientTypes, List<Integer> recipientIds) {
        
        // Get all assigned tasks
        List<Object[]> assignedResults = taskRepository.findAssignedTasksUltraFast(userId);
        List<Task> tasks = convertNativeResultsToTasks(assignedResults);
        
        // Apply filters
        if (filter != null) {
            tasks = filterAssignedTasks(tasks, filter);
        }
        
        // Apply keyword search
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim().toLowerCase();
            tasks = tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(searchKeyword) ||
                               (task.getContent() != null && task.getContent().toLowerCase().contains(searchKeyword)))
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
            taskDTOs, taskDTOs.size(), "assigned", null);
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
                .filter(task -> task.getTitle().toLowerCase().contains(searchKeyword) ||
                               (task.getContent() != null && task.getContent().toLowerCase().contains(searchKeyword)))
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
            taskDTOs, taskDTOs.size(), "created", null);
    }
    
    private com.project.quanlycanghangkhong.dto.response.task.MyTasksData handleReceivedTasksAdvancedSearch(
            User currentUser, String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime,
            List<com.project.quanlycanghangkhong.model.TaskPriority> priorityEnums) {
        
        Integer userId = currentUser.getId();
        Integer teamId = currentUser.getTeam() != null ? currentUser.getTeam().getId() : null;
        Integer unitId = currentUser.getUnit() != null ? currentUser.getUnit().getId() : null;
        
        // Get received tasks
        List<Object[]> receivedResults = taskRepository.findReceivedTasksUltraFast(userId, teamId, unitId);
        List<Task> tasks = convertNativeResultsToTasks(receivedResults);
        
        // Apply keyword search
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim().toLowerCase();
            tasks = tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(searchKeyword) ||
                               (task.getContent() != null && task.getContent().toLowerCase().contains(searchKeyword)))
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
            taskDTOs, taskDTOs.size(), "received", null);
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
            searchRequest.getKeyword(), null, null, null);
    }
}
