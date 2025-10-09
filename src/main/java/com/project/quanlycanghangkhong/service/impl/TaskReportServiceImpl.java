package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.request.TaskReportRequest;
import com.project.quanlycanghangkhong.dto.response.TaskReportResponse;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.model.*;
import com.project.quanlycanghangkhong.repository.TaskRepository;
import com.project.quanlycanghangkhong.repository.AssignmentRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.repository.TeamRepository;
import com.project.quanlycanghangkhong.service.TaskReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskReportServiceImpl implements TaskReportService {

    private final TaskRepository taskRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ApiResponseCustom<TaskReportResponse> generateReport(TaskReportRequest request) {
        try {
            log.info("Generating task report with request: {}", request);

            // Build query với criteria
            List<Task> tasks = buildTaskQuery(request);
            
            // Tạo response
            TaskReportResponse response = buildTaskReportResponse(tasks, request);
            
            return ApiResponseCustom.success("Tạo báo cáo thành công", response);
            
        } catch (Exception e) {
            log.error("Error generating task report", e);
            return ApiResponseCustom.error("Lỗi khi tạo báo cáo: " + e.getMessage());
        }
    }

    private List<Task> buildTaskQuery(TaskReportRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = cb.createQuery(Task.class);
        Root<Task> taskRoot = query.from(Task.class);

        List<Predicate> predicates = new ArrayList<>();

        // Filter by deleted = false
        predicates.add(cb.equal(taskRoot.get("deleted"), false));

        // Date range filter
        if (request.getStartDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(taskRoot.get("createdAt"), request.getStartDate()));
        }
        if (request.getEndDate() != null) {
            predicates.add(cb.lessThanOrEqualTo(taskRoot.get("createdAt"), request.getEndDate()));
        }

        // Task IDs filter
        if (request.getTaskIds() != null && !request.getTaskIds().isEmpty()) {
            predicates.add(taskRoot.get("id").in(request.getTaskIds()));
        }

        // Task status filter
        if (request.getTaskStatuses() != null && !request.getTaskStatuses().isEmpty()) {
            List<TaskStatus> statuses = request.getTaskStatuses().stream()
                .map(TaskStatus::valueOf)
                .collect(Collectors.toList());
            predicates.add(taskRoot.get("status").in(statuses));
        }

        // Priority filter
        if (request.getPriorities() != null && !request.getPriorities().isEmpty()) {
            List<TaskPriority> priorities = request.getPriorities().stream()
                .map(TaskPriority::valueOf)
                .collect(Collectors.toList());
            predicates.add(taskRoot.get("priority").in(priorities));
        }

        // Task type filter
        if (request.getTaskTypeIds() != null && !request.getTaskTypeIds().isEmpty()) {
            predicates.add(taskRoot.get("taskType").get("id").in(request.getTaskTypeIds()));
        }

        // User filter (через assignments)
        if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
            Subquery<Integer> assignmentSubquery = query.subquery(Integer.class);
            Root<Assignment> assignmentRoot = assignmentSubquery.from(Assignment.class);
            assignmentSubquery.select(assignmentRoot.get("task").get("id"))
                .where(cb.and(
                    cb.equal(assignmentRoot.get("recipientType"), "USER"),
                    assignmentRoot.get("recipientId").in(request.getUserIds())
                ));
            predicates.add(taskRoot.get("id").in(assignmentSubquery));
        }

        // Team filter (через assignments)
        if (request.getTeamIds() != null && !request.getTeamIds().isEmpty()) {
            Subquery<Integer> assignmentSubquery = query.subquery(Integer.class);
            Root<Assignment> assignmentRoot = assignmentSubquery.from(Assignment.class);
            assignmentSubquery.select(assignmentRoot.get("task").get("id"))
                .where(cb.and(
                    cb.equal(assignmentRoot.get("recipientType"), "TEAM"),
                    assignmentRoot.get("recipientId").in(request.getTeamIds())
                ));
            predicates.add(taskRoot.get("id").in(assignmentSubquery));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(taskRoot.get("createdAt")));

        return entityManager.createQuery(query).getResultList();
    }

    private TaskReportResponse buildTaskReportResponse(List<Task> tasks, TaskReportRequest request) {
        // Build task items
        List<TaskReportResponse.TaskReportItem> taskItems = new ArrayList<>();

        for (Task task : tasks) {
            List<TaskReportResponse.AssignmentDetail> assignmentDetails = new ArrayList<>();
            
            // Luôn luôn lấy assignment details
            List<Assignment> assignments = assignmentRepository.findByTaskId(task.getId());
            
            for (Assignment assignment : assignments) {
                String recipientName = getRecipientName(assignment.getRecipientType(), assignment.getRecipientId());
                
                TaskReportResponse.AssignmentDetail detail = TaskReportResponse.AssignmentDetail.builder()
                    .assignmentId(assignment.getAssignmentId())
                    .recipientType(assignment.getRecipientType())
                    .recipientId(assignment.getRecipientId())
                    .recipientName(recipientName)
                    .assignedBy(assignment.getAssignedBy().getName())
                    .assignedAt(assignment.getAssignedAt())
                    .dueAt(assignment.getDueAt())
                    .completedAt(assignment.getCompletedAt())
                    .completedBy(assignment.getCompletedBy() != null ? assignment.getCompletedBy().getName() : null)
                    .status(assignment.getStatus().name())
                    .note(assignment.getNote())
                    .build();
                
                assignmentDetails.add(detail);
            }

            TaskReportResponse.TaskReportItem taskItem = TaskReportResponse.TaskReportItem.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .content(task.getContent())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .taskType(task.getTaskType() != null ? task.getTaskType().getName() : null)
                .createdBy(task.getCreatedBy().getName())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .assignments(assignmentDetails)
                .build();

            taskItems.add(taskItem);
        }

        return TaskReportResponse.builder()
            .tasks(taskItems)
            .build();
    }

    private String getRecipientName(String recipientType, Integer recipientId) {
        if ("USER".equals(recipientType)) {
            return userRepository.findById(recipientId)
                .map(User::getName)
                .orElse("Unknown User");
        } else if ("TEAM".equals(recipientType)) {
            return teamRepository.findById(recipientId)
                .map(Team::getTeamName)
                .orElse("Unknown Team");
        }
        return "Unknown";
    }
}
