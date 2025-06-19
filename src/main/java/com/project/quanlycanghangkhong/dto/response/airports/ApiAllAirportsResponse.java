package com.project.quanlycanghangkhong.dto.response.airports;

import com.project.quanlycanghangkhong.dto.AirportDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for all airports, data is List<AirportDTO>", required = true)
public class ApiAllAirportsResponse extends ApiResponseCustom<List<AirportDTO>> {
    public ApiAllAirportsResponse(String message, int statusCode, List<AirportDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}