package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.CreateTaskRequest;
import com.project.quanlycanghangkhong.dto.TaskDTO;
import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import com.project.quanlycanghangkhong.dto.UpdateTaskDTO;
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

    @PostMapping
    @Operation(summary = "Tạo task", description = "Tạo mới một công việc")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo thành công", content = @Content(schema = @Schema(implementation = ApiTaskResponse.class)))
    })
    public ResponseEntity<ApiTaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        TaskDTO created = taskService.createTaskWithAssignmentsAndDocuments(request);
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
    @Operation(summary = "Lấy công việc của tôi theo loại", description = "Lấy danh sách công việc theo loại: created (đã tạo), assigned (đã giao), received (được giao)")
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
            case "created" -> "Danh sách công việc đã tạo";
            case "assigned" -> "Danh sách công việc đã giao";
            case "received" -> "Danh sách công việc được giao";
            default -> "Thành công";
        };
        
        return ResponseEntity.ok(new ApiAllTasksResponse(message, 200, tasks, true));
    }
}
