package com.project.quanlycanghangkhong.dto.response.user;

import com.project.quanlycanghangkhong.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for current user operations")
public class CurrentUserApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "Current user data")
	private UserDTO data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static CurrentUserApiResponse success(UserDTO data) {
		return CurrentUserApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static CurrentUserApiResponse error(String message, int statusCode) {
		return CurrentUserApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
