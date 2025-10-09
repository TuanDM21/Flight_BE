package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.request.TaskReportRequest;
import com.project.quanlycanghangkhong.dto.response.TaskReportResponse;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

public interface TaskReportService {
    
    /**
     * Tạo báo cáo Task/Assignment theo điều kiện lọc
     */
    ApiResponseCustom<TaskReportResponse> generateReport(TaskReportRequest request);
}
