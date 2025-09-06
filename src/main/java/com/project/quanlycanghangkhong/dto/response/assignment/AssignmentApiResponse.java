package com.project.quanlycanghangkhong.dto.response.assignment;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for assignment creation/update operations")
public class AssignmentApiResponse {

	@Schema(description = "Response message", example = "Đã tạo thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "201")
	private int statusCode;

	@Schema(description = "Assignment data")
	private AssignmentDTO data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static AssignmentApiResponse success(AssignmentDTO data) {
		return AssignmentApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static AssignmentApiResponse created(AssignmentDTO data) {
		return AssignmentApiResponse.builder()
				.message("Đã tạo thành công")
				.statusCode(201)
				.data(data)
				.success(true)
				.build();
	}

	public static AssignmentApiResponse error(String message, int statusCode) {
		return AssignmentApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
