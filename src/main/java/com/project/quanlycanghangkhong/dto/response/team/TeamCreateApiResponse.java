package com.project.quanlycanghangkhong.dto.response.team;

import com.project.quanlycanghangkhong.dto.TeamDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for team creation operations")
public class TeamCreateApiResponse {

	@Schema(description = "Response message", example = "Tạo team thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "201")
	private int statusCode;

	@Schema(description = "Created team data")
	private TeamDTO data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static TeamCreateApiResponse success(TeamDTO data) {
		return TeamCreateApiResponse.builder()
				.message("Tạo team thành công")
				.statusCode(201)
				.data(data)
				.success(true)
				.build();
	}

	public static TeamCreateApiResponse created(TeamDTO data) {
		return TeamCreateApiResponse.builder()
				.message("Tạo team thành công")
				.statusCode(201)
				.data(data)
				.success(true)
				.build();
	}

	public static TeamCreateApiResponse error(String message, int statusCode) {
		return TeamCreateApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
