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
@Schema(description = "API response for search flights, data is List<FlightDTO>", required = true)
public class ApiSearchFlightsResponse extends ApiResponseCustom<List<FlightDTO>> {
    public ApiSearchFlightsResponse(String message, int statusCode, List<FlightDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}