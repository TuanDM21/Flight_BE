package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.request.LoginRequest;
import com.project.quanlycanghangkhong.request.RegisterRequest;
import com.project.quanlycanghangkhong.request.FirstLoginPasswordChangeRequest;
import com.project.quanlycanghangkhong.dto.LoginDTO;
import com.project.quanlycanghangkhong.dto.RegisterDTO;
import com.project.quanlycanghangkhong.dto.FirstLoginPasswordChangeDTO;
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
	ApiResponseCustom<LoginDTO> login(LoginRequest loginRequest);

	/**
	 * Registers a new user based on the provided register request.
	 *
	 * @param registerRequest the register request containing user details
	 * @return a custom API response containing the register response
	 */
	ApiResponseCustom<RegisterDTO> register(RegisterRequest registerRequest);

	/**
	 * Changes password for first login users.
	 *
	 * @param request the password change request containing current and new passwords
	 * @return a custom API response containing the new token after password change
	 */
	ApiResponseCustom<FirstLoginPasswordChangeDTO> changePasswordFirstLogin(FirstLoginPasswordChangeRequest request);
}
