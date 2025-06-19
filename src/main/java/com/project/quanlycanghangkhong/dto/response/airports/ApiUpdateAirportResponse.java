package com.project.quanlycanghangkhong.dto.response.airports;

import com.project.quanlycanghangkhong.dto.AirportDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for update airport, data is AirportDTO", required = true)
public class ApiUpdateAirportResponse extends ApiResponseCustom<AirportDTO> {
    public ApiUpdateAirportResponse(String message, int statusCode, AirportDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}