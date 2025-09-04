package com.project.quanlycanghangkhong.request;

import java.util.List;
import com.project.quanlycanghangkhong.model.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO để tạo subtask trong mô hình Adjacency List
 * LOGIC NGHIỆP VỤ: Hỗ trợ cấu trúc task phân cấp với việc gán attachment trực
 * tiếp
 * LƯU Ý: parentId được truyền qua path parameter, không cần trong request body
 */
@Schema(description = "Request to create a subtask")
public class CreateSubtaskRequest {
    // parentId được truyền qua @PathVariable trong controller, không cần trong DTO
    @Schema(description = "Subtask title", example = "Chi tiết kiểm tra passport", required = true)
    private String title;

    @Schema(description = "Subtask content/description", example = "Kiểm tra tính hợp lệ của passport")
    private String content;

    @Schema(description = "Subtask instructions", example = "Kiểm tra theo quy trình XYZ")
    private String instructions;

    @Schema(description = "Subtask notes", example = "Lưu ý đặc biệt về passport VIP")
    private String notes;

    @Schema(description = "Subtask priority", example = "NORMAL")
    private TaskPriority priority;

    @Schema(description = "List of assignments for this subtask")
    private List<AssignmentRequest> assignments;

    @Schema(description = "List of attachment IDs to assign to this subtask", example = "[1, 2, 3]")
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
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Gán attachment trực tiếp thay thế cách tiếp cận dựa
     * trên document
     * 
     * @return Danh sách ID attachment
     */
    public List<Integer> getAttachmentIds() {
        return attachmentIds;
    }

    /**
     * Đặt danh sách ID attachment để gán trực tiếp vào subtask
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Cho phép quan hệ task-attachment trực tiếp
     * 
     * @param attachmentIds Danh sách ID attachment để gán
     */
    public void setAttachmentIds(List<Integer> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }
}
