package com.project.quanlycanghangkhong.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Response DTO cho báo cáo với số liệu thống kê và phần trăm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response chứa dữ liệu báo cáo với số liệu thống kê")
public class ReportResponse {

    @Schema(description = "Loại báo cáo")
    private String reportType;

    @Schema(description = "Tên báo cáo")
    private String reportName;

    @Schema(description = "Thống kê tổng quan")
    private ReportSummary summary;

    @Schema(description = "Cấu trúc columns của bảng")
    private List<ReportColumn> columns;

    @Schema(description = "Dữ liệu các nhóm (grouped data)")
    private List<ReportGroup> groups;

    @Schema(description = "Thống kê phần trăm")
    private Map<String, BigDecimal> percentageStats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Thống kê tổng quan của báo cáo")
    public static class ReportSummary {
        
        @Schema(description = "Tổng số records")
        private Integer totalRecords;

        @Schema(description = "Tổng số nhóm")
        private Integer totalGroups;

        @Schema(description = "Ngày tạo báo cáo")
        private String generatedAt;

        @Schema(description = "Thời gian xử lý (ms)")
        private Long processingTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Định nghĩa column trong bảng báo cáo")
    public static class ReportColumn {
        
        @Schema(description = "Tên field")
        private String field;

        @Schema(description = "Label hiển thị")
        private String label;

        @Schema(description = "Kiểu dữ liệu")
        private String dataType;

        @Schema(description = "Có thể sort được không")
        private Boolean sortable;

        @Schema(description = "Width mặc định")
        private String width;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Nhóm dữ liệu trong báo cáo")
    public static class ReportGroup {
        
        @Schema(description = "Tên nhóm")
        private String groupName;

        @Schema(description = "Mô tả nhóm")
        private String groupDescription;

        @Schema(description = "Số lượng items trong nhóm")
        private Integer itemCount;

        @Schema(description = "Phần trăm so với tổng")
        private BigDecimal percentage;

        @Schema(description = "Thống kê chi tiết của nhóm")
        private Map<String, Object> groupStats;

        @Schema(description = "Dữ liệu chi tiết của nhóm")
        private List<Map<String, Object>> items;
    }
}
