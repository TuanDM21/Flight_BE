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
@Schema(description = "API response wrapper for unit list operations")
public class UnitListApiResponse {

	@Schema(description = "Response message", example = "Thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of units (all units or filtered by teamId)")
	private List<UnitDTO> data;

	@Schema(description = "Success status", example = "true")
	private boolean success;

	public static UnitListApiResponse success(List<UnitDTO> data) {
		return UnitListApiResponse.builder()
				.message("Thành công")
				.statusCode(200)
				.data(data)
				.success(true)
				.build();
	}

	public static UnitListApiResponse error(String message, int statusCode) {
		return UnitListApiResponse.builder()
				.message(message)
				.statusCode(statusCode)
				.data(null)
				.success(false)
				.build();
	}
}
