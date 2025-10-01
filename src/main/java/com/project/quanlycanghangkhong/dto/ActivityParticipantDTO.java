package com.project.quanlycanghangkhong.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Activity participant information", example = """
{
  "id": 201,
  "participantType": "USER",
  "participantId": 1,
  "participantName": "Nguyễn Văn A"
}
""")
public class ActivityParticipantDTO {
    
    @Schema(description = "Participant record ID", example = "201")
    private Long id;
    
    @Schema(description = "Type of participant", example = "USER", allowableValues = {"USER", "TEAM", "UNIT"})
    private String participantType; // USER, TEAM, UNIT
    
    @Schema(description = "Participant ID", example = "1")
    private Long participantId;
    
    @Schema(description = "Participant name for display", example = "Nguyễn Văn A")
    private String participantName; // optional, for response

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
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
    public String getParticipantName() {
        return participantName;
    }
    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }
}
