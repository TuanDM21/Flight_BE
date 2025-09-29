package com.project.quanlycanghangkhong.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TaskTypeDTO - Data Transfer Object cho TaskType
 * Sử dụng để truyền dữ liệu TaskType qua API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Task Type Data Transfer Object", example = """
{
  "id": 1,
  "name": "Bảo trì"
}
""")
public class TaskTypeDTO {

    @Schema(description = "Task type ID", example = "1")
    private Integer id;

    @Schema(description = "Task type name", example = "Bảo trì", required = true)
    private String name;
}
