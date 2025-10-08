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
    
    @Schema(description = "Activity title", example = "Họp định kỳ tuần", required = true)
    @NotBlank(message = "Activity title is required")
    @Size(max = 255, message = "Activity title must not exceed 255 characters")
    private String title;
    
    @Schema(description = "Activity location", example = "Phòng họp A1", required = true)
    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;
    
    @Schema(description = "Start date of the activity", example = "2025-09-27T09:00:00", required = true)
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @Schema(description = "End date of the activity", example = "2025-09-27T10:00:00", required = true)
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
    
    @Schema(description = "Additional description", example = "Thảo luận về kế hoạch Q4")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @Schema(description = "List of participants (required - activity must have at least one participant)", required = true)
    @Valid
    @NotNull(message = "Participants list is required")
    @NotEmpty(message = "Activity must have at least one participant")
    private List<ActivityParticipantRequest> participants;
    
    @Schema(description = "Whether the activity is pinned", example = "false")
    private Boolean pinned;

    // Getters and Setters
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
