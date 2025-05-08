package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@CrossOrigin(origins = "*")
public class HomeController {

	@GetMapping("/")
	@Operation(summary = "Trang chủ API")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
	})
	public ResponseEntity<ApiResponseCustom<String>> home() {
		return ResponseEntity.ok(ApiResponseCustom.success("Welcome to Airport Control System API"));
	}
}
