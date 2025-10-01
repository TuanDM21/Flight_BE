package com.project.quanlycanghangkhong.dto.response.report;

import com.project.quanlycanghangkhong.dto.response.ReportResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API response wrapper for generated report")
public class GeneratedReportApiResponse {

	@Schema(description = "Response message", example = "Tạo báo cáo thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "Generated report data")
	private ReportResponse data;

	@Schema(description = "Success status", example = "true")
	private boolean success;
}
