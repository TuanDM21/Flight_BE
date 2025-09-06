package com.project.quanlycanghangkhong.dto.response.team;

import com.project.quanlycanghangkhong.dto.TeamDTO;
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
@Schema(description = "API response wrapper for assignable teams operations")
public class AssignableTeamsApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of assignable teams for current user")
	private List<TeamDTO> data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static AssignableTeamsApiResponse success(List<TeamDTO> data) {
		return AssignableTeamsApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static AssignableTeamsApiResponse error(String message, int statusCode) {
		return AssignableTeamsApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
