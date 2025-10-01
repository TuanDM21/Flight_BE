package com.project.quanlycanghangkhong.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Activity response DTO", example = """
{
  "id": 101,
  "name": "Họp định kỳ tuần",
  "location": "Phòng họp A1",
  "startTime": "2025-10-01T13:59:01.290Z",
  "endTime": "2025-10-01T15:00:00.000Z",
  "notes": "Thảo luận về kế hoạch Q4",
  "participants": [
    {
      "id": 201,
      "participantType": "USER",
      "participantId": 1,
      "participantName": "Nguyễn Văn A"
    },
    {
      "id": 202,
      "participantType": "USER",
      "participantId": 2,
      "participantName": "Trần Thị B"
    },
    {
      "id": 203,
      "participantType": "USER",
      "participantId": 3,
      "participantName": "Lê Văn C"
    }
  ],
  "createdAt": "2025-10-01T14:00:00.000Z",
  "updatedAt": "2025-10-01T14:00:00.000Z",
  "pinned": false
}
""")
public class ActivityDTO {
    
    @Schema(description = "Activity ID", example = "101")
    private Long id;
    
    @NotBlank
    @Size(max = 255)
    @Schema(description = "Activity name", example = "Họp định kỳ tuần", required = true)
    private String name;
    
    @NotBlank
    @Size(max = 255)
    @Schema(description = "Activity location", example = "Phòng họp A1", required = true)
    private String location;
    
    @NotNull
    @Schema(description = "Start time", example = "2025-10-01T13:59:01.290Z", required = true)
    private LocalDateTime startTime;
    
    @NotNull
    @Schema(description = "End time", example = "2025-10-01T15:00:00.000Z", required = true)
    private LocalDateTime endTime;
    
    @Size(max = 1000)
    @Schema(description = "Activity notes", example = "Thảo luận về kế hoạch Q4")
    private String notes;
    
    @Schema(description = "List of participants")
    private List<ActivityParticipantDTO> participants;
    
    @Schema(description = "Creation timestamp", example = "2025-10-01T14:00:00.000Z")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", example = "2025-10-01T14:00:00.000Z")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Whether the activity is pinned", example = "false")
    private Boolean pinned;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public List<ActivityParticipantDTO> getParticipants() {
        return participants;
    }
    public void setParticipants(List<ActivityParticipantDTO> participants) {
        this.participants = participants;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Boolean getPinned() {
        return pinned;
    }
    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }
}
