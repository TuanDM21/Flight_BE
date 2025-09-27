package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * TaskTypeRepository - Repository cho quản lý TaskType
 * Cung cấp các phương thức truy vấn dữ liệu cho TaskType
 */
@Repository
public interface TaskTypeRepository extends JpaRepository<TaskType, Integer> {

    /**
     * Tìm TaskType theo tên (case-insensitive)
     */
    Optional<TaskType> findByNameIgnoreCase(String name);

    /**
     * Kiểm tra xem TaskType có đang được sử dụng hay không
     */
    @Query("SELECT COUNT(t) > 0 FROM Task t WHERE t.taskType.id = :taskTypeId")
    boolean isTaskTypeInUse(Integer taskTypeId);
}
