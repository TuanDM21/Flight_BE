package com.project.quanlycanghangkhong.dto;

public class ActivityParticipantDTO {
    private Long id;
    private String participantType; // USER, TEAM, UNIT
    private Long participantId;
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
