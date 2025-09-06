package com.project.quanlycanghangkhong.dto.response.attachment;

import com.project.quanlycanghangkhong.dto.FlexiblePreSignedUrlDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for pre-signed URL generation")
public class PreSignedUrlApiResponse {

	@Schema(description = "Response message", example = "Tạo pre-signed URL thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "Pre-signed URL data")
	private FlexiblePreSignedUrlDTO data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static PreSignedUrlApiResponse success(FlexiblePreSignedUrlDTO data) {
		return PreSignedUrlApiResponse.builder()
				.message("Tạo pre-signed URL thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static PreSignedUrlApiResponse success(String message, FlexiblePreSignedUrlDTO data) {
		return PreSignedUrlApiResponse.builder()
				.message(message)
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static PreSignedUrlApiResponse error(String message, int statusCode) {
		return PreSignedUrlApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
