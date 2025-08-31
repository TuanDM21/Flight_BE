package com.project.quanlycanghangkhong.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDTO {
	private String accessToken;
	private String tokenType;
	private Long expiresIn;
}
