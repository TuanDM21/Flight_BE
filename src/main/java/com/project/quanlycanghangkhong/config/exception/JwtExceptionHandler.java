package com.project.quanlycanghangkhong.config.exception;

import com.project.quanlycanghangkhong.dto.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JwtExceptionHandler {

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
		ApiResponse<Object> response = ApiResponse.error(
				HttpStatus.UNAUTHORIZED,
				"Thông tin đăng nhập không chính xác");
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		ApiResponse<Object> response = ApiResponse.error(
				HttpStatus.UNAUTHORIZED,
				"Không tìm thấy người dùng");
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({
			ExpiredJwtException.class,
			MalformedJwtException.class,
			SignatureException.class,
			UnsupportedJwtException.class,
			IllegalArgumentException.class
	})
	public ResponseEntity<ApiResponse<Object>> handleJwtException(Exception ex) {
		String errorMessage;

		if (ex instanceof ExpiredJwtException) {
			errorMessage = "Token đã hết hạn. Vui lòng đăng nhập lại.";
		} else if (ex instanceof MalformedJwtException) {
			errorMessage = "Token không đúng định dạng. Vui lòng kiểm tra lại.";
		} else if (ex instanceof SignatureException) {
			errorMessage = "Chữ ký token không hợp lệ. Vui lòng đăng nhập lại.";
		} else if (ex instanceof UnsupportedJwtException) {
			errorMessage = "Token không được hỗ trợ. Vui lòng kiểm tra lại.";
		} else {
			errorMessage = "Lỗi xử lý token. Vui lòng thử lại.";
		}

		ApiResponse<Object> response = ApiResponse.error(HttpStatus.UNAUTHORIZED, errorMessage);
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}
}
