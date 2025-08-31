package com.project.quanlycanghangkhong.dto.response.units;

import com.project.quanlycanghangkhong.dto.UnitDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for all units, data is List<UnitDTO>", required = true)
public class ApiAllUnitsResponse extends ApiResponseCustom<List<UnitDTO>> {
}
