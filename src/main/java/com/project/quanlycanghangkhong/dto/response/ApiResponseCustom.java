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
@Schema(name = "ApiResponseCustom", description = "Response chuẩn cho tất cả API", example = """
{
  "message": "Thành công",
  "statusCode": 200,
  "data": {
    "id": 101,
    "name": "Họp định kỳ tuần",
    "location": "Phòng họp A1",
    "startTime": "2025-10-01T13:59:01.290Z",
    "endTime": "2025-10-01T15:00:00.000Z",
    "notes": "Thảo luận về kế hoạch Q4",
    "participants": [
      {
        "id": 201,
        "participantType": "USER",
        "participantId": 1,
        "participantName": "Nguyễn Văn A"
      },
      {
        "id": 202,
        "participantType": "USER",
        "participantId": 2,
        "participantName": "Trần Thị B"
      },
      {
        "id": 203,
        "participantType": "USER",
        "participantId": 3,
        "participantName": "Lê Văn C"
      }
    ],
    "createdAt": "2025-10-01T14:00:00.000Z",
    "updatedAt": "2025-10-01T14:00:00.000Z",
    "pinned": false
  },
  "success": true
}
""")
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

	public static <T> ApiResponseCustom<T> error(String message) {
		return error(HttpStatus.BAD_REQUEST, message);
	}

	public static <T> ApiResponseCustom<T> notFound(String message) {
		return error(HttpStatus.NOT_FOUND, message);
	}

	public static <T> ApiResponseCustom<T> forbidden(String message) {
		return error(HttpStatus.FORBIDDEN, message);
	}

	public static <T> ApiResponseCustom<T> unauthorized(String message) {
		return error(HttpStatus.UNAUTHORIZED, message);
	}

	public static <T> ApiResponseCustom<T> internalError(String message) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
	}

	// Convenience methods for common responses
	public static <T> ApiResponseCustom<T> deleted() {
		return success("Xóa thành công", null);
	}

	public static <T> ApiResponseCustom<T> updated(T data) {
		return success("Cập nhật thành công", data);
	}
}
