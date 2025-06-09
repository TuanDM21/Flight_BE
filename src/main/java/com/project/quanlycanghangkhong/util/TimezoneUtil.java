package com.project.quanlycanghangkhong.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimezoneUtil {
    
    private static final ZoneId VIETNAM_TIMEZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    
    /**
     * Chuyển LocalDateTime thành ZonedDateTime với timezone Vietnam
     */
    public static ZonedDateTime toVietnamTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.atZone(VIETNAM_TIMEZONE);
    }
    
    /**
     * Lấy thời gian hiện tại theo timezone Vietnam
     */
    public static ZonedDateTime nowInVietnam() {
        return ZonedDateTime.now(VIETNAM_TIMEZONE);
    }
    
    /**
     * Convert ZonedDateTime to String with timezone info
     */
    public static String formatWithTimezone(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) return null;
        return zonedDateTime.format(ISO_FORMATTER);
    }
    
    /**
     * Convert LocalDateTime to String with Vietnam timezone info
     */
    public static String formatLocalDateTimeWithVietnamTimezone(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        ZonedDateTime vietnamTime = toVietnamTime(localDateTime);
        return formatWithTimezone(vietnamTime);
    }
}