package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.request.LoginRequest;
import com.project.quanlycanghangkhong.dto.request.RegisterRequest;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.response.auth.ApiLoginResponse;
import com.project.quanlycanghangkhong.dto.response.auth.ApiRegisterResponse;
import com.project.quanlycanghangkhong.dto.response.auth.LoginResponse;
import com.project.quanlycanghangkhong.dto.response.auth.RegisterResponse;
import com.project.quanlycanghangkhong.service.AuthService;
import com.project.quanlycanghangkhong.dto.response.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticate user and return JWT token")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = ApiLoginResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
	})
	public ResponseEntity<ApiResponseCustom<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(authService.login(loginRequest));
	}

	@PostMapping("/register")
	@Operation(summary = "User registration", description = "Register a new user")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registration successful", content = @Content(schema = @Schema(implementation = ApiRegisterResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
	})
	public ResponseEntity<ApiResponseCustom<RegisterResponse>> register(
			@Valid @RequestBody RegisterRequest registerRequest) {
		return ResponseEntity.ok(authService.register(registerRequest));
	}
}
