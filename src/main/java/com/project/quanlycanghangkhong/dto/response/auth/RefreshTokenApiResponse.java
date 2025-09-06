package com.project.quanlycanghangkhong.dto.response.auth;

import com.project.quanlycanghangkhong.dto.LoginDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for refresh token operations")
public class RefreshTokenApiResponse {

	@Schema(description = "Response message", example = "Token đã được làm mới")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "New token data containing refreshed JWT and user info")
	private LoginDTO data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static RefreshTokenApiResponse success(LoginDTO data) {
		return RefreshTokenApiResponse.builder()
				.message("Token đã được làm mới")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static RefreshTokenApiResponse error(String message, int statusCode) {
		return RefreshTokenApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}