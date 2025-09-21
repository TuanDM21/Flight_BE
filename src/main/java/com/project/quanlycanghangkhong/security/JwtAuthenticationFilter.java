package com.project.quanlycanghangkhong.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
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
@ConditionalOnProperty(name = "spring.main.web-application-type", havingValue = "servlet", matchIfMissing = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;
	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Override
	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();

		return path.equals("/") ||
				path.equals("/api/auth/login") ||
				path.equals("/api/auth/register") ||
				path.equals("/swagger-ui") ||
				path.startsWith("/swagger-ui/") ||
				path.equals("/swagger-ui.html") ||
				path.startsWith("/v3/api-docs") ||
				path.startsWith("/webjars/") ||
				path.startsWith("/swagger-resources") ||
				path.equals("/swagger-resources");
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");

		// Log header Authorization
		log.info("Authorization Header: {}", authHeader);

		// Không có header Authorization hoặc không đúng format
		// Thay vì trả về 401 ngay lập tức, hãy để Spring Security xử lý
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
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
					log.warn("Invalid or expired token, continuing without authentication");
				}
			} else {
				log.warn("No user found or authentication already exists, continuing without authentication");
			}
		} catch (Exception e) {
			log.error("Error processing JWT token: {}", e.getMessage());
			// Tiếp tục filter chain thay vì trả về lỗi
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
