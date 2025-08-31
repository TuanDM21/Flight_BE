package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.request.LoginRequest;
import com.project.quanlycanghangkhong.request.RegisterRequest;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.LoginDTO;
import com.project.quanlycanghangkhong.dto.RegisterDTO;
import com.project.quanlycanghangkhong.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
		})
	public ResponseEntity<ApiResponseCustom<LoginDTO>> login(@Valid @RequestBody LoginRequest loginRequest) {
		ApiResponseCustom<LoginDTO> response = authService.login(loginRequest);		// Trả về status code dựa trên kết quả từ service
		if (response.isSuccess()) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(response.getStatusCode()).body(response);
		}
	}

	@PostMapping("/register")
	@Operation(summary = "User registration", description = "Register a new user")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registration successful", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
	})
	public ResponseEntity<ApiResponseCustom<RegisterDTO>> register(
			@Valid @RequestBody RegisterRequest registerRequest) {
		return ResponseEntity.ok(authService.register(registerRequest));
	}
}
