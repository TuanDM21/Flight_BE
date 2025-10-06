package com.project.quanlycanghangkhong.dto;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Response for first login password change")
public class FirstLoginPasswordChangeDTO {
    
    @Schema(description = "Success message", example = "Password changed successfully")
    private String message;
    
    @Schema(description = "Indicates if password change was successful", example = "true")
    private Boolean success;
    
    @Schema(description = "New JWT token after password change", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String newToken;
    
    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "Token expiration time in seconds", example = "3600")
    private Long expiresIn;
}
