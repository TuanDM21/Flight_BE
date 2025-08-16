package com.project.quanlycanghangkhong.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.project.quanlycanghangkhong.model.TaskPriority;

public class TaskDetailDTO {
    private Integer id;
    private String title;
    private String content;
    private String instructions;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO createdByUser;
    private List<AssignmentDTO> assignments;
    private com.project.quanlycanghangkhong.model.TaskStatus status;
    private TaskPriority priority;
    
    // MỚI: Hỗ trợ subtask cho mô hình Adjacency List
    private Integer parentId; // Tham chiếu đến ID task cha
    private List<TaskDetailDTO> subtasks; // Danh sách task con
    private Integer hierarchyLevel; // Cấp độ trong hierarchy (0=root, 1=child, 2=grandchild...)
    
    // ✅ DEPTH CONTROL: Giới hạn độ sâu đệ quy
    public static final int MAX_SUBTASK_DEPTH = 5; // Giới hạn tối đa 5 levels
    private boolean hasMoreSubtasks; // Flag để biết có subtasks ở levels sâu hơn không
    private int currentDepth; // Depth hiện tại khi loading subtasks
    
    // MỚI: Attachment trực tiếp (THAY THẾ hoàn toàn documents)
    private List<AttachmentDTO> attachments; // Quan hệ task-attachment trực tiếp

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public UserDTO getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(UserDTO createdByUser) {
        this.createdByUser = createdByUser;
    }

    public List<AssignmentDTO> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentDTO> assignments) {
        this.assignments = assignments;
    }

    public com.project.quanlycanghangkhong.model.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(com.project.quanlycanghangkhong.model.TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    /**
     * Lấy ID task cha cho cấu trúc phân cấp Adjacency List
     * @return ID task cha, null nếu đây là task gốc
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * Đặt ID task cha cho cấu trúc phân cấp Adjacency List
     * @param parentId ID task cha
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * Lấy tất cả subtask (task con) của task này
     * MÔ HÌNH ADJACENCY LIST: Tải đệ quy các task con
     * @return Danh sách task con
     */
    public List<TaskDetailDTO> getSubtasks() {
        return subtasks;
    }

    /**
     * Đặt subtask (task con) cho task này
     * @param subtasks Danh sách task con
     */
    public void setSubtasks(List<TaskDetailDTO> subtasks) {
        this.subtasks = subtasks;
    }

    /**
     * Lấy attachment được liên kết trực tiếp với task này
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Quan hệ task-attachment trực tiếp thay thế cách tiếp cận dựa trên document
     * @return Danh sách attachment được liên kết trực tiếp với task
     */
    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    /**
     * Đặt attachment được liên kết trực tiếp với task này
     * THAY ĐỔI LOGIC NGHIỆP VỤ: Thay thế quản lý file dựa trên document cũ
     * @param attachments Danh sách attachment để liên kết với task
     */
    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    /**
     * Lấy cấp độ hierarchy của task (0=root, 1=child, 2=grandchild...)
     * @return Cấp độ trong cấu trúc phân cấp
     */
    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }

    /**
     * Đặt cấp độ hierarchy của task
     * @param hierarchyLevel Cấp độ trong cấu trúc phân cấp
     */
    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    /**
     * ✅ DEPTH CONTROL: Kiểm tra có subtasks ở levels sâu hơn không
     * @return true nếu có subtasks nhưng bị giới hạn bởi MAX_SUBTASK_DEPTH
     */
    public boolean hasMoreSubtasks() {
        return hasMoreSubtasks;
    }

    /**
     * ✅ DEPTH CONTROL: Đặt flag cho subtasks ở levels sâu hơn
     * @param hasMoreSubtasks true nếu có subtasks nhưng bị giới hạn
     */
    public void setHasMoreSubtasks(boolean hasMoreSubtasks) {
        this.hasMoreSubtasks = hasMoreSubtasks;
    }

    /**
     * ✅ DEPTH CONTROL: Lấy depth hiện tại khi loading subtasks
     * @return Current depth level
     */
    public int getCurrentDepth() {
        return currentDepth;
    }

    /**
     * ✅ DEPTH CONTROL: Đặt depth hiện tại khi loading subtasks
     * @param currentDepth Current depth level
     */
    public void setCurrentDepth(int currentDepth) {
        this.currentDepth = currentDepth;
    }

    /**
     * ✅ DEPTH CONTROL: Kiểm tra có thể load subtasks ở level này không
     * @param currentLevel Level hiện tại
     * @return true nếu có thể load subtasks (chưa vượt quá MAX_SUBTASK_DEPTH)
     */
    public static boolean canLoadSubtasksAtLevel(int currentLevel) {
        return currentLevel < MAX_SUBTASK_DEPTH;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TaskDetailDTO that = (TaskDetailDTO) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "TaskDetailDTO{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", hierarchyLevel=" + hierarchyLevel +
            ", parentId=" + parentId +
            '}';
    }
}
