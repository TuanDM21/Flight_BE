package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.TaskDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for task creation/update operations")
public class TaskApiResponse {

	@Schema(description = "Response message", example = "Đã tạo thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "201")
	private int statusCode;

	@Schema(description = "Task data")
	private TaskDTO data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static TaskApiResponse success(TaskDTO data) {
		return TaskApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static TaskApiResponse created(TaskDTO data) {
		return TaskApiResponse.builder()
				.message("Đã tạo thành công")
				.statusCode(201)
				.data(data)
				.success(true)
				.build();
	}

	public static TaskApiResponse error(String message, int statusCode) {
		return TaskApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
