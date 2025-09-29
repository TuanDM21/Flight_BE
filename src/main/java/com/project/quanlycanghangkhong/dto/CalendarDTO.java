package com.project.quanlycanghangkhong.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Calendar view với activities theo ngày")
public class CalendarDTO {
    
    @Schema(description = "Ngày hiện tại của calendar", example = "2025-09-27")
    private LocalDate currentDate;
    
    @Schema(description = "Danh sách activities trong calendar view")
    private List<ActivityDTO> activities;
    
    @Schema(description = "Thông tin tổng quan về calendar")
    private CalendarMetadata metadata;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Metadata của calendar")
    public static class CalendarMetadata {
        
        @Schema(description = "Tổng số activities", example = "7")
        private int totalActivities;
        
        @Schema(description = "Loại calendar view", example = "company", allowableValues = {"company", "my", "empty"})
        private String viewType;
        
        @Schema(description = "Thông báo cho calendar view", example = "Lịch trống - không có hoạt động nào")
        private String message;
    }
}
