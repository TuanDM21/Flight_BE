package com.project.quanlycanghangkhong.dto;

import java.util.List;

public class UpdateTaskDTO {
    private Integer id;
    private String content;
    private String instructions;
    private String notes;
    
    // MỚI: Hỗ trợ cập nhật attachment trong cùng 1 request
    private List<Integer> attachmentIds; // null = không thay đổi, empty = xóa hết, có giá trị = replace

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public List<Integer> getAttachmentIds() {
        return attachmentIds;
    }
    
    public void setAttachmentIds(List<Integer> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }
}
