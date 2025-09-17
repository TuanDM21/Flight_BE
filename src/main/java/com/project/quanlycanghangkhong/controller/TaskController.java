package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.request.CreateTaskRequest;
import com.project.quanlycanghangkhong.request.CreateSubtaskRequest;
import com.project.quanlycanghangkhong.dto.TaskDTO;
import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import com.project.quanlycanghangkhong.dto.TaskSubtreeDTO;
import com.project.quanlycanghangkhong.dto.UpdateTaskDTO;
import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import com.project.quanlycanghangkhong.request.BulkDeleteTasksRequest;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.response.task.MyTasksApiResponse;
import com.project.quanlycanghangkhong.dto.response.task.TaskSubtreeApiResponse;
import com.project.quanlycanghangkhong.dto.response.task.TaskTreeApiResponse;
import com.project.quanlycanghangkhong.dto.response.task.TaskAttachmentsApiResponse;
import com.project.quanlycanghangkhong.dto.response.task.TaskSubtasksApiResponse;
import com.project.quanlycanghangkhong.dto.response.task.TaskApiResponse;
import com.project.quanlycanghangkhong.dto.response.task.TaskDetailApiResponse;
import com.project.quanlycanghangkhong.dto.MyTasksData;
import com.project.quanlycanghangkhong.dto.TaskTreeDTO;
import com.project.quanlycanghangkhong.request.TaskAttachmentUploadRequest;

// ✅ PRIORITY 3: Simplified DTOs imports

import com.project.quanlycanghangkhong.service.TaskService;
import com.project.quanlycanghangkhong.service.OverdueTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    
    @Autowired
    private OverdueTaskService overdueTaskService;

    @PostMapping
    @Operation(summary = "Tạo task", description = "Tạo mới một công việc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo thành công", content = @Content(schema = @Schema(implementation = TaskApiResponse.class)))
    })
    public ResponseEntity<ApiResponseCustom<TaskDTO>> createTask(@RequestBody CreateTaskRequest request) {
        System.out.println("[DEBUG] Received CreateTaskRequest: " + request);
        System.out.println("[DEBUG] Request class: " + (request != null ? request.getClass().getName() : "null"));
        if (request == null) {
            System.out.println("[DEBUG] Request is null!");
            return ResponseEntity.status(400).body(ApiResponseCustom.error("Request body is null"));
        } else {
            System.out.println("[DEBUG] Request title: " + request.getTitle());
            System.out.println("[DEBUG] Request content: " + request.getContent());
            System.out.println("[DEBUG] Request priority: " + request.getPriority());
            System.out.println("[DEBUG] Request assignments: " + request.getAssignments());
            System.out.println("[DEBUG] Request attachmentIds: " + request.getAttachmentIds());
        }
        TaskDTO created = taskService.createTaskWithAssignmentsAndAttachments(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseCustom.created(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật task", description = "Cập nhật một công việc theo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công", content = @Content(schema = @Schema(implementation = TaskApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy công việc", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<TaskDTO>> updateTask(@PathVariable Integer id,
            @RequestBody UpdateTaskDTO updateTaskDTO) {
        TaskDTO updated = taskService.updateTask(id, updateTaskDTO);
        if (updated == null)
            return ResponseEntity.status(404).body(ApiResponseCustom.notFound("Không tìm thấy công việc"));
        return ResponseEntity.ok(ApiResponseCustom.updated(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá task", description = "Xoá một công việc theo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xoá thành công", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<Void>> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponseCustom.deleted());
    }

    @DeleteMapping("/bulk-delete")
    @Operation(summary = "Xoá nhiều task", description = "Xoá nhiều công việc cùng lúc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xoá thành công", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<String>> bulkDeleteTasks(
            @Valid @RequestBody BulkDeleteTasksRequest request) {
        try {
            if (request.getTaskIds() == null || request.getTaskIds().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ApiResponseCustom.error("Danh sách task ID không được để trống"));
            }

            taskService.bulkDeleteTasks(request.getTaskIds());

            String message = "Đã xoá thành công " + request.getTaskIds().size() + " task";
            return ResponseEntity.ok(ApiResponseCustom.success(message));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    ApiResponseCustom.internalError("Lỗi khi xoá task: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết task", description = "Lấy chi tiết một công việc theo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = TaskDetailApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy công việc", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<TaskDetailDTO>> getTaskDetailById(@PathVariable Integer id) {
        TaskDetailDTO task = taskService.getTaskDetailById(id);
        if (task == null)
            return ResponseEntity.status(404).body(ApiResponseCustom.notFound("Không tìm thấy công việc"));
        return ResponseEntity.ok(ApiResponseCustom.success(task));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách task", description = "Lấy danh sách tất cả công việc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = TaskSubtasksApiResponse.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<TaskDetailDTO>>> getAllTaskDetails() {
        List<TaskDetailDTO> tasks = taskService.getAllTaskDetails();
        return ResponseEntity.ok(ApiResponseCustom.success(tasks));
    }

    @GetMapping("/my")
    @Operation(summary = "API tổng hợp: Lấy công việc của tôi với advanced search, filter status, pagination", description = "🔥 UNIFIED API cho tất cả task types với advanced search và pagination. "
            +
            "📋 TASK TYPES: " +
            "• created: Tasks đã tạo nhưng chưa giao việc (flat list) " +
            "• assigned: Tasks đã giao việc (bao gồm subtasks với hierarchy) " +
            "• received: Tasks được giao (flat list) " +
            "🎯 STATUS FILTER (chỉ cho assigned/received): IN_PROGRESS, COMPLETED, OVERDUE " +
            "🔍 KEYWORD SEARCH (cho tất cả types): Tìm kiếm trong 5 fields - ID, title, content, instructions, notes " +
            "⚡ ADVANCED FILTERS: priorities (LOW/NORMAL/HIGH/URGENT), time range (yyyy-MM-dd) " +
            "👥 RECIPIENT SEARCH (chỉ cho assigned): teamIds, userIds, unitIds (đơn giản hơn recipientTypes/recipientIds) " +
            "📄 PAGINATION: page (1-based), size (max 100, default 20)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = MyTasksApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<MyTasksData>> getMyTasks(
            @Parameter(description = "Loại task", required = true, schema = @Schema(allowableValues = { "created",
                    "assigned", "received" })) @RequestParam String type,

            @Parameter(description = "Filter theo status (chỉ cho assigned/received)", schema = @Schema(allowableValues = {
                    "IN_PROGRESS", "COMPLETED", "OVERDUE" })) @RequestParam(required = false) String status,

            @Parameter(description = "Từ khóa tìm kiếm (search trong 5 fields): ID, title, content, instructions, notes", example = "urgent task") @RequestParam(required = false) String keyword,

            @Parameter(description = "Ngày bắt đầu (format: yyyy-MM-dd)", example = "2025-08-01") @RequestParam(required = false) String startTime,

            @Parameter(description = "Ngày kết thúc (format: yyyy-MM-dd)", example = "2025-08-31") @RequestParam(required = false) String endTime,

            @Parameter(description = "Danh sách priority để filter", schema = @Schema(type = "array", allowableValues = {
                    "LOW", // 🟢 Không khẩn cấp - có thể hoãn
                    "NORMAL", // 🔵 Bình thường - công việc thường ngày
                    "HIGH", // 🟡 Quan trọng - ảnh hưởng đến chuyến bay
                    "URGENT" // 🔴 Khẩn cấp - cần xử lý ngay lập tức
            }, description = "LOW: Không khẩn cấp, NORMAL: Bình thường, HIGH: Quan trọng, URGENT: Khẩn cấp")) @RequestParam(required = false) List<String> priorities,

            @Parameter(description = "Danh sách Team IDs để filter tasks giao cho teams (chỉ cho assigned)", example = "1,2,3") @RequestParam(required = false) List<Integer> teamIds,

            @Parameter(description = "Danh sách User IDs để filter tasks giao cho users (chỉ cho assigned)", example = "1,5,10") @RequestParam(required = false) List<Integer> userIds,

            @Parameter(description = "Danh sách Unit IDs để filter tasks giao cho units (chỉ cho assigned)", example = "1,2") @RequestParam(required = false) List<Integer> unitIds,

            @Parameter(description = "Số trang (bắt đầu từ 1)", example = "1") @RequestParam(required = false, defaultValue = "1") Integer page,

            @Parameter(description = "Số items per page (max 100)", example = "20") @RequestParam(required = false, defaultValue = "20") Integer size) {
        if (!type.matches("created|assigned|received")) {
            return ResponseEntity.badRequest().body(
                    ApiResponseCustom.error("Tham số type phải là: created, assigned, hoặc received"));
        }

        // Validate status áp dụng cho type=assigned và type=received
        if (status != null && !type.matches("assigned|received")) {
            return ResponseEntity.badRequest().body(
                    ApiResponseCustom.error("Status chỉ hỗ trợ cho type=assigned và type=received"));
        }

        // Check if recipient search is used with non-assigned type
        boolean hasRecipientParams = (teamIds != null && !teamIds.isEmpty()) || 
                                   (userIds != null && !userIds.isEmpty()) || 
                                   (unitIds != null && !unitIds.isEmpty());
        
        if (hasRecipientParams && !"assigned".equals(type)) {
            return ResponseEntity.badRequest().body(
                    ApiResponseCustom.error("Recipient search (teamIds/userIds/unitIds) chỉ hỗ trợ cho type=assigned"));
        }

        // Convert simplified parameters to recipientTypes/recipientIds format for service (only for assigned type)
        List<String> recipientTypes = new ArrayList<>();
        List<Integer> recipientIds = new ArrayList<>();
        
        if ("assigned".equals(type)) {
            // Parse teamIds -> TEAM + teamIds
            if (teamIds != null && !teamIds.isEmpty()) {
                for (Integer teamId : teamIds) {
                    recipientTypes.add("TEAM");
                    recipientIds.add(teamId);
                }
            }
            
            // Parse userIds -> USER + userIds  
            if (userIds != null && !userIds.isEmpty()) {
                for (Integer userId : userIds) {
                    recipientTypes.add("USER");
                    recipientIds.add(userId);
                }
            }
            
            // Parse unitIds -> UNIT + unitIds
            if (unitIds != null && !unitIds.isEmpty()) {
                for (Integer unitId : unitIds) {
                    recipientTypes.add("UNIT");
                    recipientIds.add(unitId);
                }
            }
        }

        // Check advanced search features
        boolean hasKeywordTimeOrPriority = keyword != null || startTime != null || endTime != null ||
                (priorities != null && !priorities.isEmpty());
        boolean hasRecipientSearch = !recipientTypes.isEmpty();

        // Validate status values
        if (status != null && !status.matches("IN_PROGRESS|COMPLETED|OVERDUE")) {
            return ResponseEntity.badRequest().body(
                    ApiResponseCustom.error("Status phải là: IN_PROGRESS, COMPLETED, hoặc OVERDUE"));
        }

        // Validate pagination parameters (1-based)
        if (page != null && page < 1) {
            return ResponseEntity.badRequest().body(
                    ApiResponseCustom.error("Page phải >= 1"));
        }
        if (size != null && (size <= 0 || size > 100)) {
            return ResponseEntity.badRequest().body(
                    ApiResponseCustom.error("Size phải từ 1 đến 100"));
        }

        MyTasksData response;
        boolean hasAdvancedSearch = hasKeywordTimeOrPriority || hasRecipientSearch;

        if (hasAdvancedSearch) {
            // Sử dụng advanced search cho tất cả type với các feature được hỗ trợ
            response = taskService.getMyTasksWithAdvancedSearchAndPaginationOptimized(type, status, keyword,
                    startTime, endTime, priorities, recipientTypes, recipientIds, page, size);
        } else if (page != null || size != null) {
            // Sử dụng search thông thường với pagination tối ưu (DATABASE-LEVEL)
            response = taskService.getMyTasksWithCountStandardizedAndPaginationOptimized(type, status, page, size);
        } else {
            // 🚀 ULTRA FAST: Sử dụng batch loading optimization cho simple requests
            response = taskService.getMyTasksWithCountStandardizedUltraFast(type);

            // Apply status if specified (for assigned and received types)
            if (type.matches("assigned|received") && status != null && !status.isEmpty()) {
                // Fall back to standard method with status if ultra-fast doesn't support
                // filtering yet
                response = taskService.getMyTasksWithCountStandardized(type, status);
            }
        }

        return ResponseEntity.ok(ApiResponseCustom.success(response));
    }

    @GetMapping("/company")
    @Operation(summary = "API công ty: Lấy tất cả công việc theo phân quyền với advanced search, filter status, pagination", description = "🏢 COMPANY API với role-based permissions: "
            +
            "📋 PERMISSION LOGIC: " +
            "• ADMIN/DIRECTOR/VICE_DIRECTOR: Xem TẤT CẢ tasks trong hệ thống " +
            "• Các role khác: Xem tasks của TEAM + tasks của các UNIT thuộc team đó " +
            "🎯 STATUS FILTER: IN_PROGRESS, COMPLETED, OVERDUE " +
            "🔍 KEYWORD SEARCH: Tìm kiếm trong 5 fields - ID, title, content, instructions, notes " +
            "⚡ ADVANCED FILTERS: priorities (LOW/NORMAL/HIGH/URGENT), time range (yyyy-MM-dd) " +
            "👥 RECIPIENT SEARCH: teamIds, userIds, unitIds (đơn giản hơn recipientTypes/recipientIds) " +
            "📄 PAGINATION: page (1-based), size (max 100, default 20)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = MyTasksApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<MyTasksData>> getCompanyTasks(
            @Parameter(description = "Filter theo status", schema = @Schema(allowableValues = { "IN_PROGRESS",
                    "COMPLETED", "OVERDUE", "OPEN" })) @RequestParam(required = false) String status,

            @Parameter(description = "Từ khóa tìm kiếm (search trong 5 fields): ID, title, content, instructions, notes", example = "urgent task") @RequestParam(required = false) String keyword,

            @Parameter(description = "Ngày bắt đầu (format: yyyy-MM-dd)", example = "2025-08-01") @RequestParam(required = false) String startTime,

            @Parameter(description = "Ngày kết thúc (format: yyyy-MM-dd)", example = "2025-08-31") @RequestParam(required = false) String endTime,

            @Parameter(description = "Danh sách priority để filter", schema = @Schema(type = "array", allowableValues = {
                    "LOW", // 🟢 Không khẩn cấp - có thể hoãn
                    "NORMAL", // 🔵 Bình thường - công việc thường ngày
                    "HIGH", // 🟡 Quan trọng - ảnh hưởng đến chuyến bay
                    "URGENT" // 🔴 Khẩn cấp - cần xử lý ngay lập tức
            }, description = "LOW: Không khẩn cấp, NORMAL: Bình thường, HIGH: Quan trọng, URGENT: Khẩn cấp")) @RequestParam(required = false) List<String> priorities,

            @Parameter(description = "Danh sách Team IDs để filter tasks giao cho teams", example = "1,2,3") @RequestParam(required = false) List<Integer> teamIds,

            @Parameter(description = "Danh sách User IDs để filter tasks giao cho users", example = "1,5,10") @RequestParam(required = false) List<Integer> userIds,

            @Parameter(description = "Danh sách Unit IDs để filter tasks giao cho units", example = "1,2") @RequestParam(required = false) List<Integer> unitIds,

            @Parameter(description = "Số trang (bắt đầu từ 1)", example = "1") @RequestParam(required = false, defaultValue = "1") Integer page,

            @Parameter(description = "Số items per page (max 100)", example = "20") @RequestParam(required = false, defaultValue = "20") Integer size) {

        // Validate status values
        if (status != null && !status.matches("IN_PROGRESS|COMPLETED|OVERDUE|OPEN")) {
            return ResponseEntity.badRequest().body(
                    ApiResponseCustom.error("Status phải là: IN_PROGRESS, COMPLETED, OVERDUE, hoặc OPEN"));
        }

        // Validate pagination parameters (1-based)
        if (page != null && page < 1) {
            return ResponseEntity.badRequest().body(
                    ApiResponseCustom.error("Page phải >= 1"));
        }
        if (size != null && (size <= 0 || size > 100)) {
            return ResponseEntity.badRequest().body(
                    ApiResponseCustom.error("Size phải từ 1 đến 100"));
        }

        // Convert simplified parameters to recipientTypes/recipientIds format for service
        List<String> recipientTypes = new ArrayList<>();
        List<Integer> recipientIds = new ArrayList<>();
        
        // Parse teamIds -> TEAM + teamIds
        if (teamIds != null && !teamIds.isEmpty()) {
            for (Integer teamId : teamIds) {
                recipientTypes.add("TEAM");
                recipientIds.add(teamId);
            }
        }
        
        // Parse userIds -> USER + userIds  
        if (userIds != null && !userIds.isEmpty()) {
            for (Integer userId : userIds) {
                recipientTypes.add("USER");
                recipientIds.add(userId);
            }
        }
        
        // Parse unitIds -> UNIT + unitIds
        if (unitIds != null && !unitIds.isEmpty()) {
            for (Integer unitId : unitIds) {
                recipientTypes.add("UNIT");
                recipientIds.add(unitId);
            }
        }

        MyTasksData response;
        boolean hasRecipientSearch = !recipientTypes.isEmpty();
        boolean hasAdvancedSearch = keyword != null || startTime != null || endTime != null ||
                (priorities != null && !priorities.isEmpty()) || hasRecipientSearch;

        if (hasAdvancedSearch) {
            // Sử dụng advanced search với role-based permissions và recipient search
            response = taskService.getCompanyTasksWithAdvancedSearchAndPagination(status, keyword,
                    startTime, endTime, priorities, recipientTypes, recipientIds, page, size);
        } else if (page != null || size != null) {
            // Sử dụng pagination với role-based permissions
            response = taskService.getUnitTasksWithPagination(status, page, size);
        } else {
            // Simple request với role-based permissions
            response = taskService.getUnitTasks(status);
        }

        return ResponseEntity.ok(ApiResponseCustom.success(response));
    }

    // MÔ HÌNH ADJACENCY LIST: API Subtask
    @PostMapping("/{parentId}/subtasks")
    @Operation(summary = "Tạo subtask", description = "Tạo subtask con cho một task cha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo subtask thành công", content = @Content(schema = @Schema(implementation = TaskApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy task cha", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<TaskDTO>> createSubtask(@PathVariable Integer parentId,
            @RequestBody CreateSubtaskRequest request) {
        // parentId được truyền qua path parameter, truyền trực tiếp vào service
        TaskDTO created = taskService.createSubtask(parentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseCustom.created(created));
    }

    @GetMapping("/{id}/subtasks")
    @Operation(summary = "Lấy danh sách subtask", description = "Lấy tất cả subtask con của một task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = TaskSubtasksApiResponse.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<TaskDetailDTO>>> getSubtasks(@PathVariable Integer id) {
        List<TaskDetailDTO> subtasks = taskService.getSubtasks(id);
        return ResponseEntity.ok(ApiResponseCustom.success(subtasks));
    }

    @GetMapping("/{id}/subtree")
    @Operation(summary = "Lấy toàn bộ cây con của task (flat list)", description = "Lấy task cùng với tất cả subtask dưới dạng flat list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = TaskSubtreeApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy task", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<TaskSubtreeDTO>>> getTaskSubtree(@PathVariable Integer id) {
        List<TaskSubtreeDTO> subtree = taskService.getTaskSubtreeAsSubtreeDTO(id);

        if (subtree.isEmpty()) {
            return ResponseEntity.status(404).body(
                    ApiResponseCustom.notFound("Không tìm thấy task với ID: " + id));
        }

        return ResponseEntity.ok(ApiResponseCustom.success(subtree));
    }

    @GetMapping("/{id}/tree")
    @Operation(summary = "Lấy toàn bộ cây con của task (hierarchical structure)", description = "Lấy task cùng với tất cả subtask theo cấu trúc phân cấp nested - dễ dàng cho frontend hiển thị tree view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = TaskTreeApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy task", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<TaskTreeDTO>> getTaskTree(@PathVariable Integer id) {
        TaskTreeDTO taskTree = taskService.getTaskSubtreeHierarchical(id);

        if (taskTree == null) {
            return ResponseEntity.status(404).body(
                    ApiResponseCustom.notFound("Không tìm thấy task với ID: " + id));
        }

        return ResponseEntity.ok(ApiResponseCustom.success(taskTree));
    }

    // Existing endpoints...

    // === ATTACHMENT MANAGEMENT ===
    // Attachment chỉ được quản lý thông qua createTask và updateTask
    // Đã loại bỏ các API riêng biệt để gán/gỡ attachment vì không cần thiết

    @GetMapping("/{id}/attachments")
    @Operation(summary = "Lấy danh sách file đính kèm của task", description = "Lấy tất cả file đính kèm trực tiếp của task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = TaskAttachmentsApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy task", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<AttachmentDTO>>> getTaskAttachments(@PathVariable Integer id) {
        List<AttachmentDTO> attachments = taskService.getTaskAttachments(id);
        return ResponseEntity.ok(ApiResponseCustom.success(attachments));
    }

    @PostMapping("/{id}/attachments")
    @Operation(summary = "Thêm file đính kèm vào task", description = "Thêm các file đính kèm đã upload vào task cụ thể. File đính kèm phải được upload trước thông qua /api/attachments/generate-upload-urls và confirm-upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thêm file đính kèm thành công", content = @Content(schema = @Schema(implementation = TaskAttachmentsApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy task hoặc attachment"),
            @ApiResponse(responseCode = "409", description = "Attachment đã được gán vào task khác")
    })
    public ResponseEntity<ApiResponseCustom<List<AttachmentDTO>>> addAttachmentsToTask(
            @PathVariable Integer id,
            @Valid @RequestBody TaskAttachmentUploadRequest request) {
        try {
            List<AttachmentDTO> addedAttachments = taskService.addAttachmentsToTask(id, request.getAttachmentIds());

            return ResponseEntity.ok(ApiResponseCustom.success(addedAttachments));

        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(404).body(ApiResponseCustom.notFound(e.getMessage()));
            } else if (e.getMessage().contains("đã được gán vào task khác")) {
                return ResponseEntity.status(409).body(ApiResponseCustom.error(e.getMessage()));
            } else {
                return ResponseEntity.status(400).body(ApiResponseCustom.error(e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseCustom.internalError("Lỗi server khi thêm file đính kèm: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/attachments")
    @Operation(summary = "Xóa file đính kèm khỏi task", description = "Xóa các file đính kèm khỏi task cụ thể. File sẽ không bị xóa vĩnh viễn mà chỉ được gỡ liên kết khỏi task")
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
                            "removedAttachmentIds", request.getAttachmentIds())));

        } catch (RuntimeException e) {
            if (e.getMessage().contains("Không tìm thấy")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", e.getMessage(),
                        "statusCode", 404,
                        "data", null));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", e.getMessage(),
                        "statusCode", 400,
                        "data", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi server khi xóa file đính kèm: " + e.getMessage(),
                    "statusCode", 500,
                    "data", null));
        }
    }

    // ============== SEARCH & FILTER ENDPOINTS ==============

    // ===================================================================
    // OVERDUE MANAGEMENT ENDPOINTS
    // ===================================================================

    @PostMapping("/force-update-overdue")
    @Operation(summary = "Force update overdue status", description = "Manually trigger overdue status update for all assignments and tasks. Useful for testing or immediate update without waiting for scheduled job.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdue update completed successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseCustom<Map<String, Object>>> forceUpdateOverdueStatus() {
        try {
            // Trigger manual overdue update
            overdueTaskService.forceUpdateOverdueStatus();
            
            return ResponseEntity.ok(ApiResponseCustom.success(Map.of(
                    "message", "Overdue status update completed successfully",
                    "executedAt", java.time.LocalDateTime.now().toString(),
                    "note", "All assignments and tasks have been checked and updated if overdue"
            )));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseCustom.internalError("Failed to update overdue status: " + e.getMessage()));
        }
    }

    // ===================================================================
    // TASK HIERARCHY & SUBTASK ENDPOINTS
    // ===================================================================

}
