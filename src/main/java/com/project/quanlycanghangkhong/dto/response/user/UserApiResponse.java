package com.project.quanlycanghangkhong.dto.response.user;

import com.project.quanlycanghangkhong.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for single user operations")
public class UserApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "User data")
	private User data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static UserApiResponse success(User data) {
		return UserApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static UserApiResponse created(User data) {
		return UserApiResponse.builder()
				.message("Tạo user thành công")
				.statusCode(201)
				.data(data)
				.success(true)
				.build();
	}

	public static UserApiResponse updated(User data) {
		return UserApiResponse.builder()
				.message("Cập nhật user thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static UserApiResponse error(String message, int statusCode) {
		return UserApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
