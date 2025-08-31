package com.project.quanlycanghangkhong.dto.response.flights;

import com.project.quanlycanghangkhong.dto.FlightDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for create flight, data is FlightDTO", required = true)
public class ApiCreateFlightResponse extends ApiResponseCustom<FlightDTO> {
    public ApiCreateFlightResponse(String message, int statusCode, FlightDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}