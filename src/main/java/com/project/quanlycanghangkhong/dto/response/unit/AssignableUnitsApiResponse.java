package com.project.quanlycanghangkhong.dto.response.unit;

import com.project.quanlycanghangkhong.dto.UnitDTO;
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
@Schema(description = "API response wrapper for assignable units operations")
public class AssignableUnitsApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of assignable units for current user")
	private List<UnitDTO> data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static AssignableUnitsApiResponse success(List<UnitDTO> data) {
		return AssignableUnitsApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static AssignableUnitsApiResponse error(String message, int statusCode) {
		return AssignableUnitsApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
