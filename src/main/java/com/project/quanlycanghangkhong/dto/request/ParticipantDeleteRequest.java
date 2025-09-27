package com.project.quanlycanghangkhong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request DTO for deleting a single participant")
public class ParticipantDeleteRequest {
    
    @Schema(description = "Type of participant", example = "USER", allowableValues = {"USER", "TEAM", "UNIT"}, required = true)
    @NotBlank(message = "Participant type is required")
    @Pattern(regexp = "^(USER|TEAM|UNIT)$", message = "Participant type must be one of: USER, TEAM, UNIT")
    private String participantType;
    
    @Schema(description = "ID of the participant to delete", example = "1", required = true)
    @NotNull(message = "Participant ID is required")
    private Long participantId;

    public String getParticipantType() {
        return participantType;
    }
    
    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }
    
    public Long getParticipantId() {
        return participantId;
    }
    
    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
}
