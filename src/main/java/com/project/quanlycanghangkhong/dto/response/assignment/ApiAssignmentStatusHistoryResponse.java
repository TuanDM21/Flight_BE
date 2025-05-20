package com.project.quanlycanghangkhong.dto.response.assignment;

import com.project.quanlycanghangkhong.dto.AssignmentStatusHistoryDTO;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for assignment status history", required = true)
public class ApiAssignmentStatusHistoryResponse extends ApiResponseCustom<List<AssignmentStatusHistoryDTO>> {
    public ApiAssignmentStatusHistoryResponse(String message, int statusCode, List<AssignmentStatusHistoryDTO> data, boolean success) {
        super(message, statusCode, data, success);
    }
}
