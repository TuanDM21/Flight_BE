package com.project.quanlycanghangkhong.dto;

public class ApiResponse<T> {
    private String message;
    private int statusCode;
    private T data;
    private boolean success;

    public ApiResponse() {}

    public ApiResponse(String message, int statusCode, T data, boolean success) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
        this.success = success;
    }

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
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
