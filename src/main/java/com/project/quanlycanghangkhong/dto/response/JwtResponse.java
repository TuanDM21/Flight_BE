package com.project.quanlycanghangkhong.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String username;
	private String role;
}
