package com.project.quanlycanghangkhong.dto;

/**
 * DTO đơn giản cho thông tin pagination
 */
public class PaginationInfo {
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    
    public PaginationInfo() {}
    
    /**
     * Constructor với 1-based pagination (page bắt đầu từ 1)
     * @param currentPage Current page number (1-based: 1, 2, 3...)
     * @param pageSize Number of items per page
     * @param totalElements Total number of elements
     */
    public PaginationInfo(int currentPage, int pageSize, long totalElements) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        // ✅ 1-based pagination logic
        this.hasNext = currentPage < totalPages;
        this.hasPrevious = currentPage > 1;
    }
    
    /**
     * Helper method: Convert 1-based page to 0-based offset for database queries
     * @param page 1-based page number
     * @param size page size
     * @return 0-based offset for database LIMIT/OFFSET
     */
    public static int calculateOffset(int page, int size) {
        return Math.max(0, (page - 1) * size);
    }
    
    /**
     * Helper method: Validate and normalize page parameters for 1-based pagination
     * @param page requested page (can be null)
     * @param size requested size (can be null)
     * @return array [normalizedPage, normalizedSize] where page >= 1 and size is valid
     */
    public static int[] normalizePageParams(Integer page, Integer size) {
        int normalizedPage = (page != null && page > 0) ? page : 1; // Default to page 1
        int normalizedSize = (size != null && size > 0 && size <= 100) ? size : 20; // Default to 20, max 100
        return new int[]{normalizedPage, normalizedSize};
    }
    
    // Getters and Setters
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public boolean isHasNext() {
        return hasNext;
    }
    
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public boolean isHasPrevious() {
        return hasPrevious;
    }
    
    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
