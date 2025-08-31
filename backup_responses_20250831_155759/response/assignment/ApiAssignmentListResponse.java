package com.project.quanlycanghangkhong.dto.response.assignment;

import com.project.quanlycanghangkhong.dto.AssignmentDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for list of assignments", required = true)
public class ApiAssignmentListResponse extends ApiResponseCustom<List<AssignmentDTO>> {
    public ApiAssignmentListResponse(String message, int statusCode, List<AssignmentDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
