package com.project.quanlycanghangkhong.dto.response.fileshare;

import com.project.quanlycanghangkhong.dto.FileShareDTO;
import java.util.List;

public class ApiFileShareListResponse {
    private String message;
    private int statusCode;
    private List<FileShareDTO> data;
    private boolean success;

    // Constructors
    public ApiFileShareListResponse() {}

    public ApiFileShareListResponse(String message, int statusCode, List<FileShareDTO> data, boolean success) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
        this.success = success;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<FileShareDTO> getData() {
        return data;
    }

    public void setData(List<FileShareDTO> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}