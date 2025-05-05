package com.project.quanlycanghangkhong.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiResponseSuccess", description = "Response cho các trường hợp thành công")
public class ApiResponseSuccess<T> {
	@Schema(description = "Thông báo kết quả", example = "Thành công")
	private String message;

	@Schema(description = "Mã trạng thái HTTP", example = "200")
	private int statusCode;

	@Schema(description = "Dữ liệu trả về (object, list hoặc null)")
	private T data;

	@Schema(description = "Trạng thái thành công hay thất bại", example = "true")
	private boolean success;

	// Getter, Setter, Constructor
	public ApiResponseSuccess() {
	}

	public ApiResponseSuccess(String message, int statusCode, T data, boolean success) {
		this.message = message;
		this.statusCode = statusCode;
		this.data = data;
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
