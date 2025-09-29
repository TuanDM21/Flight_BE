package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.TaskTypeDTO;
import com.project.quanlycanghangkhong.model.TaskType;
import com.project.quanlycanghangkhong.repository.TaskTypeRepository;
import com.project.quanlycanghangkhong.request.CreateTaskTypeRequest;
import com.project.quanlycanghangkhong.request.UpdateTaskTypeRequest;
import com.project.quanlycanghangkhong.service.TaskTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TaskTypeServiceImpl - Implementation của TaskTypeService
 * Quản lý logic nghiệp vụ cho TaskType
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskTypeServiceImpl implements TaskTypeService {

    private final TaskTypeRepository taskTypeRepository;

    @Override
    public ApiResponseCustom<TaskTypeDTO> createTaskType(CreateTaskTypeRequest request) {
        try {
            log.info("Creating new task type: {}", request.getName());

            // Kiểm tra xem task type đã tồn tại chưa
            Optional<TaskType> existingTaskType = taskTypeRepository.findByNameIgnoreCase(request.getName());
            if (existingTaskType.isPresent()) {
                return ApiResponseCustom.error(HttpStatus.CONFLICT, "Task type with name '" + request.getName() + "' already exists");
            }

            // Tạo TaskType mới
            TaskType taskType = TaskType.builder()
                    .name(request.getName())
                    .build();

            TaskType savedTaskType = taskTypeRepository.save(taskType);
            TaskTypeDTO dto = convertToDTO(savedTaskType);

            log.info("Task type created successfully with ID: {}", savedTaskType.getId());
            return ApiResponseCustom.success("Task type created successfully", dto);

        } catch (Exception e) {
            log.error("Error creating task type: {}", e.getMessage(), e);
            return ApiResponseCustom.error("Failed to create task type: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseCustom<List<TaskTypeDTO>> getAllTaskTypes() {
        try {
            log.info("Fetching all task types");
            List<TaskType> taskTypes = taskTypeRepository.findAll();
            List<TaskTypeDTO> dtos = taskTypes.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ApiResponseCustom.success("Task types retrieved successfully", dtos);
        } catch (Exception e) {
            log.error("Error fetching all task types: {}", e.getMessage(), e);
            return ApiResponseCustom.error("Failed to fetch task types: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponseCustom<TaskTypeDTO> getTaskTypeById(Integer id) {
        try {
            log.info("Fetching task type by ID: {}", id);
            Optional<TaskType> taskTypeOpt = taskTypeRepository.findById(id);

            if (taskTypeOpt.isEmpty()) {
                return ApiResponseCustom.error(HttpStatus.NOT_FOUND, "Task type not found with ID: " + id);
            }

            TaskTypeDTO dto = convertToDTO(taskTypeOpt.get());
            return ApiResponseCustom.success("Task type retrieved successfully", dto);

        } catch (Exception e) {
            log.error("Error fetching task type by ID {}: {}", id, e.getMessage(), e);
            return ApiResponseCustom.error("Failed to fetch task type: " + e.getMessage());
        }
    }

    @Override
    public ApiResponseCustom<TaskTypeDTO> updateTaskType(Integer id, UpdateTaskTypeRequest request) {
        try {
            log.info("Updating task type with ID: {}", id);
            Optional<TaskType> taskTypeOpt = taskTypeRepository.findById(id);

            if (taskTypeOpt.isEmpty()) {
                return ApiResponseCustom.error(HttpStatus.NOT_FOUND, "Task type not found with ID: " + id);
            }

            TaskType taskType = taskTypeOpt.get();

            // Kiểm tra trùng tên nếu có cập nhật tên
            if (request.getName() != null && !request.getName().equalsIgnoreCase(taskType.getName())) {
                Optional<TaskType> existingTaskType = taskTypeRepository.findByNameIgnoreCase(request.getName());
                if (existingTaskType.isPresent()) {
                    return ApiResponseCustom.error(HttpStatus.CONFLICT, "Task type with name '" + request.getName() + "' already exists");
                }
                taskType.setName(request.getName());
            }

            TaskType updatedTaskType = taskTypeRepository.save(taskType);
            TaskTypeDTO dto = convertToDTO(updatedTaskType);

            log.info("Task type updated successfully with ID: {}", id);
            return ApiResponseCustom.success("Task type updated successfully", dto);

        } catch (Exception e) {
            log.error("Error updating task type with ID {}: {}", id, e.getMessage(), e);
            return ApiResponseCustom.error("Failed to update task type: " + e.getMessage());
        }
    }

    @Override
    public ApiResponseCustom<String> deleteTaskType(Integer id) {
        try {
            log.info("Deleting task type with ID: {}", id);
            Optional<TaskType> taskTypeOpt = taskTypeRepository.findById(id);

            if (taskTypeOpt.isEmpty()) {
                return ApiResponseCustom.error(HttpStatus.NOT_FOUND, "Task type not found with ID: " + id);
            }

            // Kiểm tra xem task type có đang được sử dụng không
            boolean isInUse = taskTypeRepository.isTaskTypeInUse(id);
            if (isInUse) {
                return ApiResponseCustom.error(HttpStatus.CONFLICT, "Cannot delete task type because it is being used by existing tasks");
            }

            taskTypeRepository.deleteById(id);

            log.info("Task type deleted successfully with ID: {}", id);
            return ApiResponseCustom.success("Task type deleted successfully");

        } catch (Exception e) {
            log.error("Error deleting task type with ID {}: {}", id, e.getMessage(), e);
            return ApiResponseCustom.error("Failed to delete task type: " + e.getMessage());
        }
    }

    /**
     * Convert TaskType entity to DTO
     */
    private TaskTypeDTO convertToDTO(TaskType taskType) {
        return TaskTypeDTO.builder()
                .id(taskType.getId())
                .name(taskType.getName())
                .build();
    }
}
