package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.dto.ActivityDTO;
import com.project.quanlycanghangkhong.dto.ActivityParticipantDTO;

import java.util.List;

public interface ActivityService {
    ActivityDTO createActivity(ActivityDTO dto);
    ActivityDTO updateActivity(Long id, ActivityDTO dto);
    void deleteActivity(Long id);
    ActivityDTO getActivity(Long id);
    List<ActivityDTO> searchActivities(String keyword, String participantType, Long participantId, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    List<ActivityDTO> getAllActivities();
    List<ActivityDTO> searchActivitiesByMonthYear(int month, int year);
    List<ActivityDTO> getActivitiesByDate(java.time.LocalDate date);
    List<ActivityDTO> getActivitiesByDateRange(java.time.LocalDate start, java.time.LocalDate end);
    // Participants
    List<ActivityParticipantDTO> addParticipants(Long activityId, List<ActivityParticipantDTO> participants);
    void removeParticipant(Long activityId, String participantType, Long participantId);
    List<ActivityDTO> getActivitiesForUser(Integer userId);
    void pinActivity(Long id, boolean pinned);
    List<ActivityDTO> getPinnedActivities();
    void clearUserActivitiesCache(Integer userId);
}
