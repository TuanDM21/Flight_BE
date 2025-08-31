package com.project.quanlycanghangkhong.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssignmentCommentRequest {
    @JsonProperty("comment")
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}