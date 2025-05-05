package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.request.LoginRequest;
import com.project.quanlycanghangkhong.dto.request.RegisterRequest;
import com.project.quanlycanghangkhong.dto.response.auth.LoginResponse;
import com.project.quanlycanghangkhong.dto.response.auth.RegisterResponse;
import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

/**
 * AuthService interface for authentication-related operations.
 */
public interface AuthService {

	/**
	 * Authenticates a user based on the provided login request.
	 *
	 * @param loginRequest the login request containing email and password
	 * @return a custom API response containing the login response
	 * @throws UnauthorizedException if the credentials are invalid
	 * @throws ForbiddenException    if the user account is disabled
	 */
	ApiResponseCustom<LoginResponse> login(LoginRequest loginRequest);

	/**
	 * Registers a new user based on the provided register request.
	 *
	 * @param registerRequest the register request containing user details
	 * @return a custom API response containing the register response
	 */
	ApiResponseCustom<RegisterResponse> register(RegisterRequest registerRequest);
}
