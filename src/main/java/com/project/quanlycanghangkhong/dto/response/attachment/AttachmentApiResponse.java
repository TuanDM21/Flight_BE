package com.project.quanlycanghangkhong.dto.response.attachment;

import com.project.quanlycanghangkhong.dto.AttachmentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for single attachment operations")
public class AttachmentApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "Attachment data")
	private AttachmentDTO data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static AttachmentApiResponse success(AttachmentDTO data) {
		return AttachmentApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static AttachmentApiResponse success(String message, AttachmentDTO data) {
		return AttachmentApiResponse.builder()
				.message(message)
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static AttachmentApiResponse error(String message, int statusCode) {
		return AttachmentApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
