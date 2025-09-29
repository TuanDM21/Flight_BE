package com.project.quanlycanghangkhong.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;

/**
 * UpdateTaskTypeRequest - Request DTO để cập nhật TaskType
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update an existing task type")
public class UpdateTaskTypeRequest {

    @Schema(description = "Task type name", example = "Bảo trì")
    @Size(max = 100, message = "Task type name must be less than 100 characters")
    private String name;
}
