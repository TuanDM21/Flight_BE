package com.project.quanlycanghangkhong.dto.response.report;

import com.project.quanlycanghangkhong.model.ReportType;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Response wrapper for report types API
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Response wrapper cho danh sách loại báo cáo")
public class ReportTypesApiResponse extends ApiResponseCustom<List<ReportType>> {
    
    @Schema(description = "Danh sách các loại báo cáo có sẵn", 
            example = "[\"TASK_REPORT\", \"RECIPIENT_PERFORMANCE_REPORT\", \"ASSIGNMENT_TRACKING_REPORT\", \"TASK_STATUS_REPORT\", \"TEAM_WORKLOAD_REPORT\", \"OVERDUE_ANALYSIS_REPORT\"]")
    private List<ReportType> data;
    
    public ReportTypesApiResponse() {
        super();
    }
    
    public ReportTypesApiResponse(int statusCode, String message, List<ReportType> data, boolean success) {
        super();
        this.setStatusCode(statusCode);
        this.setMessage(message);
        this.setData(data);
        this.setSuccess(success);
        this.data = data;
    }
}
