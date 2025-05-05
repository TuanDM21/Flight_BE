package com.project.quanlycanghangkhong.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "ApiResponseCustom", description = "Response chuẩn cho tất cả API")
public class ApiResponseCustom<T> {
	@Schema(description = "Thông báo kết quả", example = "Thành công")
	private String message;

	@Schema(description = "Mã trạng thái HTTP", example = "200")
	private int statusCode;

	@Schema(description = "Dữ liệu trả về (object, list hoặc null). Kiểu thực tế phụ thuộc vào API cụ thể.")
	private T data;

	@Schema(description = "Trạng thái thành công hay thất bại", example = "true")
	private boolean success;

	public ApiResponseCustom(boolean success, String message) {
		this.success = success;
		this.message = message;
		this.statusCode = success ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value();
	}

	public static <T> ApiResponseCustom<T> success(T data) {
		return ApiResponseCustom.<T>builder()
				.message("Thành công")
				.statusCode(HttpStatus.OK.value())
				.data(data)
				.success(true)
				.build();
	}

	public static <T> ApiResponseCustom<T> success(String message, T data) {
		return ApiResponseCustom.<T>builder()
				.message(message)
				.statusCode(HttpStatus.OK.value())
				.data(data)
				.success(true)
				.build();
	}

	public static <T> ApiResponseCustom<T> created(T data) {
		return ApiResponseCustom.<T>builder()
				.message("Đã tạo thành công")
				.statusCode(HttpStatus.CREATED.value())
				.data(data)
				.success(true)
				.build();
	}

	public static <T> ApiResponseCustom<T> error(HttpStatus status, String message) {
		return ApiResponseCustom.<T>builder()
				.message(message)
				.statusCode(status.value())
				.success(false)
				.build();
	}
}
