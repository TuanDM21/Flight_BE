package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import com.project.quanlycanghangkhong.model.Assignment;
import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.repository.AssignmentRepository;
import com.project.quanlycanghangkhong.repository.TaskRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.AssignmentService;
import com.project.quanlycanghangkhong.dto.request.UpdateAssignmentRequest;
import com.project.quanlycanghangkhong.service.TaskService;
import com.project.quanlycanghangkhong.dto.CreateAssignmentRequest;
import com.project.quanlycanghangkhong.dto.request.CreateAssignmentsRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.project.quanlycanghangkhong.model.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.ZoneId;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskService taskService;

    private AssignmentDTO toDTO(Assignment a) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setAssignmentId(a.getAssignmentId());
        // Set taskId from Assignment entity
        dto.setTaskId(a.getTask() != null ? a.getTask().getId() : null);
        // Không set recipientId, assignedBy, completedBy vào DTO nữa
        dto.setRecipientType(a.getRecipientType());
        dto.setRecipientId(a.getRecipientId()); // Đảm bảo luôn set recipientId cho DTO
        System.out.println("[DEBUG] toDTO: assignmentId=" + a.getAssignmentId() + ", recipientType=" + a.getRecipientType() + ", recipientId=" + a.getRecipientId());
        dto.setAssignedAt(a.getAssignedAt() != null ? Timestamp.valueOf(a.getAssignedAt()) : null);
        dto.setDueAt(a.getDueAt() != null ? Timestamp.valueOf(a.getDueAt()) : null);
        dto.setCompletedAt(a.getCompletedAt() != null ? Timestamp.valueOf(a.getCompletedAt()) : null);
        dto.setStatus(a.getStatus());
        dto.setNote(a.getNote());
        // Set user info dạng object
        if (a.getAssignedBy() != null) dto.setAssignedByUser(new com.project.quanlycanghangkhong.dto.UserDTO(a.getAssignedBy()));
        if (a.getCompletedBy() != null) dto.setCompletedByUser(new com.project.quanlycanghangkhong.dto.UserDTO(a.getCompletedBy()));
        if ("user".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null) {
            userRepository.findById(a.getRecipientId()).ifPresent(u -> dto.setRecipientUser(new com.project.quanlycanghangkhong.dto.UserDTO(u)));
        } else if ("team".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null) {
            userRepository.findTeamLeadByTeamId(a.getRecipientId()).ifPresent(u -> dto.setRecipientUser(new com.project.quanlycanghangkhong.dto.UserDTO(u)));
        } else if ("unit".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null) {
            userRepository.findUnitLeadByUnitId(a.getRecipientId()).ifPresent(u -> dto.setRecipientUser(new com.project.quanlycanghangkhong.dto.UserDTO(u)));
        }
        return dto;
    }

    // Xoá hàm updateEntityFromDTO vì không dùng đến

    private Assignment toEntity(CreateAssignmentRequest request) {
        Assignment a = new Assignment();
        a.setRecipientType(request.getRecipientType());
        a.setDueAt(request.getDueAt() != null ? new java.sql.Timestamp(request.getDueAt().getTime()).toLocalDateTime() : null);
        a.setNote(request.getNote());
        if (request.getRecipientId() != null) {
            a.setRecipientId(request.getRecipientId());
        }
        a.setAssignedAt(java.time.LocalDateTime.now());
        // Lấy email hiện tại và set assignedBy
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User creator = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        if (creator != null) {
            a.setAssignedBy(creator);
        }
        return a;
    }

    private Assignment toEntity(CreateAssignmentRequest request, Integer taskId) {
        Assignment a = new Assignment();
        if (taskId != null) {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            taskOpt.ifPresent(a::setTask);
        }
        a.setRecipientType(request.getRecipientType());
        a.setDueAt(request.getDueAt() != null ? new java.sql.Timestamp(request.getDueAt().getTime()).toLocalDateTime() : null);
        a.setNote(request.getNote());
        if (request.getRecipientId() != null) {
            a.setRecipientId(request.getRecipientId());
        }
        a.setAssignedAt(java.time.LocalDateTime.now());
        // Lấy email hiện tại và set assignedBy
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication != null ? authentication.getName() : null;
        User creator = (email != null) ? userRepository.findByEmail(email).orElse(null) : null;
        if (creator != null) {
            a.setAssignedBy(creator);
        }
        return a;
    }

    @Override
    public AssignmentDTO createAssignment(CreateAssignmentRequest request) {
        Assignment a = toEntity(request);
        Assignment saved = assignmentRepository.save(a);
        if (saved.getTask() != null) {
            taskService.updateTaskStatus(saved.getTask());
        }
        return toDTO(saved);
    }

    @Override
    public AssignmentDTO updateAssignment(Integer assignmentId, UpdateAssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
        if (assignment == null) return null;

        boolean recipientChanged = false;
        if (request.getRecipientType() != null && !request.getRecipientType().equals(assignment.getRecipientType())) {
            assignment.setRecipientType(request.getRecipientType());
            recipientChanged = true;
        }
        // Xử lý recipientId
        if (request.getRecipientId() != null) {
            assignment.setRecipientId(request.getRecipientId());
            recipientChanged = true;
        }
        if (request.getDueAt() != null) {
            assignment.setDueAt(request.getDueAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (request.getNote() != null) {
            assignment.setNote(request.getNote());
        }
        if (request.getStatus() != null) {
            assignment.setStatus(request.getStatus());
            // Nếu chuyển sang DONE thì set completedAt, completedBy
            if (request.getStatus() == com.project.quanlycanghangkhong.model.AssignmentStatus.DONE) {
                assignment.setCompletedAt(java.time.LocalDateTime.now());
                // assignment.setCompletedBy(currentUser); // Lấy user hiện tại nếu cần
            } else {
                assignment.setCompletedAt(null);
                assignment.setCompletedBy(null);
            }
        }
        // Nếu thay đổi recipient thì reset completedAt, completedBy, status
        if (recipientChanged) {
            assignment.setCompletedAt(null);
            assignment.setCompletedBy(null);
            assignment.setStatus(com.project.quanlycanghangkhong.model.AssignmentStatus.WORKING);
        }
        Assignment saved = assignmentRepository.save(assignment);
        if (saved.getTask() != null) {
            taskService.updateTaskStatus(saved.getTask());
        }
        return toDTO(saved);
    }

    @Override
    public void deleteAssignment(Integer assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
        if (assignment != null) {
            Task task = assignment.getTask();
            assignmentRepository.deleteById(assignmentId);
            if (task != null) {
                taskService.updateTaskStatus(task);
            }
        }
    }

    @Override
    public AssignmentDTO getAssignmentById(Integer assignmentId) {
        return assignmentRepository.findById(assignmentId).map(this::toDTO).orElse(null);
    }

    @Override
    public List<AssignmentDTO> getAssignmentsByTaskId(Integer taskId) {
        return assignmentRepository.findByTask_Id(taskId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public void addAssignmentComment(Integer assignmentId, String comment) {
        Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
        if (assignment == null) return;
        // Lấy userId từ user đang login hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            userId = ((User) authentication.getPrincipal()).getId().longValue();
        }
        if (userId == null) throw new RuntimeException("Không xác định được user đang đăng nhập");
    }

    @Override
    public List<AssignmentDTO> createAssignments(CreateAssignmentsRequest request) {
        List<CreateAssignmentRequest> reqs = request.getAssignments();
        Integer taskId = request.getTaskId();
        return reqs.stream().map(req -> {
            Assignment a = toEntity(req, taskId);
            Assignment saved = assignmentRepository.save(a);
            if (saved.getTask() != null) {
                taskService.updateTaskStatus(saved.getTask());
            }
            return toDTO(saved);
        }).collect(Collectors.toList());
    }
}
