package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.request.LoginRequest;
import com.project.quanlycanghangkhong.dto.request.RegisterRequest;
import com.project.quanlycanghangkhong.dto.response.ApiResponse;
import com.project.quanlycanghangkhong.dto.response.LoginResponse;
import com.project.quanlycanghangkhong.dto.response.RegisterResponse;
import com.project.quanlycanghangkhong.service.AuthService;
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
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticate user and return JWT token")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(authService.login(loginRequest));
	}

	@PostMapping("/register")
	@Operation(summary = "User registration", description = "Register a new user")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registration successful"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
		return ResponseEntity.ok(authService.register(registerRequest));
	}
}
