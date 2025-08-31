package com.project.quanlycanghangkhong.dto.response.flights;

import com.project.quanlycanghangkhong.dto.FlightDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for all flights, data is List<FlightDTO>", required = true)
public class ApiAllFlightsResponse extends ApiResponseCustom<List<FlightDTO>> {
    public ApiAllFlightsResponse(String message, int statusCode, List<FlightDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}