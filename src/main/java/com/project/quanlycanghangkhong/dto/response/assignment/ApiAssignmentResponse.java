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
@Schema(description = "API response for a single assignment", required = true)
public class ApiAssignmentResponse extends ApiResponseCustom<AssignmentDTO> {
    public ApiAssignmentResponse(String message, int statusCode, AssignmentDTO data, boolean success) {
        super(message, statusCode, data, success);
    }
}
