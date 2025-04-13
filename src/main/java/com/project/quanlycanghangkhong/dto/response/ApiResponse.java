package com.project.quanlycanghangkhong.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
	private String message;
	private int statusCode;
	private T data;
	private boolean success;

	public ApiResponse(boolean success, String message) {
		this.success = success;
		this.message = message;
		this.statusCode = success ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value();
	}

	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder()
				.message("Thành công")
				.statusCode(HttpStatus.OK.value())
				.data(data)
				.success(true)
				.build();
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return ApiResponse.<T>builder()
				.message(message)
				.statusCode(HttpStatus.OK.value())
				.data(data)
				.success(true)
				.build();
	}

	public static <T> ApiResponse<T> created(T data) {
		return ApiResponse.<T>builder()
				.message("Đã tạo thành công")
				.statusCode(HttpStatus.CREATED.value())
				.data(data)
				.success(true)
				.build();
	}

	public static <T> ApiResponse<T> error(HttpStatus status, String message) {
		return ApiResponse.<T>builder()
				.message(message)
				.statusCode(status.value())
				.success(false)
				.build();
	}
}
