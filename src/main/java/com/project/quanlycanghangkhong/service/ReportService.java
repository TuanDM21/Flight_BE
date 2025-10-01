package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.request.ReportRequest;
import com.project.quanlycanghangkhong.dto.response.ReportFieldsResponse;
import com.project.quanlycanghangkhong.dto.response.ReportResponse;
import com.project.quanlycanghangkhong.model.ReportType;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

import java.util.List;

/**
 * Service interface cho hệ thống báo cáo
 */
public interface ReportService {

    /**
     * Lấy danh sách các loại báo cáo có sẵn
     */
    ApiResponseCustom<List<ReportType>> getAvailableReportTypes();

    /**
     * Lấy danh sách fields có thể chọn cho một loại báo cáo cụ thể
     */
    ApiResponseCustom<ReportFieldsResponse> getReportFields(ReportType reportType);

    /**
     * Tạo báo cáo dựa trên request
     */
    ApiResponseCustom<ReportResponse> generateReport(ReportRequest request);

    /**
     * Export báo cáo ra file Excel (legacy - từ request)
     */
    ApiResponseCustom<byte[]> exportReportToExcel(ReportRequest request);

    /**
     * Export báo cáo ra file PDF (legacy - từ request)
     */
    ApiResponseCustom<byte[]> exportReportToPdf(ReportRequest request);

    /**
     * Export báo cáo ra file CSV (legacy - từ request)
     */
    ApiResponseCustom<byte[]> exportReportToCsv(ReportRequest request);

    /**
     * Export báo cáo ra file Word (legacy - từ request)
     */
    ApiResponseCustom<byte[]> exportReportToWord(ReportRequest request);

    /**
     * Export data báo cáo có sẵn ra file Excel
     */
    byte[] generateExcelFile(ReportResponse reportData) throws Exception;

    /**
     * Export data báo cáo có sẵn ra file PDF
     */
    byte[] generatePdfFile(ReportResponse reportData) throws Exception;

    /**
     * Export data báo cáo có sẵn ra file CSV
     */
    byte[] generateCsvFile(ReportResponse reportData) throws Exception;

    /**
     * Export data báo cáo có sẵn ra file Word (.docx)
     */
    byte[] generateWordFile(ReportResponse reportData) throws Exception;

    /**
     * Export data báo cáo có sẵn ra file PowerPoint (.pptx)
     */
    byte[] generatePowerPointFile(ReportResponse reportData) throws Exception;
}
