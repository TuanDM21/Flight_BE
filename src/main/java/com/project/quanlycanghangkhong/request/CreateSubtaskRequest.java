package com.project.quanlycanghangkhong.request;

import java.util.List;
import com.project.quanlycanghangkhong.model.TaskPriority;

/**
 * DTO để tạo subtask trong mô hình Adjacency List
 * LOGIC NGHIỆP VỤ: Hỗ trợ cấu trúc task phân cấp với việc gán attachment trực tiếp
 * LƯU Ý: parentId được truyền qua path parameter, không cần trong request body
 */
public class CreateSubtaskRequest {
    // parentId được truyền qua @PathVariable trong controller, không cần trong DTO
    private String title;
    private String content;
    private String instructions;
    private String notes;
    private TaskPriority priority;
    private List<AssignmentRequest> assignments;
    private List<Integer> attachmentIds; // Gán attachment trực tiếp (thay thế cách tiếp cận document)

    // Getters and setters
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public List<AssignmentRequest> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentRequest> assignments) {
        this.assignments = assignments;
    }

    /**
     * Lấy danh sách ID attachment để gán trực tiếp vào subtask
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Gán attachment trực tiếp thay thế cách tiếp cận dựa trên document
     * @return Danh sách ID attachment
     */
    public List<Integer> getAttachmentIds() {
        return attachmentIds;
    }

    /**
     * Đặt danh sách ID attachment để gán trực tiếp vào subtask
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Cho phép quan hệ task-attachment trực tiếp
     * @param attachmentIds Danh sách ID attachment để gán
     */
    public void setAttachmentIds(List<Integer> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }
}
