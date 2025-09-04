package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.TaskDetailDTO;
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
@Schema(description = "API response wrapper for task subtasks list")
public class TaskSubtasksApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of subtasks")
	private List<TaskDetailDTO> data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static TaskSubtasksApiResponse success(List<TaskDetailDTO> data) {
		return TaskSubtasksApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static TaskSubtasksApiResponse error(String message, int statusCode) {
		return TaskSubtasksApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
