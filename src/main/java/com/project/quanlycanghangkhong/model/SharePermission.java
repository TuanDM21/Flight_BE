package com.project.quanlycanghangkhong.model;

public enum SharePermission {
    READ_ONLY("Chỉ đọc"),
    READ_WRITE("Đọc và chỉnh sửa");
    
    private final String description;
    
    SharePermission(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}