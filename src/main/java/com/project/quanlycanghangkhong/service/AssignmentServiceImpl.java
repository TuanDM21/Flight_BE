package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import com.project.quanlycanghangkhong.model.Assignment;
import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.AssignmentRepository;
import com.project.quanlycanghangkhong.repository.TaskRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        dto.setRecipientId(a.getRecipientId());
        dto.setRecipientType(a.getRecipientType());
        dto.setAssignedBy(a.getAssignedBy() != null ? a.getAssignedBy().getId() : null);
        dto.setAssignedAt(a.getAssignedAt() != null ? Timestamp.valueOf(a.getAssignedAt()) : null);
        dto.setDueAt(a.getDueAt() != null ? Timestamp.valueOf(a.getDueAt()) : null);
        dto.setCompletedAt(a.getCompletedAt() != null ? Timestamp.valueOf(a.getCompletedAt()) : null);
        dto.setCompletedBy(a.getCompletedBy() != null ? a.getCompletedBy().getId() : null);
        dto.setStatus(a.getStatus());
        dto.setNote(a.getNote());
        return dto;
    }

    private void updateEntityFromDTO(Assignment a, AssignmentDTO dto) {
        a.setRecipientId(dto.getRecipientId());
        a.setRecipientType(dto.getRecipientType());
        if (dto.getAssignedBy() != null) {
            userRepository.findById(dto.getAssignedBy()).ifPresent(a::setAssignedBy);
        }
        a.setAssignedAt(dto.getAssignedAt() != null ? new Timestamp(dto.getAssignedAt().getTime()).toLocalDateTime() : a.getAssignedAt());
        a.setDueAt(dto.getDueAt() != null ? new Timestamp(dto.getDueAt().getTime()).toLocalDateTime() : null);
        a.setCompletedAt(dto.getCompletedAt() != null ? new Timestamp(dto.getCompletedAt().getTime()).toLocalDateTime() : null);
        if (dto.getCompletedBy() != null) {
            userRepository.findById(dto.getCompletedBy()).ifPresent(a::setCompletedBy);
        }
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
        return toDTO(assignmentRepository.save(a));
    }

    @Override
    public AssignmentDTO updateAssignment(Integer assignmentId, AssignmentDTO dto) {
        Optional<Assignment> opt = assignmentRepository.findById(assignmentId);
        if (opt.isEmpty()) return null;
        Assignment a = opt.get();
        updateEntityFromDTO(a, dto);
        return toDTO(assignmentRepository.save(a));
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
