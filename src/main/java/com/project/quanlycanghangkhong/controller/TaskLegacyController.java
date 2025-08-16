package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.response.task.MyTasksResponse;
import com.project.quanlycanghangkhong.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskLegacyController {
    
    @Autowired
    private TaskService taskService;

    @GetMapping("/my/legacy")
    @Operation(summary = "LEGACY: Lấy công việc của tôi theo loại - Old Format", 
               description = "DEPRECATED: Sử dụng /api/tasks/my thay thế. Endpoint này giữ lại để backward compatibility.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = MyTasksResponse.class))),
        @ApiResponse(responseCode = "400", description = "Tham số type không hợp lệ", content = @Content(schema = @Schema(implementation = MyTasksResponse.class)))
    })
    @Deprecated
    public ResponseEntity<MyTasksResponse> getMyTasksLegacy(@RequestParam String type) {
        if (!type.matches("created|assigned|received")) {
            return ResponseEntity.badRequest().body(
                new MyTasksResponse("Tham số type phải là: created, assigned, hoặc received", 400, null, 0, type, false, null)
            );
        }
        
        MyTasksResponse response = taskService.getMyTasksWithCount(type);
        return ResponseEntity.ok(response);
    }
}
