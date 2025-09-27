package com.project.quanlycanghangkhong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Request DTO for creating or updating activities")
public class ActivityRequest {
    
    @Schema(description = "Activity name", example = "Họp định kỳ tuần", required = true)
    @NotBlank(message = "Activity name is required")
    @Size(max = 255, message = "Activity name must not exceed 255 characters")
    private String name;
    
    @Schema(description = "Activity location", example = "Phòng họp A1", required = true)
    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;
    
    @Schema(description = "Start time of the activity", example = "2025-09-27T09:00:00", required = true)
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    @Schema(description = "End time of the activity", example = "2025-09-27T10:00:00", required = true)
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
    
    @Schema(description = "Additional notes", example = "Thảo luận về kế hoạch Q4")
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    @Schema(description = "List of participants (required - activity must have at least one participant)", required = true)
    @Valid
    @NotNull(message = "Participants list is required")
    @NotEmpty(message = "Activity must have at least one participant")
    private List<ActivityParticipantRequest> participants;
    
    @Schema(description = "Whether the activity is pinned", example = "false")
    private Boolean pinned;

    // Getters and Setters
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
    
    public List<ActivityParticipantRequest> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<ActivityParticipantRequest> participants) {
        this.participants = participants;
    }
    
    public Boolean getPinned() {
        return pinned;
    }
    
    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }
}
