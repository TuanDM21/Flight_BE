package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import com.project.quanlycanghangkhong.model.Assignment;
import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.repository.AssignmentRepository;
import com.project.quanlycanghangkhong.repository.TaskRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.AssignmentService;
import com.project.quanlycanghangkhong.dto.request.UpdateAssignmentRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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

    private AssignmentDTO toDTO(Assignment a) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setAssignmentId(a.getAssignmentId());
        // Set taskId from Assignment entity
        dto.setTaskId(a.getTask() != null ? a.getTask().getId() : null);
        // Không set recipientId, assignedBy, completedBy vào DTO nữa
        dto.setRecipientType(a.getRecipientType());
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
        }
        return dto;
    }

    private void updateEntityFromDTO(Assignment a, AssignmentDTO dto) {
        // Không set recipientId, assignedBy, completedBy từ DTO nữa
        a.setRecipientType(dto.getRecipientType());
        a.setAssignedAt(dto.getAssignedAt() != null ? new Timestamp(dto.getAssignedAt().getTime()).toLocalDateTime() : a.getAssignedAt());
        a.setDueAt(dto.getDueAt() != null ? new Timestamp(dto.getDueAt().getTime()).toLocalDateTime() : null);
        a.setCompletedAt(dto.getCompletedAt() != null ? new Timestamp(dto.getCompletedAt().getTime()).toLocalDateTime() : null);
        a.setStatus(dto.getStatus());
        a.setNote(dto.getNote());
    }

    @Override
    public AssignmentDTO createAssignment(AssignmentDTO dto) {
        Assignment a = new Assignment();
        if (dto.getTaskId() != null) {
            Optional<Task> taskOpt = taskRepository.findById(dto.getTaskId());
            taskOpt.ifPresent(a::setTask);
        }
        updateEntityFromDTO(a, dto);
        a.setAssignedAt(a.getAssignedAt() == null ? java.time.LocalDateTime.now() : a.getAssignedAt());
        // Xử lý tự động set recipientId là userId của leader nếu recipientType là team hoặc unit
        if ("team".equalsIgnoreCase(dto.getRecipientType()) && dto.getRecipientId() != null) {
            userRepository.findTeamLeadByTeamId(dto.getRecipientId())
                .ifPresent(leader -> a.setRecipientId(leader.getId()));
        } else if ("unit".equalsIgnoreCase(dto.getRecipientType()) && dto.getRecipientId() != null) {
            userRepository.findUnitLeadByUnitId(dto.getRecipientId())
                .ifPresent(leader -> a.setRecipientId(leader.getId()));
        } else if ("user".equalsIgnoreCase(dto.getRecipientType()) && dto.getRecipientId() != null) {
            a.setRecipientId(dto.getRecipientId());
        }
        return toDTO(assignmentRepository.save(a));
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
        // Xử lý tự động set recipientId là userId của leader nếu recipientType là team hoặc unit
        if ("team".equalsIgnoreCase(request.getRecipientType()) && request.getRecipientId() != null) {
            userRepository.findTeamLeadByTeamId(request.getRecipientId())
                .ifPresent(leader -> assignment.setRecipientId(leader.getId()));
            recipientChanged = true;
        } else if ("unit".equalsIgnoreCase(request.getRecipientType()) && request.getRecipientId() != null) {
            userRepository.findUnitLeadByUnitId(request.getRecipientId())
                .ifPresent(leader -> assignment.setRecipientId(leader.getId()));
            recipientChanged = true;
        } else if ("user".equalsIgnoreCase(request.getRecipientType()) && request.getRecipientUser() != null && request.getRecipientUser().getId() != null) {
            assignment.setRecipientId(request.getRecipientUser().getId());
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
            // Nếu chuyển sang completed thì set completedAt, completedBy
            if (request.getStatus() == 2) { // 2 = Completed
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
            assignment.setStatus(0); // Pending
        }
        assignmentRepository.save(assignment);
        return toDTO(assignment);
    }

    @Override
    public void deleteAssignment(Integer assignmentId) {
        assignmentRepository.deleteById(assignmentId);
    }

    @Override
    public AssignmentDTO getAssignmentById(Integer assignmentId) {
        return assignmentRepository.findById(assignmentId).map(this::toDTO).orElse(null);
    }

    @Override
    public List<AssignmentDTO> getAssignmentsByTaskId(Integer taskId) {
        return assignmentRepository.findByTask_Id(taskId).stream().map(this::toDTO).collect(Collectors.toList());
    }
}
