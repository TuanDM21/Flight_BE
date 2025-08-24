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
import com.project.quanlycanghangkhong.dto.response.task.ApiMyTasksResponse;
import com.project.quanlycanghangkhong.dto.response.task.MyTasksData;
import com.project.quanlycanghangkhong.dto.response.task.ApiTaskAttachmentsSimplifiedResponse;
import com.project.quanlycanghangkhong.dto.response.task.ApiTaskAttachmentUploadResponse;
import com.project.quanlycanghangkhong.dto.response.task.TaskTreeDTO;
import com.project.quanlycanghangkhong.dto.request.TaskAttachmentUploadRequest;
import com.project.quanlycanghangkhong.dto.request.AdvancedSearchRequest;

// ✅ PRIORITY 3: Simplified DTOs imports
import com.project.quanlycanghangkhong.dto.simplified.TaskDetailSimplifiedDTO;

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
    @Operation(summary = "Lấy công việc của tôi theo loại với ROOT TASKS count (sorted by latest), advanced search và pagination", 
               description = "Lấy danh sách công việc theo loại với sort theo thời gian mới nhất và thông tin count ROOT TASKS: created (đã tạo nhưng chưa giao việc - flat list), assigned (đã giao việc bao gồm tất cả subtasks với hierarchyLevel), received (được giao việc - flat list). Count chỉ tính ROOT TASKS (parent IS NULL), data vẫn bao gồm tất cả tasks để hiển thị hierarchy. Hỗ trợ filter cho type=assigned: completed, pending, urgent, overdue. Hỗ trợ advanced search cho TẤT CẢ TYPES với keyword, priorities, time range (format: yyyy-MM-dd). Recipient search chỉ cho type=assigned. Hỗ trợ pagination với page (bắt đầu từ 1) và size (max 100, default 20)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiMyTasksResponse.class))),
        @ApiResponse(responseCode = "400", description = "Tham số type hoặc filter không hợp lệ", content = @Content(schema = @Schema(implementation = ApiMyTasksResponse.class)))
    })
    public ResponseEntity<ApiMyTasksResponse> getMyTasks(
            @RequestParam String type,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) List<String> priorities,
            @RequestParam(required = false) List<String> recipientTypes,
            @RequestParam(required = false) List<Integer> recipientIds,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        if (!type.matches("created|assigned|received")) {
            return ResponseEntity.badRequest().body(
                ApiMyTasksResponse.error("Tham số type phải là: created, assigned, hoặc received", 400)
            );
        }
        
        // Validate filter chỉ áp dụng cho type=assigned
        if (filter != null && !"assigned".equals(type)) {
            return ResponseEntity.badRequest().body(
                ApiMyTasksResponse.error("Filter chỉ hỗ trợ cho type=assigned", 400)
            );
        }
        
        // Check advanced search features
        boolean hasKeywordTimeOrPriority = keyword != null || startTime != null || endTime != null || 
                                          (priorities != null && !priorities.isEmpty());
        boolean hasRecipientSearch = (recipientTypes != null && !recipientTypes.isEmpty());
        
        // Validate recipient search chỉ áp dụng cho type=assigned
        if (hasRecipientSearch && !"assigned".equals(type)) {
            return ResponseEntity.badRequest().body(
                ApiMyTasksResponse.error("Recipient search chỉ hỗ trợ cho type=assigned", 400)
            );
        }
        
        // Validate filter values
        if (filter != null && !filter.matches("completed|pending|urgent|overdue")) {
            return ResponseEntity.badRequest().body(
                ApiMyTasksResponse.error("Filter phải là: completed, pending, urgent, hoặc overdue", 400)
            );
        }
        
        // Validate recipients matching
        if (recipientTypes != null && recipientIds != null && recipientTypes.size() != recipientIds.size()) {
            return ResponseEntity.badRequest().body(
                ApiMyTasksResponse.error("Số lượng recipientTypes và recipientIds phải bằng nhau", 400)
            );
        }
        
        // Validate recipient types
        if (recipientTypes != null) {
            for (String recipientType : recipientTypes) {
                if (!recipientType.matches("user|team|unit")) {
                    return ResponseEntity.badRequest().body(
                        ApiMyTasksResponse.error("recipientType phải là: user, team, hoặc unit", 400)
                    );
                }
            }
        }
        
        // Validate pagination parameters (1-based)
        if (page != null && page < 1) {
            return ResponseEntity.badRequest().body(
                ApiMyTasksResponse.error("Page phải >= 1", 400)
            );
        }
        if (size != null && (size <= 0 || size > 100)) {
            return ResponseEntity.badRequest().body(
                ApiMyTasksResponse.error("Size phải từ 1 đến 100", 400)
            );
        }
        
        MyTasksData response;
        boolean hasAdvancedSearch = hasKeywordTimeOrPriority || hasRecipientSearch;
        
        if (hasAdvancedSearch) {
            // Sử dụng advanced search cho tất cả type với các feature được hỗ trợ
            response = taskService.getMyTasksWithAdvancedSearchAndPaginationOptimized(type, filter, keyword, 
                startTime, endTime, priorities, recipientTypes, recipientIds, page, size);
        } else if (page != null || size != null) {
            // Sử dụng search thông thường với pagination tối ưu (DATABASE-LEVEL)
            response = taskService.getMyTasksWithCountStandardizedAndPaginationOptimized(type, filter, page, size);
        } else {
            // 🚀 ULTRA FAST: Sử dụng batch loading optimization cho simple requests
            response = taskService.getMyTasksWithCountStandardizedUltraFast(type);
            
            // Apply filter if specified (for assigned type only)
            if ("assigned".equals(type.toLowerCase()) && filter != null && !filter.isEmpty()) {
                // Fall back to standard method with filter if ultra-fast doesn't support filtering yet
                response = taskService.getMyTasksWithCountStandardized(type, filter);
            }
        }
        
        return ResponseEntity.ok(ApiMyTasksResponse.success(response));
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

        @GetMapping("/{id}/subtree")
    @Operation(summary = "Lấy toàn bộ cây con của task (flat list)", 
               description = "Lấy task cùng với tất cả subtask dưới dạng flat list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy task")
    })
    public ResponseEntity<List<TaskDetailDTO>> getTaskSubtree(@PathVariable Integer id) {
        List<TaskDetailDTO> subtree = taskService.getTaskSubtree(id);
        
        if (subtree.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(subtree);
    }

    @GetMapping("/{id}/tree")
    @Operation(summary = "Lấy toàn bộ cây con của task (hierarchical structure)", 
               description = "Lấy task cùng với tất cả subtask theo cấu trúc phân cấp nested - dễ dàng cho frontend hiển thị tree view")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy task")
    })
    public ResponseEntity<TaskTreeDTO> getTaskTree(@PathVariable Integer id) {
        TaskTreeDTO taskTree = taskService.getTaskSubtreeHierarchical(id);
        
        if (taskTree == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(taskTree);
    }
    
    // Existing endpoints...

    // === ATTACHMENT MANAGEMENT ===
    // Attachment chỉ được quản lý thông qua createTask và updateTask
    // Đã loại bỏ các API riêng biệt để gán/gỡ attachment vì không cần thiết
    
    @GetMapping("/{id}/attachments")
    @Operation(summary = "Lấy danh sách file đính kèm của task (Simplified)", description = "Lấy tất cả file đính kèm trực tiếp của task với cấu trúc simplified, không có nested data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiTaskAttachmentsSimplifiedResponse.class))),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy task", content = @Content(schema = @Schema(implementation = ApiTaskAttachmentsSimplifiedResponse.class)))
    })
    public ResponseEntity<ApiTaskAttachmentsSimplifiedResponse> getTaskAttachments(@PathVariable Integer id) {
        List<com.project.quanlycanghangkhong.dto.simplified.SimpleAttachmentDTO> attachments = taskService.getTaskAttachmentsSimplified(id);
        return ResponseEntity.ok(new ApiTaskAttachmentsSimplifiedResponse("Thành công", 200, attachments, true));
    }

    @GetMapping("/{id}/attachments/legacy")
    @Operation(summary = "Lấy danh sách file đính kèm của task (Legacy - có nested data)", description = "Legacy endpoint với AttachmentDTO có nested UserDTO - có thể gây lồng data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy task")
    })
    public ResponseEntity<?> getTaskAttachmentsLegacy(@PathVariable Integer id) {
        List<AttachmentDTO> attachments = taskService.getTaskAttachments(id);
        return ResponseEntity.ok(Map.of(
            "message", "Thành công (Legacy endpoint)",
            "statusCode", 200,
            "data", attachments,
            "success", true,
            "warning", "Endpoint này có thể có nested data. Khuyến nghị dùng /{id}/attachments"
        ));
    }

    @PostMapping("/{id}/attachments")
    @Operation(summary = "Thêm file đính kèm vào task", 
               description = "Thêm các file đính kèm đã upload vào task cụ thể. File đính kèm phải được upload trước thông qua /api/attachments/generate-upload-urls và confirm-upload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thêm file đính kèm thành công", 
                    content = @Content(schema = @Schema(implementation = ApiTaskAttachmentUploadResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy task hoặc attachment"),
        @ApiResponse(responseCode = "409", description = "Attachment đã được gán vào task khác")
    })
    public ApiTaskAttachmentUploadResponse addAttachmentsToTask(
            @PathVariable Integer id, 
            @Valid @RequestBody TaskAttachmentUploadRequest request) {
        try {
            List<AttachmentDTO> addedAttachments = taskService.addAttachmentsToTask(id, request.getAttachmentIds());
            
            String message = String.format("Đã thêm %d file đính kèm vào task thành công", addedAttachments.size());
            return new ApiTaskAttachmentUploadResponse(message, 200, addedAttachments, true);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return new ApiTaskAttachmentUploadResponse(e.getMessage(), 404, null, false);
            } else if (e.getMessage().contains("đã được gán vào task khác")) {
                return new ApiTaskAttachmentUploadResponse(e.getMessage(), 409, null, false);
            } else {
                return new ApiTaskAttachmentUploadResponse(e.getMessage(), 400, null, false);
            }
        } catch (Exception e) {
            return new ApiTaskAttachmentUploadResponse("Lỗi server khi thêm file đính kèm: " + e.getMessage(), 500, null, false);
        }
    }

    @DeleteMapping("/{id}/attachments")
    @Operation(summary = "Xóa file đính kèm khỏi task", 
               description = "Xóa các file đính kèm khỏi task cụ thể. File sẽ không bị xóa vĩnh viễn mà chỉ được gỡ liên kết khỏi task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Xóa file đính kèm thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy task")
    })
    public ResponseEntity<Map<String, Object>> removeAttachmentsFromTask(
            @PathVariable Integer id, 
            @Valid @RequestBody TaskAttachmentUploadRequest request) {
        try {
            int removedCount = taskService.removeAttachmentsFromTask(id, request.getAttachmentIds());
            
            String message = String.format("Đã xóa %d file đính kèm khỏi task thành công", removedCount);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", message,
                "statusCode", 200,
                "data", Map.of(
                    "taskId", id,
                    "removedCount", removedCount,
                    "removedAttachmentIds", request.getAttachmentIds()
                )
            ));
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "statusCode", 404,
                    "data", null
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "statusCode", 400,
                    "data", null
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Lỗi server khi xóa file đính kèm: " + e.getMessage(),
                "statusCode", 500,
                "data", null
            ));
        }
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
    
    // ===================================================================
    // ✅ PRIORITY 3: SIMPLIFIED DTOs ENDPOINTS
    // ===================================================================
    
    @GetMapping("/{id}/simplified")
    @Operation(summary = "Lấy chi tiết task với Simplified DTO", 
               description = "PRIORITY 3: Trả về task detail với cấu trúc đơn giản hóa, không có nested DTOs phức tạp")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy task")
    })
    public ResponseEntity<?> getTaskDetailSimplified(@PathVariable Integer id) {
        try {
            TaskDetailSimplifiedDTO task = taskService.getTaskDetailSimplifiedById(id);
            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                        "success", false,
                        "message", "Không tìm thấy task với ID: " + id,
                        "statusCode", 404,
                        "data", null
                    )
                );
            }
            return ResponseEntity.ok(
                Map.of(
                    "success", true,
                    "message", "Lấy chi tiết task thành công (Simplified DTO)",
                    "statusCode", 200,
                    "data", task,
                    "simplifiedStructure", true,
                    "explanation", "Flattened user info, no nested DTOs, better performance"
                )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                    "success", false,
                    "message", "Lỗi server: " + e.getMessage(),
                    "statusCode", 500,
                    "data", null
                )
            );
        }
    }
    
    // ============== ADVANCED SEARCH ENDPOINTS ==============
    
    @PostMapping("/my/search")
    @Operation(summary = "Tìm kiếm nâng cao tasks đã giao việc", 
               description = "Tìm kiếm tasks với nhiều tiêu chí: keyword, time range (format: yyyy-MM-dd), priority, recipient. Chỉ áp dụng cho type=assigned")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tìm kiếm thành công", 
                    content = @Content(schema = @Schema(implementation = ApiMyTasksResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ",
                    content = @Content(schema = @Schema(implementation = ApiMyTasksResponse.class)))
    })
    public ResponseEntity<ApiMyTasksResponse> searchMyTasksAdvanced(@RequestBody AdvancedSearchRequest searchRequest) {
        // Validate input
        if (searchRequest == null || !searchRequest.isValid()) {
            return ResponseEntity.badRequest().body(
                ApiMyTasksResponse.error("Dữ liệu tìm kiếm không hợp lệ", 400)
            );
        }
        
        // Check có tiêu chí tìm kiếm không
        if (!searchRequest.hasSearchCriteria()) {
            return ResponseEntity.badRequest().body(
                ApiMyTasksResponse.error("Cần ít nhất một tiêu chí tìm kiếm", 400)
            );
        }
        
        MyTasksData response = taskService.searchMyTasksAdvanced(searchRequest);
        return ResponseEntity.ok(ApiMyTasksResponse.success(response));
    }
    
}
