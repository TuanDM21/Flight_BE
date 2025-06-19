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
@Schema(description = "API response for single airport by ID, data is AirportDTO", required = true)
public class ApiAirportByIdResponse extends ApiResponseCustom<AirportDTO> {
    public ApiAirportByIdResponse(String message, int statusCode, AirportDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}