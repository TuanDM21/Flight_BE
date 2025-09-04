package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for task detail")
public class TaskDetailApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "Task detail data")
	private TaskDetailDTO data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static TaskDetailApiResponse success(TaskDetailDTO data) {
		return TaskDetailApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static TaskDetailApiResponse error(String message, int statusCode) {
		return TaskDetailApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
