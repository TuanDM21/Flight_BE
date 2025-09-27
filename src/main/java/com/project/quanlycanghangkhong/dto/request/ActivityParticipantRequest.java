package com.project.quanlycanghangkhong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Schema(description = "Request DTO for activity participant - contains type and array of IDs")
public class ActivityParticipantRequest {
    
    @Schema(description = "Type of participant", example = "USER", allowableValues = {"USER", "TEAM", "UNIT"}, required = true)
    @NotBlank(message = "Participant type is required")
    @Pattern(regexp = "^(USER|TEAM|UNIT)$", message = "Participant type must be one of: USER, TEAM, UNIT")
    private String participantType; // USER, TEAM, UNIT
    
    @Schema(description = "Array of participant IDs", example = "[1, 2, 3]", required = true)
    @NotNull(message = "Participant IDs are required")
    @NotEmpty(message = "At least one participant ID is required")
    private List<Long> participantIds;

    public String getParticipantType() {
        return participantType;
    }
    
    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }
    
    public List<Long> getParticipantIds() {
        return participantIds;
    }
    
    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }
}
