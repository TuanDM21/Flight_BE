package com.project.quanlycanghangkhong.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * Response DTO cho danh sách các fields có thể chọn trong báo cáo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Danh sách fields có thể chọn cho từng loại báo cáo")
public class ReportFieldsResponse {

    @Schema(description = "Loại báo cáo")
    private String reportType;

    @Schema(description = "Tên báo cáo")
    private String reportName;

    @Schema(description = "Fields mặc định (luôn hiển thị)")
    private List<FieldDefinition> defaultFields;

    @Schema(description = "Fields động (có thể chọn)")
    private List<FieldCategory> dynamicFields;

    @Schema(description = "Options cho filters")
    private FilterOptions filterOptions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Định nghĩa một field")
    public static class FieldDefinition {
        
        @Schema(description = "Tên field")
        private String field;

        @Schema(description = "Label hiển thị")
        private String label;

        @Schema(description = "Bảng nguồn")
        private String table;

        @Schema(description = "Kiểu dữ liệu")
        private String dataType;

        @Schema(description = "Field bắt buộc")
        private Boolean required;

        @Schema(description = "Được chọn mặc định")
        private Boolean defaultSelected;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Nhóm fields theo category")
    public static class FieldCategory {
        
        @Schema(description = "Tên category")
        private String category;

        @Schema(description = "Mô tả category")
        private String description;

        @Schema(description = "Danh sách fields trong category")
        private List<FieldDefinition> fields;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Options cho các bộ lọc")
    public static class FilterOptions {
        
        @Schema(description = "Danh sách trạng thái có thể chọn")
        private List<FilterOption> statuses;

        @Schema(description = "Danh sách độ ưu tiên có thể chọn")
        private List<FilterOption> priorities;

        @Schema(description = "Danh sách TaskTypes có thể chọn")
        private List<FilterOption> taskTypes;

        @Schema(description = "Danh sách Teams có thể chọn")
        private List<FilterOption> teams;

        @Schema(description = "Danh sách Units có thể chọn")
        private List<FilterOption> units;

        @Schema(description = "Danh sách Users có thể chọn")
        private List<FilterOption> users;

        @Schema(description = "Danh sách RecipientTypes có thể chọn")
        private List<FilterOption> recipientTypes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Option cho filter")
    public static class FilterOption {
        
        @Schema(description = "Giá trị")
        private String value;

        @Schema(description = "Label hiển thị")
        private String label;

        @Schema(description = "Số lượng items")
        private Integer count;
    }
}
