package com.project.quanlycanghangkhong.request;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to assign a task to a recipient")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignmentRequest {

    // Constructor mặc định cần thiết cho Jackson
    public AssignmentRequest() {
    }

    @Schema(description = "Recipient ID (user, team, or unit ID)", example = "123", required = true)
    private Integer recipientId;

    @Schema(description = "Type of recipient", example = "USER", allowableValues = { "USER", "TEAM",
            "UNIT" }, required = true)
    private String recipientType;

    @Schema(description = "Due date for the assignment", example = "2025-09-05T18:00:00")
    private Date dueAt;

    @Schema(description = "Assignment note", example = "Please complete this urgently")
    private String note;

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public Date getDueAt() {
        return dueAt;
    }

    public void setDueAt(Date dueAt) {
        this.dueAt = dueAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
