package com.project.quanlycanghangkhong.dto.request;

import com.project.quanlycanghangkhong.model.TaskPriority;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho tìm kiếm nâng cao tasks
 */
public class AdvancedSearchRequest {
    
    /**
     * Từ khóa tìm kiếm trong title hoặc content
     */
    private String keyword;
    
    /**
     * Thời gian bắt đầu (format: yyyy-MM-dd'T'HH:mm:ss)
     */
    private LocalDateTime startTime;
    
    /**
     * Thời gian kết thúc (format: yyyy-MM-dd'T'HH:mm:ss)
     */
    private LocalDateTime endTime;
    
    /**
     * Danh sách priority để filter: LOW, NORMAL, HIGH, URGENT
     * Có thể chọn nhiều priority (multi-select)
     */
    private List<TaskPriority> priorities;
    
    /**
     * Danh sách recipient để filter (multi-select)
     * Mỗi recipient object chứa recipientType và recipientId
     */
    private List<RecipientFilter> recipients;
    
    /**
     * Filter type: completed, pending, urgent, overdue
     * (Tương thích với filter hiện tại)
     */
    private String filter;
    
    /**
     * Inner class cho recipient filter
     */
    public static class RecipientFilter {
        private String recipientType; // user, team, unit
        private Integer recipientId;
        
        public RecipientFilter() {}
        
        public RecipientFilter(String recipientType, Integer recipientId) {
            this.recipientType = recipientType;
            this.recipientId = recipientId;
        }
        
        public String getRecipientType() { return recipientType; }
        public void setRecipientType(String recipientType) { this.recipientType = recipientType; }
        public Integer getRecipientId() { return recipientId; }
        public void setRecipientId(Integer recipientId) { this.recipientId = recipientId; }
        
        public boolean isValid() {
            return recipientType != null && recipientType.matches("user|team|unit") && recipientId != null;
        }
    }
    
    // Constructors
    public AdvancedSearchRequest() {}
    
    public AdvancedSearchRequest(String keyword, LocalDateTime startTime, LocalDateTime endTime, 
                               List<TaskPriority> priorities, List<RecipientFilter> recipients, String filter) {
        this.keyword = keyword;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priorities = priorities;
        this.recipients = recipients;
        this.filter = filter;
    }
    
    // Getters and Setters
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
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
    
    public List<TaskPriority> getPriorities() {
        return priorities;
    }
    
    public void setPriorities(List<TaskPriority> priorities) {
        this.priorities = priorities;
    }
    
    public List<RecipientFilter> getRecipients() {
        return recipients;
    }
    
    public void setRecipients(List<RecipientFilter> recipients) {
        this.recipients = recipients;
    }
    
    public String getFilter() {
        return filter;
    }
    
    public void setFilter(String filter) {
        this.filter = filter;
    }
    
    /**
     * Validate dữ liệu đầu vào
     * @return true nếu valid, false nếu không
     */
    public boolean isValid() {
        // Validate recipients
        if (recipients != null) {
            for (RecipientFilter recipient : recipients) {
                if (!recipient.isValid()) {
                    return false;
                }
            }
        }
        
        // Validate filter
        if (filter != null && !filter.matches("completed|pending|urgent|overdue")) {
            return false;
        }
        
        // Validate time range
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Check xem có tiêu chí tìm kiếm nào không
     * @return true nếu có ít nhất 1 tiêu chí tìm kiếm
     */
    public boolean hasSearchCriteria() {
        return keyword != null || startTime != null || endTime != null || 
               (priorities != null && !priorities.isEmpty()) ||
               (recipients != null && !recipients.isEmpty()) || filter != null;
    }
}
