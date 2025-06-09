package com.project.quanlycanghangkhong.config;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {
    
    @PostConstruct
    public void init() {
        // Set default timezone cho toàn bộ JVM
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        System.out.println("✅ Default timezone set to: " + TimeZone.getDefault().getID());
    }
}