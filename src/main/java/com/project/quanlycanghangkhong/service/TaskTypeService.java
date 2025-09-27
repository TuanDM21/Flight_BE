package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.TaskTypeDTO;
import com.project.quanlycanghangkhong.request.CreateTaskTypeRequest;
import com.project.quanlycanghangkhong.request.UpdateTaskTypeRequest;

import java.util.List;

/**
 * TaskTypeService - Service interface cho quản lý TaskType
 */
public interface TaskTypeService {

    /**
     * Tạo TaskType mới
     */
    ApiResponseCustom<TaskTypeDTO> createTaskType(CreateTaskTypeRequest request);

    /**
     * Lấy tất cả TaskType
     */
    ApiResponseCustom<List<TaskTypeDTO>> getAllTaskTypes();

    /**
     * Lấy TaskType theo ID
     */
    ApiResponseCustom<TaskTypeDTO> getTaskTypeById(Integer id);

    /**
     * Cập nhật TaskType
     */
    ApiResponseCustom<TaskTypeDTO> updateTaskType(Integer id, UpdateTaskTypeRequest request);

    /**
     * Xóa TaskType
     */
    ApiResponseCustom<String> deleteTaskType(Integer id);
}
