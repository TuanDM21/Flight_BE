package com.project.quanlycanghangkhong.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		
		// Set timezone cho Jackson để serialize đúng múi giờ
		mapper.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		
		// Register custom serializer cho LocalDateTime
		SimpleModule module = new SimpleModule();
		module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
		mapper.registerModule(module);
		
		return mapper;
	}
}
