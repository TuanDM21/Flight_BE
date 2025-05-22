package com.project.quanlycanghangkhong.dto;

import java.util.List;

public class UpdateDocumentRequest {
    private String documentType;
    private String content;
    private String notes;
    private List<Integer> attachmentIds;
    // Đã bỏ attachments, chỉ còn các trường thông tin document
    // getters and setters
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<Integer> getAttachmentIds() { return attachmentIds; }
    public void setAttachmentIds(List<Integer> attachmentIds) { this.attachmentIds = attachmentIds; }
}