package com.project.quanlycanghangkhong.dto.response.report;

import com.project.quanlycanghangkhong.model.ReportType;
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
@Schema(description = "API response wrapper for report types")
public class ReportTypesApiResponse {

	@Schema(description = "Response message", example = "Lấy danh sách loại báo cáo thành công")
	private String message;

	@Schema(description = "HTTP status code", example = "200")
	private int statusCode;

	@Schema(description = "List of available report types", 
			example = "[\"TASK_REPORT\", \"RECIPIENT_PERFORMANCE_REPORT\", \"ASSIGNMENT_TRACKING_REPORT\", \"TASK_STATUS_REPORT\", \"TEAM_WORKLOAD_REPORT\", \"OVERDUE_ANALYSIS_REPORT\"]")
	private List<ReportType> data;

	@Schema(description = "Success status", example = "true")
	private boolean success;
}
