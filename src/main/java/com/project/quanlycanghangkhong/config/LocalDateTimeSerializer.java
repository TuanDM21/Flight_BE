package com.project.quanlycanghangkhong.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    
    private static final ZoneId VIETNAM_TIMEZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    
    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) 
            throws IOException {
        if (value != null) {
            // Chuyển LocalDateTime thành ZonedDateTime với timezone Vietnam
            String formattedDateTime = value.atZone(VIETNAM_TIMEZONE).format(FORMATTER);
            gen.writeString(formattedDateTime);
        } else {
            gen.writeNull();
        }
    }
}