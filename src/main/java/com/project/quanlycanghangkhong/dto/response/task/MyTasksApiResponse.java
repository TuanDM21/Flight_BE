package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.MyTasksData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper specifically for /my tasks endpoint")
public class MyTasksApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "My tasks data containing list and pagination info")
	private MyTasksData data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static MyTasksApiResponse success(MyTasksData data) {
		return MyTasksApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static MyTasksApiResponse error(String message, int statusCode) {
		return MyTasksApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
