package com.project.quanlycanghangkhong.config.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RequestInterceptor implements HandlerInterceptor {

	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper objectMapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// Kiểm tra token cho tất cả các API khác
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			ApiResponseCustom<?> apiResponse = ApiResponseCustom.error(HttpStatus.UNAUTHORIZED,
					"Vui lòng đăng nhập để tiếp tục");
			response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
			return false;
		}

		String token = authHeader.substring(7);
		if (!jwtTokenProvider.validateToken(token)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			ApiResponseCustom<?> apiResponse = ApiResponseCustom.error(HttpStatus.UNAUTHORIZED,
					"Token không hợp lệ hoặc đã hết hạn");
			response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
			return false;
		}

		return true;
	}
}
