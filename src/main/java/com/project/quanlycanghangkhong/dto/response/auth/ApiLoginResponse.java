package com.project.quanlycanghangkhong.dto.response.auth;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API response for login, data is LoginResponse")
public class ApiLoginResponse extends ApiResponseCustom<LoginResponse> {
}
