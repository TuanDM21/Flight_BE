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
  "title": "Họp định kỳ tuần",
  "location": "Phòng họp A1",
  "startDate": "2025-10-01T13:59:01.290Z",
  "endDate": "2025-10-01T15:00:00.000Z",
  "description": "Thảo luận về kế hoạch Q4",
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
    @Schema(description = "Activity title", example = "Họp định kỳ tuần", required = true)
    private String title;
    
    @NotBlank
    @Size(max = 255)
    @Schema(description = "Activity location", example = "Phòng họp A1", required = true)
    private String location;
    
    @NotNull
    @Schema(description = "Start date", example = "2025-10-01T13:59:01.290Z", required = true)
    private LocalDateTime startDate;
    
    @NotNull
    @Schema(description = "End date", example = "2025-10-01T15:00:00.000Z", required = true)
    private LocalDateTime endDate;
    
    @Size(max = 1000)
    @Schema(description = "Activity description", example = "Thảo luận về kế hoạch Q4")
    private String description;
    
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
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public LocalDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    public LocalDateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
