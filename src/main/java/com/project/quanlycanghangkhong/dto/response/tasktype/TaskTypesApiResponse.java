package com.project.quanlycanghangkhong.dto.response.tasktype;

import com.project.quanlycanghangkhong.dto.TaskTypeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for task types list operations")
public class TaskTypesApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of task types")
	private List<TaskTypeDTO> data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static TaskTypesApiResponse success(List<TaskTypeDTO> data) {
		return TaskTypesApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static TaskTypesApiResponse error(String message, int statusCode) {
		return TaskTypesApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
