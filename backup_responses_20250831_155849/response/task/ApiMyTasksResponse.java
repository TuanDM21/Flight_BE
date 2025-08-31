package com.project.quanlycanghangkhong.dto.response.task;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response for my tasks with standardized structure", required = true)
public class ApiMyTasksResponse extends ApiResponseCustom<MyTasksData> {
    
    public ApiMyTasksResponse(String message, int statusCode, MyTasksData data, boolean success) {
        super(message, statusCode, data, success);
    }
    
    /**
     * Create success response with my tasks data
     */
    public static ApiMyTasksResponse success(MyTasksData data) {
        return new ApiMyTasksResponse("Thành công", 200, data, true);
    }
    
    /**
     * Create success response with custom message
     */
    public static ApiMyTasksResponse success(String message, MyTasksData data) {
        return new ApiMyTasksResponse(message, 200, data, true);
    }
    
    /**
     * Create error response
     */
    public static ApiMyTasksResponse error(String message, int statusCode) {
        return new ApiMyTasksResponse(message, statusCode, null, false);
    }
}
