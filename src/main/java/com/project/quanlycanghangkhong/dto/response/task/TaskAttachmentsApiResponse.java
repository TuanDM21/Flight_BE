package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "API response for task attachments")
public class TaskAttachmentsApiResponse {
	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of task attachments")
	private List<AttachmentDTO> data;

	@Schema(description = "Success flag", example = "true")
	private boolean success;

	public TaskAttachmentsApiResponse(String message, int statusCode, List<AttachmentDTO> data, boolean success) {
		this.message = message;
		this.statusCode = statusCode;
		this.data = data;
		this.success = success;
	}

	public static TaskAttachmentsApiResponse success(List<AttachmentDTO> data) {
		return new TaskAttachmentsApiResponse("Thành công", 200, data, true);
	}

	public static TaskAttachmentsApiResponse error(String message, int statusCode) {
		return new TaskAttachmentsApiResponse(message, statusCode, null, false);
	}
}
