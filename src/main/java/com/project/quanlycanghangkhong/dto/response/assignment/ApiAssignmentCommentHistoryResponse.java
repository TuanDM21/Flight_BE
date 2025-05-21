package com.project.quanlycanghangkhong.dto.response.assignment;

import com.project.quanlycanghangkhong.dto.AssignmentCommentHistoryDTO;
import java.util.List;

public class ApiAssignmentCommentHistoryResponse {
    private String message;
    private int statusCode;
    private List<AssignmentCommentHistoryDTO> data;
    private boolean success;

    public ApiAssignmentCommentHistoryResponse(String message, int statusCode, List<AssignmentCommentHistoryDTO> data, boolean success) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
        this.success = success;
    }
    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public List<AssignmentCommentHistoryDTO> getData() { return data; }
    public void setData(List<AssignmentCommentHistoryDTO> data) { this.data = data; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
