package com.project.quanlycanghangkhong.config.exception;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
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
	public ResponseEntity<ApiResponseCustom<Object>> handleAllExceptions(Exception ex, WebRequest request) {
		ApiResponseCustom<Object> response = ApiResponseCustom.error(
				HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponseCustom<Object>> handleAccessDeniedException(AccessDeniedException ex) {
		ApiResponseCustom<Object> response = ApiResponseCustom.error(
				HttpStatus.FORBIDDEN, "Access denied");
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ApiResponseCustom<Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
		ApiResponseCustom<Object> response = ApiResponseCustom.error(
				HttpStatus.NOT_FOUND, ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponseCustom<Map<String, String>>> handleValidationExceptions(
			MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		ApiResponseCustom<Map<String, String>> response = ApiResponseCustom.error(HttpStatus.BAD_REQUEST,
				"Validation failed");
		response.setData(errors);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponseCustom<Object>> handleConstraintViolationException(
			ConstraintViolationException ex) {
		ApiResponseCustom<Object> response = ApiResponseCustom.error(
				HttpStatus.BAD_REQUEST,
				"Dữ liệu vi phạm ràng buộc: " + ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponseCustom<Object>> handleMethodArgumentTypeMismatch(
			MethodArgumentTypeMismatchException ex) {
		ApiResponseCustom<Object> response = ApiResponseCustom.error(
				HttpStatus.BAD_REQUEST,
				"Tham số không đúng kiểu dữ liệu: " + ex.getName());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponseCustom<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		ApiResponseCustom<Object> response = ApiResponseCustom.error(
				HttpStatus.CONFLICT,
				"Vi phạm tính toàn vẹn dữ liệu: " + ex.getMostSpecificCause().getMessage());
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponseCustom<Object>> handleRuntimeException(RuntimeException ex) {
		ApiResponseCustom<Object> response = ApiResponseCustom.error(
				HttpStatus.BAD_REQUEST,
				ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
