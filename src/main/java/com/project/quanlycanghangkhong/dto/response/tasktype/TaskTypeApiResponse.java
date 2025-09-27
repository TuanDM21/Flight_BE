package com.project.quanlycanghangkhong.dto.response.tasktype;

import com.project.quanlycanghangkhong.dto.TaskTypeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for task type creation/update operations")
public class TaskTypeApiResponse {

	@Schema(description = "Response message", example = "Đã tạo thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "201")
	private int statusCode;

	@Schema(description = "Task type data")
	private TaskTypeDTO data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static TaskTypeApiResponse success(TaskTypeDTO data) {
		return TaskTypeApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static TaskTypeApiResponse created(TaskTypeDTO data) {
		return TaskTypeApiResponse.builder()
				.message("Đã tạo thành công")
				.statusCode(201)
				.data(data)
				.success(true)
				.build();
	}

	public static TaskTypeApiResponse updated(TaskTypeDTO data) {
		return TaskTypeApiResponse.builder()
				.message("Cập nhật thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static TaskTypeApiResponse error(String message, int statusCode) {
		return TaskTypeApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
