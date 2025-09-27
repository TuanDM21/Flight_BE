package com.project.quanlycanghangkhong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request DTO for activity participant - only contains fields needed for requests")
public class ActivityParticipantRequest {
    
    @Schema(description = "Type of participant", example = "USER", allowableValues = {"USER", "TEAM", "UNIT"}, required = true)
    @NotBlank(message = "Participant type is required")
    private String participantType; // USER, TEAM, UNIT
    
    @Schema(description = "ID of the participant", example = "1", required = true)
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
