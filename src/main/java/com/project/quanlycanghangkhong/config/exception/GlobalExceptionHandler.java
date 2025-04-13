package com.project.quanlycanghangkhong.config.exception;

import com.project.quanlycanghangkhong.dto.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex, WebRequest request) {
		ApiResponse<Object> response = ApiResponse.error(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Đã xảy ra lỗi: " + ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
		ApiResponse<Object> response = ApiResponse.error(
				HttpStatus.FORBIDDEN,
				"Bạn không có quyền truy cập tài nguyên này");
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
		ApiResponse<Object> response = ApiResponse.error(
				HttpStatus.NOT_FOUND,
				ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
			MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			String fieldName = error.getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		// Log validation errors for debugging
		System.out.println("Validation errors: " + errors);

		ApiResponse<Map<String, String>> response = ApiResponse.error(
				HttpStatus.BAD_REQUEST,
				"Dữ liệu không hợp lệ");
		response.setData(errors);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
		ApiResponse<Object> response = ApiResponse.error(
				HttpStatus.BAD_REQUEST,
				"Dữ liệu vi phạm ràng buộc: " + ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatch(
			MethodArgumentTypeMismatchException ex) {
		ApiResponse<Object> response = ApiResponse.error(
				HttpStatus.BAD_REQUEST,
				"Tham số không đúng kiểu dữ liệu: " + ex.getName());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		ApiResponse<Object> response = ApiResponse.error(
				HttpStatus.CONFLICT,
				"Vi phạm tính toàn vẹn dữ liệu: " + ex.getMostSpecificCause().getMessage());
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
		ApiResponse<Object> response = ApiResponse.error(
				HttpStatus.BAD_REQUEST,
				ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
