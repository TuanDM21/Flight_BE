package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.request.LoginRequest;
import com.project.quanlycanghangkhong.dto.request.RegisterRequest;
import com.project.quanlycanghangkhong.dto.response.ApiResponse;
import com.project.quanlycanghangkhong.dto.response.LoginResponse;
import com.project.quanlycanghangkhong.dto.response.RegisterResponse;

public interface AuthService {
	ApiResponse<LoginResponse> login(LoginRequest loginRequest);

	ApiResponse<RegisterResponse> register(RegisterRequest registerRequest);
}
