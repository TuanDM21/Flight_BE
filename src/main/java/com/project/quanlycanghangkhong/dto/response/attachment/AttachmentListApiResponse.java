package com.project.quanlycanghangkhong.dto.response.attachment;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
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
@Schema(description = "API response wrapper for attachment list operations")
public class AttachmentListApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of attachments")
	private List<AttachmentDTO> data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static AttachmentListApiResponse success(List<AttachmentDTO> data) {
		return AttachmentListApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static AttachmentListApiResponse success(String message, List<AttachmentDTO> data) {
		return AttachmentListApiResponse.builder()
				.message(message)
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static AttachmentListApiResponse error(String message, int statusCode) {
		return AttachmentListApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
