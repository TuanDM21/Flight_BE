package com.project.quanlycanghangkhong.dto.response.activity;

import com.project.quanlycanghangkhong.dto.CalendarDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper specifically for calendar activities endpoint")
public class CalendarApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "Calendar data containing activities and metadata")
	private CalendarDTO data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static CalendarApiResponse success(CalendarDTO data) {
		return CalendarApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static CalendarApiResponse error(String message, int statusCode) {
		return CalendarApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
