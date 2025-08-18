package com.project.quanlycanghangkhong.model;

public enum TaskStatus {
    OPEN,         // 📝 Chưa có ai làm
    IN_PROGRESS,  // ⚡ Có ít nhất 1 assignment đang WORKING  
    COMPLETED,    // ✅ Tất cả assignments đều DONE
    OVERDUE       // ⏰ Trễ hạn - có assignment nào đó quá hạn mà chưa hoàn thành
}
