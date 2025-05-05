package com.project.quanlycanghangkhong.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;
	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();

		// Chỉ có API đăng nhập và đăng ký mới được bỏ qua việc kiểm tra token
		return path.equals("/api/auth/login") ||
				path.equals("/api/auth/register") ||
				path.startsWith("/swagger-ui/") ||
				path.startsWith("/v3/api-docs") ||
				path.startsWith("/webjars/") ||
				path.startsWith("/swagger-resources");
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");

		// Log header Authorization
		log.info("Authorization Header: {}", authHeader);

		// Không có header Authorization hoặc không đúng format
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.error("Missing or invalid Authorization header");
			sendUnauthorizedError(response,
					"Thiếu header Authorization hoặc định dạng không hợp lệ. Vui lòng đăng nhập.");
			return;
		}

		try {
			String jwt = authHeader.substring(7);

			String email = jwtTokenProvider.getUsernameFromJWT(jwt);

			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

				if (jwtTokenProvider.validateToken(jwt)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails,
							null,
							userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				} else {
					sendUnauthorizedError(response, "Token không hợp lệ hoặc đã hết hạn. Vui lòng đăng nhập lại.");
					return;
				}
			} else {
				sendUnauthorizedError(response, "Không tìm thấy thông tin xác thực. Vui lòng đăng nhập.");
				return;
			}
		} catch (Exception e) {
			sendUnauthorizedError(response, "Lỗi xử lý token. Vui lòng thử lại.");
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		ApiResponseCustom<?> apiResponse = ApiResponseCustom.error(HttpStatus.UNAUTHORIZED, message);
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	}
}
