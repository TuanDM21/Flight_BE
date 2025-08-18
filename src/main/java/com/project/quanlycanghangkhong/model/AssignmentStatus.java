package com.project.quanlycanghangkhong.model;

public enum AssignmentStatus {
        WORKING,     // 🔨 Đang làm (DEFAULT khi vừa giao)
        DONE,        // ✅ Hoàn thành  
        CANCELLED,   // ❌ Đã hủy
        OVERDUE      // ⏰ Trễ hạn - đã quá dueAt mà vẫn chưa hoàn thành
}
