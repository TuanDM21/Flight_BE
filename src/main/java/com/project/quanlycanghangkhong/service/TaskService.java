package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.CreateTaskRequest;
import com.project.quanlycanghangkhong.dto.TaskDTO;
import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import com.project.quanlycanghangkhong.dto.DocumentDetailDTO;
import com.project.quanlycanghangkhong.dto.UpdateTaskDTO;
import java.util.List;

public interface TaskService {
    TaskDTO createTaskWithAssignmentsAndDocuments(CreateTaskRequest request);
    TaskDTO createTask(TaskDTO taskDTO);
    TaskDTO updateTask(Integer id, UpdateTaskDTO updateTaskDTO);
    void deleteTask(Integer id);
    TaskDTO getTaskById(Integer id);
    List<TaskDTO> getAllTasks();
    TaskDetailDTO getTaskDetailById(Integer id);
    List<TaskDetailDTO> getAllTaskDetails();
    void updateTaskStatus(com.project.quanlycanghangkhong.model.Task task);
}