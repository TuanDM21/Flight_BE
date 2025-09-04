package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.TaskTreeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "API response for task tree (hierarchical structure)")
public class TaskTreeApiResponse {
	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "Task tree data with hierarchical structure")
	private TaskTreeDTO data;

	@Schema(description = "Success flag", example = "true")
	private boolean success;

	public TaskTreeApiResponse(String message, int statusCode, TaskTreeDTO data, boolean success) {
		this.message = message;
		this.statusCode = statusCode;
		this.data = data;
		this.success = success;
	}

	public static TaskTreeApiResponse success(TaskTreeDTO data) {
		return new TaskTreeApiResponse("Thành công", 200, data, true);
	}

	public static TaskTreeApiResponse error(String message, int statusCode) {
		return new TaskTreeApiResponse(message, statusCode, null, false);
	}
}
