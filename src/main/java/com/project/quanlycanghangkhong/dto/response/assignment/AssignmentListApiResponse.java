package com.project.quanlycanghangkhong.dto.response.assignment;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
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
@Schema(description = "API response wrapper for assignment list operations")
public class AssignmentListApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of assignments")
	private List<AssignmentDTO> data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static AssignmentListApiResponse success(List<AssignmentDTO> data) {
		return AssignmentListApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static AssignmentListApiResponse created(List<AssignmentDTO> data) {
		return AssignmentListApiResponse.builder()
				.message("Đã tạo thành công")
				.statusCode(201)
				.data(data)
				.success(true)
				.build();
	}

	public static AssignmentListApiResponse error(String message, int statusCode) {
		return AssignmentListApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
