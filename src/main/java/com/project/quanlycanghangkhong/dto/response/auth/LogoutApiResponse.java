package com.project.quanlycanghangkhong.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for logout operations")
public class LogoutApiResponse {

	@Schema(description = "Response message", example = "Đăng xuất thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "Logout data (usually null)", nullable = true)
	private Void data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static LogoutApiResponse success() {
		return LogoutApiResponse.builder()
				.message("Đăng xuất thành công")
				.statusCode(200)
				.data(null)
				.success(true)
				.build();
	}

	public static LogoutApiResponse error(String message, int statusCode) {
		return LogoutApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}