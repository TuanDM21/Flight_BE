package com.project.quanlycanghangkhong.config;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class VietnamTimestampListener {
    
    private static final ZoneId VIETNAM_TIMEZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    
    @PrePersist
    public void setCreatedAt(Object entity) {
        setTimestamp(entity, "createdAt");
        setTimestamp(entity, "updatedAt");
    }
    
    @PreUpdate
    public void setUpdatedAt(Object entity) {
        setTimestamp(entity, "updatedAt");
    }
    
    private void setTimestamp(Object entity, String fieldName) {
        try {
            Field field = findField(entity.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                // Lấy thời gian hiện tại theo timezone Vietnam
                LocalDateTime vietnamTime = ZonedDateTime.now(VIETNAM_TIMEZONE).toLocalDateTime();
                field.set(entity, vietnamTime);
                System.out.println("✅ Set " + fieldName + " to Vietnam time: " + vietnamTime);
            }
        } catch (Exception e) {
            System.err.println("❌ Error setting " + fieldName + ": " + e.getMessage());
        }
    }
    
    private Field findField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), fieldName);
            }
            return null;
        }
    }
}