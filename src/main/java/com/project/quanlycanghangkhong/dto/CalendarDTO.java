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
}
