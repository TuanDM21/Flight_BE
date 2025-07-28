package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.CreateTaskRequest;
import com.project.quanlycanghangkhong.dto.CreateSubtaskRequest;
import com.project.quanlycanghangkhong.dto.TaskDTO;
import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import com.project.quanlycanghangkhong.dto.UpdateTaskDTO;
import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.dto.request.BulkDeleteTasksRequest;
import com.project.quanlycanghangkhong.dto.response.task.ApiAllTasksResponse;
import com.project.quanlycanghangkhong.dto.response.task.ApiTaskResponse;
import com.project.quanlycanghangkhong.dto.response.task.ApiTaskDetailResponse;
import com.project.quanlycanghangkhong.dto.response.task.ApiBulkDeleteTasksResponse;
import com.project.quanlycanghangkhong.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/test")
    @Operation(summary = "Test request body mapping", description = "Test endpoint để debug JSON mapping")
    public ResponseEntity<ApiTaskResponse> testCreateTask(@RequestBody CreateTaskRequest request) {
        System.out.println("[TEST] Test endpoint called with: " + request);
        return ResponseEntity.ok(new ApiTaskResponse("Test thành công", 200, null, true));
    }

    @PostMapping
    @Operation(summary = "Tạo task", description = "Tạo mới một công việc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công", content = @Content(schema = @Schema(implementation = ApiTaskResponse.class)))
    })
    public ResponseEntity<ApiTaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        System.out.println("[DEBUG] Received CreateTaskRequest: " + request);
        System.out.println("[DEBUG] Request class: " + (request != null ? request.getClass().getName() : "null"));
        if (request == null) {
            System.out.println("[DEBUG] Request is null!");
            return ResponseEntity.status(400).body(new ApiTaskResponse("Request body is null", 400, null, false));
        } else {
            System.out.println("[DEBUG] Request title: " + request.getTitle());
            System.out.println("[DEBUG] Request content: " + request.getContent());
            System.out.println("[DEBUG] Request priority: " + request.getPriority());
            System.out.println("[DEBUG] Request assignments: " + request.getAssignments());
            System.out.println("[DEBUG] Request attachmentIds: " + request.getAttachmentIds());
        }
        TaskDTO created = taskService.createTaskWithAssignmentsAndAttachments(request);
        ApiTaskResponse res = new ApiTaskResponse("Tạo công việc thành công", 201, created, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật task", description = "Cập nhật một công việc theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công", content = @Content(schema = @Schema(implementation = ApiTaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy công việc", content = @Content(schema = @Schema(implementation = ApiTaskResponse.class)))
    })
    public ResponseEntity<ApiTaskResponse> updateTask(@PathVariable Integer id, @RequestBody UpdateTaskDTO updateTaskDTO) {
        TaskDTO updated = taskService.updateTask(id, updateTaskDTO);
        if (updated == null) return ResponseEntity.status(404).body(new ApiTaskResponse("Không tìm thấy công việc", 404, null, false));
        return ResponseEntity.ok(new ApiTaskResponse("Cập nhật thành công", 200, updated, true));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá task", description = "Xoá một công việc theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xoá thành công", content = @Content(schema = @Schema(implementation = ApiTaskResponse.class)))
    })
    public ResponseEntity<ApiTaskResponse> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(new ApiTaskResponse("Xoá thành công", 200, null, true));
    }

    @DeleteMapping("/bulk-delete")
    @Operation(summary = "Xoá nhiều task", description = "Xoá nhiều công việc cùng lúc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xoá thành công", content = @Content(schema = @Schema(implementation = ApiBulkDeleteTasksResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = @Content(schema = @Schema(implementation = ApiBulkDeleteTasksResponse.class)))
    })
    public ResponseEntity<ApiBulkDeleteTasksResponse> bulkDeleteTasks(@Valid @RequestBody BulkDeleteTasksRequest request) {
        try {
            if (request.getTaskIds() == null || request.getTaskIds().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ApiBulkDeleteTasksResponse("Danh sách task ID không được để trống", 400, null, false)
                );
            }

            taskService.bulkDeleteTasks(request.getTaskIds());
            
            String message = "Đã xoá thành công " + request.getTaskIds().size() + " task";
            return ResponseEntity.ok(new ApiBulkDeleteTasksResponse(message, 200, message, true));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                new ApiBulkDeleteTasksResponse("Lỗi khi xoá task: " + e.getMessage(), 500, null, false)
            );
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết task", description = "Lấy chi tiết một công việc theo id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiTaskDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy công việc", content = @Content(schema = @Schema(implementation = ApiTaskDetailResponse.class)))
    })
    public ResponseEntity<ApiTaskDetailResponse> getTaskDetailById(@PathVariable Integer id) {
        TaskDetailDTO task = taskService.getTaskDetailById(id);
        if (task == null) return ResponseEntity.status(404).body(new ApiTaskDetailResponse("Không tìm thấy công việc", 404, null, false));
        return ResponseEntity.ok(new ApiTaskDetailResponse("Thành công", 200, task, true));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách task", description = "Lấy danh sách tất cả công việc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiAllTasksResponse.class)))
    })
    public ResponseEntity<ApiAllTasksResponse> getAllTaskDetails() {
        List<TaskDetailDTO> tasks = taskService.getAllTaskDetails();
        return ResponseEntity.ok(new ApiAllTasksResponse("Thành công", 200, tasks, true));
    }

    @GetMapping("/my")
    @Operation(summary = "Lấy công việc của tôi theo loại", description = "Lấy danh sách công việc theo loại: created (đã tạo nhưng chưa giao việc - flat list), assigned (đã giao việc bao gồm tất cả subtasks với hierarchyLevel), received (được giao việc - flat list)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiAllTasksResponse.class))),
        @ApiResponse(responseCode = "400", description = "Tham số type không hợp lệ", content = @Content(schema = @Schema(implementation = ApiAllTasksResponse.class)))
    })
    public ResponseEntity<ApiAllTasksResponse> getMyTasks(@RequestParam String type) {
        if (!type.matches("created|assigned|received")) {
            return ResponseEntity.badRequest().body(
                new ApiAllTasksResponse("Tham số type phải là: created, assigned, hoặc received", 400, null, false)
            );
        }
        
        List<TaskDetailDTO> tasks = taskService.getMyTasks(type);
        String message = switch (type.toLowerCase()) {
            case "created" -> "Danh sách công việc đã tạo nhưng chưa giao việc (flat list)";
            case "assigned" -> "Danh sách công việc đã giao (bao gồm tất cả subtasks với hierarchyLevel)";
            case "received" -> "Danh sách công việc được giao (flat list)";
            default -> "Thành công";
        };
        
        return ResponseEntity.ok(new ApiAllTasksResponse(message, 200, tasks, true));
    }

    // MÔ HÌNH ADJACENCY LIST: API Subtask
    @PostMapping("/{parentId}/subtasks")
    @Operation(summary = "Tạo subtask", description = "Tạo subtask con cho một task cha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo subtask thành công", content = @Content(schema = @Schema(implementation = ApiTaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy task cha", content = @Content(schema = @Schema(implementation = ApiTaskResponse.class)))
    })
    public ResponseEntity<ApiTaskResponse> createSubtask(@PathVariable Integer parentId, @RequestBody CreateSubtaskRequest request) {
        // parentId được truyền qua path parameter, truyền trực tiếp vào service
        TaskDTO created = taskService.createSubtask(parentId, request);
        ApiTaskResponse res = new ApiTaskResponse("Tạo subtask thành công", 201, created, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{id}/subtasks")
    @Operation(summary = "Lấy danh sách subtask", description = "Lấy tất cả subtask con của một task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiAllTasksResponse.class)))
    })
    public ResponseEntity<ApiAllTasksResponse> getSubtasks(@PathVariable Integer id) {
        List<TaskDetailDTO> subtasks = taskService.getSubtasks(id);
        return ResponseEntity.ok(new ApiAllTasksResponse("Thành công", 200, subtasks, true));
    }

    @GetMapping("/root")
    @Operation(summary = "Lấy danh sách task gốc", description = "Lấy tất cả task không có parent (task gốc)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiAllTasksResponse.class)))
    })
    public ResponseEntity<ApiAllTasksResponse> getRootTasks() {
        List<TaskDetailDTO> rootTasks = taskService.getRootTasks();
        return ResponseEntity.ok(new ApiAllTasksResponse("Thành công", 200, rootTasks, true));
    }

    // === ATTACHMENT MANAGEMENT ===
    // Attachment chỉ được quản lý thông qua createTask và updateTask
    // Đã loại bỏ các API riêng biệt để gán/gỡ attachment vì không cần thiết
    
    @GetMapping("/{id}/attachments")
    @Operation(summary = "Lấy danh sách file đính kèm của task", description = "Lấy tất cả file đính kèm trực tiếp của task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy task")
    })
    public ResponseEntity<?> getTaskAttachments(@PathVariable Integer id) {
        List<AttachmentDTO> attachments = taskService.getTaskAttachments(id);
        return ResponseEntity.ok(Map.of(
            "message", "Thành công",
            "statusCode", 200,
            "data", attachments,
            "success", true
        ));
    }

    // ============== SEARCH & FILTER ENDPOINTS ==============

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm task theo title", description = "Tìm kiếm task theo title (case-insensitive)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiAllTasksResponse.class))),
        @ApiResponse(responseCode = "400", description = "Thiếu từ khóa tìm kiếm", content = @Content(schema = @Schema(implementation = ApiAllTasksResponse.class)))
    })
    public ResponseEntity<ApiAllTasksResponse> searchTasksByTitle(@RequestParam String title) {
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                new ApiAllTasksResponse("Từ khóa tìm kiếm không được để trống", 400, null, false)
            );
        }
        List<TaskDetailDTO> tasks = taskService.searchTasksByTitle(title.trim());
        return ResponseEntity.ok(new ApiAllTasksResponse("Tìm thấy " + tasks.size() + " task", 200, tasks, true));
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Lọc task theo priority", description = "Lấy danh sách task theo mức độ ưu tiên")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiAllTasksResponse.class))),
        @ApiResponse(responseCode = "400", description = "Priority không hợp lệ", content = @Content(schema = @Schema(implementation = ApiAllTasksResponse.class)))
    })
    public ResponseEntity<ApiAllTasksResponse> getTasksByPriority(@PathVariable String priority) {
        try {
            com.project.quanlycanghangkhong.model.TaskPriority taskPriority = 
                com.project.quanlycanghangkhong.model.TaskPriority.valueOf(priority.toUpperCase());
            List<TaskDetailDTO> tasks = taskService.getTasksByPriority(taskPriority);
            return ResponseEntity.ok(new ApiAllTasksResponse("Tìm thấy " + tasks.size() + " task với priority " + priority, 200, tasks, true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                new ApiAllTasksResponse("Priority phải là: LOW, NORMAL, HIGH, hoặc URGENT", 400, null, false)
            );
        }
    }

    @GetMapping("/search/all")
    @Operation(summary = "Tìm kiếm task theo title hoặc content", description = "Tìm kiếm task trong title hoặc content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiAllTasksResponse.class))),
        @ApiResponse(responseCode = "400", description = "Thiếu từ khóa tìm kiếm", content = @Content(schema = @Schema(implementation = ApiAllTasksResponse.class)))
    })
    public ResponseEntity<ApiAllTasksResponse> searchAllTasks(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                new ApiAllTasksResponse("Từ khóa tìm kiếm không được để trống", 400, null, false)
            );
        }
        List<TaskDetailDTO> tasks = taskService.searchTasks(keyword.trim());
        return ResponseEntity.ok(new ApiAllTasksResponse("Tìm thấy " + tasks.size() + " task", 200, tasks, true));
    }
}
