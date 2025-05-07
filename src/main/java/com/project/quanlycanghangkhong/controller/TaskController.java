package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.CreateTaskRequest;
import com.project.quanlycanghangkhong.dto.TaskDTO;
import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponseCustom<TaskDTO>> createTask(@RequestBody CreateTaskRequest request) {
        TaskDTO created = taskService.createTaskWithAssignmentsAndDocuments(request);
        return ResponseEntity.status(201).body(ApiResponseCustom.created(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<TaskDTO>> updateTask(@PathVariable Integer id, @RequestBody TaskDTO taskDTO) {
        TaskDTO updated = taskService.updateTask(id, taskDTO);
        if (updated == null) return ResponseEntity.status(404).body(ApiResponseCustom.error(HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));
        return ResponseEntity.ok(ApiResponseCustom.success("Cập nhật thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<Void>> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponseCustom.success("Xoá thành công", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseCustom<TaskDetailDTO>> getTaskDetailById(@PathVariable Integer id) {
        TaskDetailDTO task = taskService.getTaskDetailById(id);
        if (task == null) return ResponseEntity.status(404).body(ApiResponseCustom.error(HttpStatus.NOT_FOUND, "Không tìm thấy công việc"));
        return ResponseEntity.ok(ApiResponseCustom.success(task));
    }

    @GetMapping
    public ResponseEntity<ApiResponseCustom<List<TaskDetailDTO>>> getAllTaskDetails() {
        List<TaskDetailDTO> tasks = taskService.getAllTaskDetails();
        return ResponseEntity.ok(ApiResponseCustom.success(tasks));
    }
}
