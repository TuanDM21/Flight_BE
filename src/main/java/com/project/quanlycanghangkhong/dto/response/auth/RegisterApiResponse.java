package com.project.quanlycanghangkhong.dto.response.auth;

import com.project.quanlycanghangkhong.dto.RegisterDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for registration operations")
public class RegisterApiResponse {

	@Schema(description = "Response message", example = "Đăng ký thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "201")
	private int statusCode;

	@Schema(description = "Registration data")
	private RegisterDTO data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static RegisterApiResponse success(RegisterDTO data) {
		return RegisterApiResponse.builder()
				.message("Đăng ký thành công")
				.statusCode(201)
				.data(data)
				.success(true)
				.build();
	}

	public static RegisterApiResponse created(RegisterDTO data) {
		return RegisterApiResponse.builder()
				.message("Đăng ký thành công")
				.statusCode(201)
				.data(data)
				.success(true)
				.build();
	}

	public static RegisterApiResponse error(String message, int statusCode) {
		return RegisterApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}