package com.project.quanlycanghangkhong.dto.request;

import com.project.quanlycanghangkhong.model.TaskPriority;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO cho tìm kiếm nâng cao tasks
 */
public class AdvancedSearchRequest {
    
    /**
     * Từ khóa tìm kiếm trong title hoặc content
     */
    private String keyword;
    
    /**
     * Thời gian bắt đầu (format: yyyy-MM-dd)
     */
    private LocalDate startTime;
    
    /**
     * Thời gian kết thúc (format: yyyy-MM-dd)
     */
    private LocalDate endTime;
    
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
     * Task type: created, assigned, received
     * Mặc định là assigned để tương thích ngược
     */
    private String type = "assigned";
    
    /**
     * Pagination - Số trang (bắt đầu từ 0)
     */
    private Integer page = 0;
    
    /**
     * Pagination - Số lượng items per page (default 20, max 100)
     */
    private Integer size = 20;
    
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
        
        @JsonIgnore
        public boolean isValid() {
            return recipientType != null && recipientType.matches("user|team|unit") && recipientId != null;
        }
    }
    
    // Constructors
    public AdvancedSearchRequest() {}
    
    public AdvancedSearchRequest(String keyword, LocalDate startTime, LocalDate endTime, 
                               List<TaskPriority> priorities, List<RecipientFilter> recipients, String filter,
                               Integer page, Integer size) {
        this.keyword = keyword;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priorities = priorities;
        this.recipients = recipients;
        this.filter = filter;
        this.page = (page != null) ? page : 0;
        this.size = (size != null) ? size : 20;
    }
    
    // Getters and Setters
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public LocalDate getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }
    
    public LocalDate getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDate endTime) {
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = (page != null && page >= 0) ? page : 0;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = (size != null && size > 0 && size <= 100) ? size : 20;
    }
    
    /**
     * Validate dữ liệu đầu vào
     * @return true nếu valid, false nếu không
     */
    @JsonIgnore
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
        
        // Validate pagination
        if (page != null && page < 0) {
            return false;
        }
        if (size != null && (size <= 0 || size > 100)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Check xem có tiêu chí tìm kiếm nào không
     * @return true nếu có ít nhất 1 tiêu chí tìm kiếm (hoặc chỉ type với pagination)
     */
    @JsonIgnore
    public boolean hasSearchCriteria() {
        // Nếu chỉ có type và pagination parameters thì vẫn được phép
        return keyword != null || startTime != null || endTime != null || 
               (priorities != null && !priorities.isEmpty()) ||
               (recipients != null && !recipients.isEmpty()) || 
               filter != null ||
               (type != null && (page != null || size != null)); // Allow type-only search with pagination
    }
}
