package com.project.quanlycanghangkhong.dto.response.report;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.response.ReportFieldsResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response wrapper for report fields API
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Response wrapper cho danh sách fields báo cáo")
public class ReportFieldsApiResponse extends ApiResponseCustom<ReportFieldsResponse> {
    
    @Schema(description = "Thông tin về các fields có thể chọn cho báo cáo")
    private ReportFieldsResponse data;
    
    public ReportFieldsApiResponse() {
        super();
    }
    
    public ReportFieldsApiResponse(int statusCode, String message, ReportFieldsResponse data, boolean success) {
        super();
        this.setStatusCode(statusCode);
        this.setMessage(message);
        this.setData(data);
        this.setSuccess(success);
        this.data = data;
    }
}
