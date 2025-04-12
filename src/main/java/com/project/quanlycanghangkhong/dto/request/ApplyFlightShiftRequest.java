package com.project.quanlycanghangkhong.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFlightShiftRequest {
	private Integer flightId;
	private LocalDate shiftDate;
	private Integer userId;
}
