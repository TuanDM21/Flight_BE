package com.project.quanlycanghangkhong.config.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResponseInterceptor implements ResponseBodyAdvice<Object> {

	private final ObjectMapper objectMapper;

	public ResponseInterceptor(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		// Chỉ áp dụng cho các response không phải là ApiResponseCustom hoặc
		// ResponseEntity<ApiResponseCustom>
		return !returnType.getParameterType().equals(ApiResponseCustom.class) &&
				!(returnType.getParameterType().equals(ResponseEntity.class) &&
						returnType.getGenericParameterType().getTypeName().contains("ApiResponseCustom"));
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		// Nếu là Swagger UI hoặc OpenAPI docs, trả về nguyên vẹn
		String path = request.getURI().getPath();
		if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
			return body;
		}

		// Nếu body là null, trả về ApiResponseCustom với data là null
		if (body == null) {
			return ApiResponseCustom.success(null);
		}

		// Nếu đã là ApiResponseCustom, trả về nguyên vẹn
		if (body instanceof ApiResponseCustom) {
			return body;
		}

		// Nếu là ResponseEntity, xử lý đặc biệt
		if (body instanceof ResponseEntity) {
			ResponseEntity<?> responseEntity = (ResponseEntity<?>) body;
			Object responseBody = responseEntity.getBody();

			// Không wrap nếu đã là ApiResponseCustom
			if (responseBody instanceof ApiResponseCustom) {
				return body;
			}

			// Tạo ApiResponseCustom mới với status code và body từ ResponseEntity
			ApiResponseCustom<?> apiResponse = ApiResponseCustom.builder()
					.statusCode(responseEntity.getStatusCode().value())
					.message("Thành công")
					.data(responseBody)
					.success(true)
					.build();

			return ResponseEntity.status(responseEntity.getStatusCode())
					.headers(responseEntity.getHeaders())
					.body(apiResponse);
		}

		// Nếu là String, cần chuyển đổi thành JSON string
		if (body instanceof String) {
			try {
				ApiResponseCustom<Object> apiResponse = ApiResponseCustom.success(body);
				return objectMapper.writeValueAsString(apiResponse);
			} catch (JsonProcessingException e) {
				return body;
			}
		}

		// Nếu không phải ResponseEntity, wrap trực tiếp trong ApiResponseCustom
		return ApiResponseCustom.success(body);
	}
}
