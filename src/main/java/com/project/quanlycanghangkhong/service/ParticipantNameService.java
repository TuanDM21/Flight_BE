package com.project.quanlycanghangkhong.service;

import java.util.Map;
import java.util.Set;

public interface ParticipantNameService {
    
    /**
     * Get participant name by type and ID with caching
     */
    String getParticipantName(String participantType, Integer participantId);
    
    /**
     * Batch load participant names with optimized caching
     */
    Map<String, String> batchLoadParticipantNames(
        Set<Integer> userIds, 
        Set<Integer> teamIds, 
        Set<Integer> unitIds
    );
    
    /**
     * Clear individual name caches
     */
    void clearParticipantNameCache(String participantType, Integer participantId);
    
    /**
     * Clear all name caches
     */
    void clearAllNameCaches();
} 