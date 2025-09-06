package com.project.quanlycanghangkhong.dto.response.user;

import com.project.quanlycanghangkhong.dto.UserDTO;
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
@Schema(description = "API response wrapper for user list operations")
public class UserListApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of users")
	private List<UserDTO> data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static UserListApiResponse success(List<UserDTO> data) {
		return UserListApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static UserListApiResponse error(String message, int statusCode) {
		return UserListApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
