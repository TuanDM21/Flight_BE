package com.project.quanlycanghangkhong.dto.response.assignment;

import com.project.quanlycanghangkhong.dto.AssignmentCommentHistoryDTO;
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
@Schema(description = "API response wrapper for assignment comments")
public class AssignmentCommentsApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of assignment comments")
	private List<AssignmentCommentHistoryDTO> data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static AssignmentCommentsApiResponse success(List<AssignmentCommentHistoryDTO> data) {
		return AssignmentCommentsApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static AssignmentCommentsApiResponse error(String message, int statusCode) {
		return AssignmentCommentsApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
