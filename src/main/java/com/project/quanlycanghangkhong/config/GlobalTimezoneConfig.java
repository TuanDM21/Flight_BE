package com.project.quanlycanghangkhong.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import java.time.ZoneId;

@Configuration
public class GlobalTimezoneConfig {
    
    public static final String VIETNAM_TIMEZONE_ID = "Asia/Ho_Chi_Minh";
    public static final ZoneId VIETNAM_ZONE = ZoneId.of(VIETNAM_TIMEZONE_ID);
    public static final TimeZone VIETNAM_TIMEZONE = TimeZone.getTimeZone(VIETNAM_TIMEZONE_ID);
    
    @PostConstruct
    public void configureTimezone() {
        // Set default timezone cho toàn bộ JVM
        TimeZone.setDefault(VIETNAM_TIMEZONE);
        
        // Set system property để đảm bảo MySQL driver sử dụng đúng timezone
        System.setProperty("user.timezone", VIETNAM_TIMEZONE_ID);
        
        System.out.println("🌏 ✅ Global timezone configured: " + VIETNAM_TIMEZONE_ID);
        System.out.println("🕒 Current JVM timezone: " + TimeZone.getDefault().getID());
        System.out.println("🕒 Current system timezone: " + System.getProperty("user.timezone"));
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void verifyTimezoneOnStartup() {
        System.out.println("🚀 APPLICATION TIMEZONE VERIFICATION:");
        System.out.println("   ✅ JVM Default Timezone: " + TimeZone.getDefault().getID());
        System.out.println("   ✅ System Property Timezone: " + System.getProperty("user.timezone"));
        System.out.println("   ✅ ZoneId Default: " + ZoneId.systemDefault());
        
        if (!VIETNAM_TIMEZONE_ID.equals(TimeZone.getDefault().getID())) {
            System.err.println("❌ WARNING: JVM timezone is not set to " + VIETNAM_TIMEZONE_ID);
        } else {
            System.out.println("   🎉 All timezone configurations are correct!");
        }
    }
}