package com.project.quanlycanghangkhong.config;

import com.project.quanlycanghangkhong.config.interceptor.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	// Không sử dụng RequestInterceptor vì chúng ta đã có bảo mật từ Spring Security
	// private final RequestInterceptor requestInterceptor;

	@Override
	public void addInterceptors(@NonNull InterceptorRegistry registry) {
		// Không thêm RequestInterceptor - Spring Security sẽ xử lý xác thực
		// registry.addInterceptor(requestInterceptor);
	}

	@Override
	public void addCorsMappings(@NonNull CorsRegistry registry) {
		// CORS đã được cấu hình trong SecurityConfig
		// Bỏ qua cấu hình CORS ở đây để tránh xung đột
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/")
				.resourceChain(false);
	}
}
