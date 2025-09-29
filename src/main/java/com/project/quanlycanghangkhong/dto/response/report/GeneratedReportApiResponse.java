package com.project.quanlycanghangkhong.dto.response.report;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.response.ReportResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response wrapper for generated report API
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Response wrapper cho báo cáo đã tạo")
public class GeneratedReportApiResponse extends ApiResponseCustom<ReportResponse> {
    
    @Schema(description = "Dữ liệu báo cáo với thống kê phần trăm và nhóm")
    private ReportResponse data;
    
    public GeneratedReportApiResponse() {
        super();
    }
    
    public GeneratedReportApiResponse(int statusCode, String message, ReportResponse data, boolean success) {
        super();
        this.setStatusCode(statusCode);
        this.setMessage(message);
        this.setData(data);
        this.setSuccess(success);
        this.data = data;
    }
}
