package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.dto.response.ApiResponseCustom;
import com.project.quanlycanghangkhong.dto.HomeResponseDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class HomeController {

	private final Environment env;

	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Trang chủ API")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công", content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
	})
	public ResponseEntity<ApiResponseCustom<HomeResponseDTO>> home(HttpServletRequest request) {
		String scheme = request.getScheme(); // http or https
		String host = request.getServerName(); // localhost or domain
		int port = request.getServerPort();
		String base = scheme + "://" + host + (port == 80 || port == 443 ? "" : ":" + port);
		String docsUrl = null;

		// Hide docs link if 'prod' profile is active
		if (!env.acceptsProfiles(Profiles.of("prod"))) {
			docsUrl = base + "/swagger-ui/index.html";
		}

		HomeResponseDTO dto = new HomeResponseDTO("Welcome to Airport Control System API", docsUrl);
		return ResponseEntity.ok(ApiResponseCustom.success(dto));
	}
}
