package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.CreateTaskRequest;
import com.project.quanlycanghangkhong.dto.TaskDTO;
import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import com.project.quanlycanghangkhong.dto.response.task.ApiAllTasksResponse;
import com.project.quanlycanghangkhong.dto.response.task.ApiTaskResponse;
import com.project.quanlycanghangkhong.dto.response.task.ApiTaskDetailResponse;
import com.project.quanlycanghangkhong.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiTaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        TaskDTO created = taskService.createTaskWithAssignmentsAndDocuments(request);
        ApiTaskResponse res = new ApiTaskResponse("Tạo công việc thành công", 201, created, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiTaskResponse> updateTask(@PathVariable Integer id, @RequestBody TaskDTO taskDTO) {
        TaskDTO updated = taskService.updateTask(id, taskDTO);
        if (updated == null) return ResponseEntity.status(404).body(new ApiTaskResponse("Không tìm thấy công việc", 404, null, false));
        return ResponseEntity.ok(new ApiTaskResponse("Cập nhật thành công", 200, updated, true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiTaskResponse> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(new ApiTaskResponse("Xoá thành công", 200, null, true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiTaskDetailResponse> getTaskDetailById(@PathVariable Integer id) {
        TaskDetailDTO task = taskService.getTaskDetailById(id);
        if (task == null) return ResponseEntity.status(404).body(new ApiTaskDetailResponse("Không tìm thấy công việc", 404, null, false));
        return ResponseEntity.ok(new ApiTaskDetailResponse("Thành công", 200, task, true));
    }

    @GetMapping
    public ResponseEntity<ApiAllTasksResponse> getAllTaskDetails() {
        List<TaskDetailDTO> tasks = taskService.getAllTaskDetails();
        return ResponseEntity.ok(new ApiAllTasksResponse("Thành công", 200, tasks, true));
    }
}
