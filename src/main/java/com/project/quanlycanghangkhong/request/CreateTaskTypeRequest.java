package com.project.quanlycanghangkhong.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CreateTaskTypeRequest - Request DTO để tạo TaskType mới
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create a new task type")
public class CreateTaskTypeRequest {

    @Schema(description = "Task type name", example = "Bảo trì", required = true)
    @NotBlank(message = "Task type name is required")
    @Size(max = 100, message = "Task type name must be less than 100 characters")
    private String name;
}
