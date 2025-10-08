package com.project.quanlycanghangkhong.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request for changing password on first login")
public class FirstLoginPasswordChangeRequest {
    
    @NotBlank(message = "Current password is required")
    @Schema(description = "Current password", example = "currentPassword123", required = true)
    private String currentPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    @Schema(description = "New password", example = "newPassword123", required = true, minLength = 6)
    private String newPassword;
    
    @NotBlank(message = "Confirm password is required")
    @Schema(description = "Confirm new password", example = "newPassword123", required = true)
    private String confirmPassword;
}
