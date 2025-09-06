package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import com.project.quanlycanghangkhong.model.Assignment;
import com.project.quanlycanghangkhong.model.Task;
import com.project.quanlycanghangkhong.repository.AssignmentRepository;
import com.project.quanlycanghangkhong.repository.TaskRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.repository.TeamRepository;
import com.project.quanlycanghangkhong.repository.UnitRepository;
import com.project.quanlycanghangkhong.service.AssignmentService;
import com.project.quanlycanghangkhong.request.UpdateAssignmentRequest;
import com.project.quanlycanghangkhong.service.TaskService;
import com.project.quanlycanghangkhong.request.CreateAssignmentRequest;
import com.project.quanlycanghangkhong.request.CreateAssignmentsRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.project.quanlycanghangkhong.model.User;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
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
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private TaskService taskService;

    private AssignmentDTO toDTO(Assignment a) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setAssignmentId(a.getAssignmentId());
        // Set taskId from Assignment entity
        dto.setTaskId(a.getTask() != null ? a.getTask().getId() : null);
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
        
        /* 📋 RECIPIENT MAPPING LOGIC:
         * ┌──────────────┬─────────────────┬──────────────────┬──────────────────────┬──────────────────────┐
         * │ recipientType│ recipientUser   │ recipientId      │ recipientTeamName    │ recipientUnitName    │
         * ├──────────────┼─────────────────┼──────────────────┼──────────────────────┼──────────────────────┤
         * │ USER         │ ✅ UserDTO       │ ✅ userId        │ null                 │ null                 │
         * │ TEAM         │ null            │ ✅ teamId        │ ✅ team name         │ null                 │
         * │ UNIT         │ null            │ ✅ unitId        │ null                 │ ✅ unit name         │
         * └──────────────┴─────────────────┴──────────────────┴──────────────────────┴──────────────────────┘
         * 🚨 IMPORTANT: recipientTeamLead và recipientUnitLead sẽ LUÔN là null
         */
        
        // Xử lý recipient theo type
        if ("USER".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null) {
            // ✅ Trường hợp giao trực tiếp cho user cụ thể
            userRepository.findById(a.getRecipientId()).ifPresent(u -> 
                dto.setRecipientUser(new com.project.quanlycanghangkhong.dto.UserDTO(u)));
                
        } else if ("TEAM".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null) {
            // ✅ Trường hợp giao cho team - chỉ set thông tin team
            teamRepository.findById(a.getRecipientId()).ifPresent(team -> {
                dto.setRecipientTeamName(team.getTeamName());
                // recipientUser = null (chính xác vì chưa assign cho user cụ thể)
                // recipientTeamLead = null (không set vì không phải người nhận việc thực tế)
            });
            
        } else if ("UNIT".equalsIgnoreCase(a.getRecipientType()) && a.getRecipientId() != null) {
            // ✅ Trường hợp giao cho unit - chỉ set thông tin unit
            unitRepository.findById(a.getRecipientId()).ifPresent(unit -> {
                dto.setRecipientUnitName(unit.getUnitName());
                // recipientUser = null (chính xác vì chưa assign cho user cụ thể)
                // recipientUnitLead = null (không set vì không phải người nhận việc thực tế)
            });
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

        // Cập nhật theo đúng data được gửi lên, không có logic tự động nào khác
        if (request.getRecipientType() != null) {
            assignment.setRecipientType(request.getRecipientType());
        }
        if (request.getRecipientId() != null) {
            assignment.setRecipientId(request.getRecipientId());
        }
        if (request.getDueAt() != null) {
            assignment.setDueAt(request.getDueAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (request.getNote() != null) {
            assignment.setNote(request.getNote());
        }
        if (request.getStatus() != null) {
            assignment.setStatus(request.getStatus());
            // Chỉ tự động set completedAt khi status = DONE
            if (request.getStatus() == com.project.quanlycanghangkhong.model.AssignmentStatus.DONE) {
                assignment.setCompletedAt(java.time.LocalDateTime.now());
                // assignment.setCompletedBy(currentUser); // Lấy user hiện tại nếu cần
            } else if (request.getStatus() != com.project.quanlycanghangkhong.model.AssignmentStatus.DONE) {
                // Clear completedAt nếu status không phải DONE
                assignment.setCompletedAt(null);
                assignment.setCompletedBy(null);
            }
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
        
        List<AssignmentDTO> results = new ArrayList<>();
        
        for (CreateAssignmentRequest req : reqs) {
            Assignment a = toEntity(req, taskId);
            Assignment saved = assignmentRepository.save(a);
            results.add(toDTO(saved));
            
            if (saved.getTask() != null) {
                taskService.updateTaskStatus(saved.getTask());
            }
        }
        
        return results;
    }
}
