package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.TaskSubtreeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "API response for task subtree (flat list)")
public class TaskSubtreeApiResponse {
	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of tasks in subtree")
	private List<TaskSubtreeDTO> data;

	@Schema(description = "Success flag", example = "true")
	private boolean success;

	public TaskSubtreeApiResponse(String message, int statusCode, List<TaskSubtreeDTO> data, boolean success) {
		this.message = message;
		this.statusCode = statusCode;
		this.data = data;
		this.success = success;
	}

	public static TaskSubtreeApiResponse success(List<TaskSubtreeDTO> data) {
		return new TaskSubtreeApiResponse("Thành công", 200, data, true);
	}

	public static TaskSubtreeApiResponse error(String message, int statusCode) {
		return new TaskSubtreeApiResponse(message, statusCode, null, false);
	}
}
