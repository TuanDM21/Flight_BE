package com.project.quanlycanghangkhong.dto.response.attachment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for download URL generation")
public class DownloadUrlApiResponse {

	@Schema(description = "Response message", example = "Tạo download URL thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "Download URL string", example = "https://storage.blob.core.windows.net/container/file.pdf?signature=...")
	private String data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static DownloadUrlApiResponse success(String downloadUrl) {
		return DownloadUrlApiResponse.builder()
				.message("Tạo download URL thành công")
				.statusCode(200)
				.data(downloadUrl)
				.success(true)
				.build();
	}

	public static DownloadUrlApiResponse success(String message, String downloadUrl) {
		return DownloadUrlApiResponse.builder()
				.message(message)
				.statusCode(200)
				.data(downloadUrl)
				.success(true)
				.build();
	}

	public static DownloadUrlApiResponse error(String message, int statusCode) {
		return DownloadUrlApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
