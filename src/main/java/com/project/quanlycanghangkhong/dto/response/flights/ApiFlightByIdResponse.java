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
@Schema(description = "API response for get flight by ID, data is FlightDTO", required = true)
public class ApiFlightByIdResponse extends ApiResponseCustom<FlightDTO> {
    public ApiFlightByIdResponse(String message, int statusCode, FlightDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}