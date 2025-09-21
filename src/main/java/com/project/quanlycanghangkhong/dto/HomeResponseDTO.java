package com.project.quanlycanghangkhong.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Home response with docs link")
public class HomeResponseDTO {
	@Schema(description = "Welcome message")
	private String message;

	@Schema(description = "Link to API documentation (Swagger UI)")
	private String docs;
}
