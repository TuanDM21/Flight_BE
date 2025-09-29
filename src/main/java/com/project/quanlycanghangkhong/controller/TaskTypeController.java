package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.TaskTypeDTO;
import com.project.quanlycanghangkhong.request.CreateTaskTypeRequest;
import com.project.quanlycanghangkhong.request.UpdateTaskTypeRequest;
import com.project.quanlycanghangkhong.service.TaskTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * TaskTypeController - REST Controller cho quản lý TaskType
 * Cung cấp các API endpoint để thao tác với loại task
 * 
 * ✅ FIXED: Sử dụng ApiResponseCustom trực tiếp thay vì custom wrapper classes
 * ✅ PATTERN: Tuân thủ TaskController pattern - không có double-wrapping
 */
@RestController
@RequestMapping("/api/task-types")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Task Type Management", description = "APIs for managing task types - Quản lý loại công việc")
public class TaskTypeController {

    private final TaskTypeService taskTypeService;

    @PostMapping
    @Operation(summary = "Create new task type", description = "Tạo loại công việc mới với tên được chỉ định")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task type created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Success Response",
                                    value = "{\n" +
                                            "  \"message\": \"Đã tạo thành công\",\n" +
                                            "  \"statusCode\": 201,\n" +
                                            "  \"success\": true,\n" +
                                            "  \"data\": {\n" +
                                            "    \"id\": 1,\n" +
                                            "    \"name\": \"Bảo trì\"\n" +
                                            "  }\n" +
                                            "}"))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Bad Request",
                                    value = "{\n" +
                                            "  \"message\": \"Tên loại công việc không được để trống\",\n" +
                                            "  \"statusCode\": 400,\n" +
                                            "  \"success\": false,\n" +
                                            "  \"data\": null\n" +
                                            "}"))),
            @ApiResponse(responseCode = "409", description = "Task type with same name already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Conflict",
                                    value = "{\n" +
                                            "  \"message\": \"Loại công việc đã tồn tại\",\n" +
                                            "  \"statusCode\": 409,\n" +
                                            "  \"success\": false,\n" +
                                            "  \"data\": null\n" +
                                            "}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<TaskTypeDTO>> createTaskType(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Task type creation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateTaskTypeRequest.class,
                            example = "{\n" +
                                    "  \"name\": \"Bảo trì\"\n" +
                                    "}")))
            @Valid @RequestBody CreateTaskTypeRequest request) {
        
        log.info("Creating task type with name: {}", request.getName());
        ApiResponseCustom<TaskTypeDTO> response = taskTypeService.createTaskType(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all task types", description = "Lấy tất cả loại công việc trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task types retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Success Response",
                                    value = "{\n" +
                                            "  \"message\": \"Thành công\",\n" +
                                            "  \"statusCode\": 200,\n" +
                                            "  \"success\": true,\n" +
                                            "  \"data\": [\n" +
                                            "    {\n" +
                                            "      \"id\": 1,\n" +
                                            "      \"name\": \"Bảo trì\"\n" +
                                            "    },\n" +
                                            "    {\n" +
                                            "      \"id\": 2,\n" +
                                            "      \"name\": \"An ninh\"\n" +
                                            "    },\n" +
                                            "    {\n" +
                                            "      \"id\": 3,\n" +
                                            "      \"name\": \"Vệ sinh\"\n" +
                                            "    }\n" +
                                            "  ]\n" +
                                            "}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<List<TaskTypeDTO>>> getAllTaskTypes() {
        log.info("Fetching all task types");
        ApiResponseCustom<List<TaskTypeDTO>> response = taskTypeService.getAllTaskTypes();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task type by ID", description = "Retrieve a specific task type by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task type retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Success Response",
                                    value = "{\n" +
                                            "  \"message\": \"Thành công\",\n" +
                                            "  \"statusCode\": 200,\n" +
                                            "  \"success\": true,\n" +
                                            "  \"data\": {\n" +
                                            "    \"id\": 1,\n" +
                                            "    \"name\": \"Bảo trì\"\n" +
                                            "  }\n" +
                                            "}"))),
            @ApiResponse(responseCode = "404", description = "Task type not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Not Found",
                                    value = "{\n" +
                                            "  \"message\": \"Không tìm thấy loại công việc\",\n" +
                                            "  \"statusCode\": 404,\n" +
                                            "  \"success\": false,\n" +
                                            "  \"data\": null\n" +
                                            "}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<TaskTypeDTO>> getTaskTypeById(
            @Parameter(description = "Task type ID", required = true, example = "1")
            @PathVariable Integer id) {
        
        log.info("Fetching task type with ID: {}", id);
        ApiResponseCustom<TaskTypeDTO> response = taskTypeService.getTaskTypeById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task type", description = "Update an existing task type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task type updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "404", description = "Task type not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "409", description = "Task type with same name already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<TaskTypeDTO>> updateTaskType(
            @Parameter(description = "Task type ID", required = true, example = "1")
            @PathVariable Integer id,
            @Valid @RequestBody UpdateTaskTypeRequest request) {
        
        log.info("Updating task type with ID: {}", id);
        ApiResponseCustom<TaskTypeDTO> response = taskTypeService.updateTaskType(id, request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task type", description = "Delete a task type (only if not being used by any tasks)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task type deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "404", description = "Task type not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "409", description = "Task type is being used by existing tasks",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<String>> deleteTaskType(
            @Parameter(description = "Task type ID", required = true, example = "1")
            @PathVariable Integer id) {
        
        log.info("Deleting task type with ID: {}", id);
        ApiResponseCustom<String> response = taskTypeService.deleteTaskType(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
