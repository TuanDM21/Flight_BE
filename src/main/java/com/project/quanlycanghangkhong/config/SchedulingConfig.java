package com.project.quanlycanghangkhong.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Cấu hình cho Spring Scheduling
 * Enables scheduled tasks như cập nhật overdue status
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Spring Boot sẽ tự động tìm và chạy các @Scheduled methods
}
