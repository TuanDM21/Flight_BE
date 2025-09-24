package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.dto.ActivityDTO;
import com.project.quanlycanghangkhong.dto.ActivityParticipantDTO;
import com.project.quanlycanghangkhong.model.Activity;
import com.project.quanlycanghangkhong.model.ActivityParticipant;
import com.project.quanlycanghangkhong.repository.ActivityParticipantRepository;
import com.project.quanlycanghangkhong.repository.ActivityRepository;
import com.project.quanlycanghangkhong.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.repository.TeamRepository;
import com.project.quanlycanghangkhong.repository.UnitRepository;
import com.project.quanlycanghangkhong.service.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ActivityServiceImpl implements ActivityService {
    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ActivityParticipantRepository activityParticipantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public ActivityDTO createActivity(ActivityDTO dto) {
        logger.info("[createActivity] Bắt đầu tạo activity với name: {}", dto.getName());
        Activity activity = new Activity();
        activity.setName(dto.getName());
        activity.setLocation(dto.getLocation());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setNotes(dto.getNotes());
        Activity saved = activityRepository.save(activity);
        logger.info("[createActivity] Đã lưu activity id: {}", saved.getId());

        // Lưu participants nếu có
        List<Long> notifiedUserIds = new ArrayList<>();
        if (dto.getParticipants() != null && !dto.getParticipants().isEmpty()) {
            logger.info("[createActivity] Có {} participants", dto.getParticipants().size());
            List<ActivityParticipant> entities = new ArrayList<>();
            for (ActivityParticipantDTO p : dto.getParticipants()) {
                logger.info("[createActivity] Xử lý participant: type={}, id={}", p.getParticipantType(), p.getParticipantId());
                boolean valid = false;
                if ("USER".equals(p.getParticipantType())) {
                    valid = userRepository.findById(p.getParticipantId().intValue()).isPresent();
                    logger.info("[createActivity] USER tồn tại: {}", valid);
                } else if ("TEAM".equals(p.getParticipantType())) {
                    valid = teamRepository.findById(p.getParticipantId().intValue()).isPresent();
                    logger.info("[createActivity] TEAM tồn tại: {}", valid);
                } else if ("UNIT".equals(p.getParticipantType())) {
                    valid = unitRepository.findById(p.getParticipantId().intValue()).isPresent();
                    logger.info("[createActivity] UNIT tồn tại: {}", valid);
                } else {
                    logger.warn("[createActivity] participantType không hợp lệ: {}", p.getParticipantType());
                }
                if (!valid) {
                    logger.warn("[createActivity] participantId không hợp lệ, bỏ qua: type={}, id={}", p.getParticipantType(), p.getParticipantId());
                    continue;
                }
                ActivityParticipant entity = new ActivityParticipant();
                entity.setActivity(saved);
                entity.setParticipantType(p.getParticipantType());
                entity.setParticipantId(p.getParticipantId());
                entities.add(entity);
                // Xác định user nhận notification
                if ("USER".equals(p.getParticipantType())) {
                    notifiedUserIds.add(p.getParticipantId());
                } else if ("TEAM".equals(p.getParticipantType())) {
                    List<Integer> userIds = userRepository.findUserIdsByTeamId(p.getParticipantId().intValue());
                    logger.info("[createActivity] TEAM userIds: {}", userIds);
                    for (Integer uid : userIds) notifiedUserIds.add(uid.longValue());
                } else if ("UNIT".equals(p.getParticipantType())) {
                    List<Integer> userIds = userRepository.findUserIdsByUnitId(p.getParticipantId().intValue());
                    logger.info("[createActivity] UNIT userIds: {}", userIds);
                    for (Integer uid : userIds) notifiedUserIds.add(uid.longValue());
                }
            }
            activityParticipantRepository.saveAll(entities);
            logger.info("[createActivity] Đã lưu {} participants", entities.size());
        } else {
            logger.info("[createActivity] Không có participants");
        }

        // Trigger notification cho các user liên quan
        if (!notifiedUserIds.isEmpty()) {
            List<Integer> userIds = notifiedUserIds.stream()
                .distinct()
                .map(Long::intValue)
                .filter(id -> userRepository.findById(id).isPresent())
                .toList();
            logger.info("[createActivity] Gửi notification cho userIds: {}", userIds);
            try {
                notificationService.createNotifications(
                    userIds,
                    "ACTIVITY",
                    "Bạn có hoạt động mới: " + activity.getName(),
                    "Địa điểm: " + activity.getLocation() + ", Thời gian: " + activity.getStartTime(),
                    saved.getId().intValue(),
                    true
                );
            } catch (Exception e) {
                logger.error("[createActivity] Lỗi khi gửi notification: {}", e.getMessage(), e);
            }
            
            // Clear relevant caches after creating activity
            userIds.forEach(this::clearUserActivitiesCache);
        } else {
            logger.info("[createActivity] Không có user nào nhận notification");
        }
        
        logger.info("[createActivity] Hoàn tất tạo activity id: {}", saved.getId());
        return toDTO(saved);
    }

    @Override
    @Transactional
    public ActivityDTO updateActivity(Long id, ActivityDTO dto) {
        Activity activity = activityRepository.findById(id).orElseThrow();
        activity.setName(dto.getName());
        activity.setLocation(dto.getLocation());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setNotes(dto.getNotes());
        Activity saved = activityRepository.save(activity);

        // Xóa toàn bộ participant cũ và thêm mới lại từ DTO
        activityParticipantRepository.deleteAll(activityParticipantRepository.findByActivityId(id));
        if (dto.getParticipants() != null && !dto.getParticipants().isEmpty()) {
            List<ActivityParticipant> entities = new ArrayList<>();
            for (ActivityParticipantDTO p : dto.getParticipants()) {
                boolean valid = false;
                if ("USER".equals(p.getParticipantType())) {
                    valid = userRepository.findById(p.getParticipantId().intValue()).isPresent();
                } else if ("TEAM".equals(p.getParticipantType())) {
                    valid = teamRepository.findById(p.getParticipantId().intValue()).isPresent();
                } else if ("UNIT".equals(p.getParticipantType())) {
                    valid = unitRepository.findById(p.getParticipantId().intValue()).isPresent();
                }
                if (!valid) continue; // Bỏ qua participant không hợp lệ
                ActivityParticipant entity = new ActivityParticipant();
                entity.setActivity(saved);
                entity.setParticipantType(p.getParticipantType());
                entity.setParticipantId(p.getParticipantId());
                entities.add(entity);
            }
            activityParticipantRepository.saveAll(entities);
        }

        // Clear relevant caches after updating activity
        clearActivityRelatedCaches(saved);
        
        return toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    @Override
    public ActivityDTO getActivity(Long id) {
        Activity activity = activityRepository.findById(id).orElseThrow();
        return toDTO(activity);
    }

    @Override
    public List<ActivityDTO> getAllActivities() {
        return activityRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ActivityDTO> searchActivities(String keyword, String participantType, Long participantId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        // If searching by participant, use optimized repository query
        if (participantType != null && participantId != null) {
            return searchActivitiesByParticipant(keyword, participantType, participantId, startTime, endTime);
        }
        
        // Standard search without participant filter
        return activityRepository.findAll().stream()
                .filter(a -> (keyword == null || 
                        a.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        (a.getNotes() != null && a.getNotes().toLowerCase().contains(keyword.toLowerCase())) ||
                        a.getLocation().toLowerCase().contains(keyword.toLowerCase()))
                        && (startTime == null || a.getStartTime().isAfter(startTime) || a.getStartTime().isEqual(startTime))
                        && (endTime == null || a.getEndTime().isBefore(endTime) || a.getEndTime().isEqual(endTime)))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private List<ActivityDTO> searchActivitiesByParticipant(String keyword, String participantType, Long participantId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        // Validate participant type
        if (!participantType.equals("USER") && !participantType.equals("TEAM") && !participantType.equals("UNIT")) {
            logger.warn("[searchActivitiesByParticipant] Invalid participantType: {}", participantType);
            return new ArrayList<>();
        }
        
        // Validate that the participant exists
        boolean participantExists = false;
        switch (participantType) {
            case "USER" -> participantExists = userRepository.findById(participantId.intValue()).isPresent();
            case "TEAM" -> participantExists = teamRepository.findById(participantId.intValue()).isPresent();
            case "UNIT" -> participantExists = unitRepository.findById(participantId.intValue()).isPresent();
        }
        
        if (!participantExists) {
            logger.warn("[searchActivitiesByParticipant] Participant not found: type={}, id={}", participantType, participantId);
            return new ArrayList<>();
        }
        
        // Find activities that have this specific participant
        List<ActivityParticipant> participants = activityParticipantRepository
                .findByParticipantTypeAndParticipantId(participantType, participantId);
        
        if (participants.isEmpty()) {
            return new ArrayList<>();
        }
        
        Set<Long> activityIds = participants.stream()
                .map(p -> p.getActivity().getId())
                .collect(Collectors.toSet());
        
        // Get activities and apply remaining filters including keyword and time range
        return activityRepository.findAllById(activityIds).stream()
                .filter(a -> (keyword == null || 
                        a.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        (a.getNotes() != null && a.getNotes().toLowerCase().contains(keyword.toLowerCase())) ||
                        a.getLocation().toLowerCase().contains(keyword.toLowerCase()))
                        && (startTime == null || a.getStartTime().isAfter(startTime) || a.getStartTime().isEqual(startTime))
                        && (endTime == null || a.getEndTime().isBefore(endTime) || a.getEndTime().isEqual(endTime)))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityDTO> searchActivitiesByMonthYear(int month, int year) {
        List<Activity> activities = activityRepository.findByMonthAndYear(month, year);
        return activities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ActivityParticipantDTO> addParticipants(Long activityId, List<ActivityParticipantDTO> participants) {
        Activity activity = activityRepository.findById(activityId).orElseThrow();
        List<ActivityParticipant> entities = new ArrayList<>();
        for (ActivityParticipantDTO dto : participants) {
            boolean valid = false;
            if ("USER".equals(dto.getParticipantType())) {
                valid = userRepository.findById(dto.getParticipantId().intValue()).isPresent();
            } else if ("TEAM".equals(dto.getParticipantType())) {
                valid = teamRepository.findById(dto.getParticipantId().intValue()).isPresent();
            } else if ("UNIT".equals(dto.getParticipantType())) {
                valid = unitRepository.findById(dto.getParticipantId().intValue()).isPresent();
            }
            if (!valid) continue; // Bỏ qua participant không hợp lệ
            ActivityParticipant entity = new ActivityParticipant();
            entity.setActivity(activity);
            entity.setParticipantType(dto.getParticipantType());
            entity.setParticipantId(dto.getParticipantId());
            entities.add(entity);
        }
        List<ActivityParticipant> saved = activityParticipantRepository.saveAll(entities);
        return saved.stream().map(this::toParticipantDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeParticipant(Long activityId, String participantType, Long participantId) {
        activityParticipantRepository.deleteByActivityIdAndParticipantTypeAndParticipantId(activityId, participantType, participantId);
    }

    @Override
    @Cacheable(value = "userActivities", key = "#userId", unless = "#result.isEmpty()")
    public List<ActivityDTO> getActivitiesForUser(Integer userId) {
        long startTime = System.currentTimeMillis();
        logger.info("[getActivitiesForUser] Starting for userId: {}", userId);
        
        try {
            List<Integer> teamIds = teamRepository.findTeamIdsByUserId(userId);
            List<Integer> unitIds = unitRepository.findUnitIdsByUserId(userId);
            
            // Use optimized query with JOIN FETCH
            List<Activity> activities = activityRepository.findActivitiesForUserOptimized(userId, teamIds, unitIds);
            
            // Use optimized conversion with batch loading to avoid N+1 queries
            List<ActivityDTO> result = toDTOsOptimized(activities);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("[getActivitiesForUser] Completed in {}ms for userId: {}, returned {} activities", 
                       duration, userId, result.size());
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("[getActivitiesForUser] Error after {}ms for userId: {}: {}", duration, userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ActivityDTO> getActivitiesByDate(java.time.LocalDate date) {
        return activityRepository.findByDate(date).stream().map(this::toDTO).toList();
    }

    @Override
    public List<ActivityDTO> getActivitiesByDateRange(java.time.LocalDate start, java.time.LocalDate end) {
        return activityRepository.findByDateRange(start, end).stream().map(this::toDTO).toList();
    }

    @Override
    @CacheEvict(value = "pinnedActivities", allEntries = true)
    public void pinActivity(Long id, boolean pinned) {
        long start = System.currentTimeMillis();
        Activity activity = activityRepository.findById(id).orElseThrow();
        activity.setPinned(pinned);
        activityRepository.save(activity);
        long duration = System.currentTimeMillis() - start;
        logger.info("[pinActivity] Completed in {}ms for activity id: {}, pinned: {}", duration, id, pinned);
    }

    @Override
    @Cacheable(value = "pinnedActivities", key = "'all'")
    public List<ActivityDTO> getPinnedActivities() {
        long start = System.currentTimeMillis();
        logger.info("[getPinnedActivities] Starting to fetch pinned activities");
        
        try {
            // Use optimized query with JOIN FETCH
            List<Activity> pinnedActivities = activityRepository.findPinnedActivitiesOptimized();
            List<ActivityDTO> result = pinnedActivities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
            
            long duration = System.currentTimeMillis() - start;
            logger.info("[getPinnedActivities] Successfully fetched {} pinned activities in {}ms", 
                       result.size(), duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            logger.error("[getPinnedActivities] Error occurred after {}ms: {}", duration, e.getMessage(), e);
            throw e;
        }
    }

    @CacheEvict(value = "pinnedActivities", allEntries = true)
    public void evictPinnedActivitiesCache() {
        logger.info("[evictPinnedActivitiesCache] Cache evicted for pinned activities");
    }

    @Override
    @CacheEvict(value = "userActivities", key = "#userId")
    public void clearUserActivitiesCache(Integer userId) {
        logger.info("[clearUserActivitiesCache] Cache cleared for userId: {}", userId);
    }

    private void clearActivityRelatedCaches(Activity activity) {
        // Clear pinned activities cache if activity is pinned
        if (activity.getPinned() != null && activity.getPinned()) {
            evictPinnedActivitiesCache();
        }
        
        // Clear user activities cache for all participants
        List<ActivityParticipant> participants = activityParticipantRepository.findByActivityId(activity.getId());
        for (ActivityParticipant participant : participants) {
            if ("USER".equals(participant.getParticipantType())) {
                clearUserActivitiesCache(participant.getParticipantId().intValue());
            }
        }
    }

    private ActivityDTO toDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setName(activity.getName());
        dto.setLocation(activity.getLocation());
        dto.setStartTime(activity.getStartTime());
        dto.setEndTime(activity.getEndTime());
        dto.setNotes(activity.getNotes());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setUpdatedAt(activity.getUpdatedAt());
        dto.setPinned(activity.getPinned());
        
        // Use the already fetched participants from EntityGraph to avoid N+1
        List<ActivityParticipantDTO> participants = activity.getParticipants()
                .stream().map(this::toParticipantDTO).collect(Collectors.toList());
        dto.setParticipants(participants);
        return dto;
    }

    // Optimized version with batch loading for participant names
    private List<ActivityDTO> toDTOsOptimized(List<Activity> activities) {
        if (activities.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Collect all participant IDs by type for batch loading
        Set<Integer> userIds = new HashSet<>();
        Set<Integer> teamIds = new HashSet<>();
        Set<Integer> unitIds = new HashSet<>();
        
        for (Activity activity : activities) {
            for (ActivityParticipant participant : activity.getParticipants()) {
                Integer participantId = participant.getParticipantId().intValue();
                switch (participant.getParticipantType()) {
                    case "USER" -> userIds.add(participantId);
                    case "TEAM" -> teamIds.add(participantId);
                    case "UNIT" -> unitIds.add(participantId);
                }
            }
        }
        
        // Batch load all names
        Map<Integer, String> userNames = userIds.isEmpty() ? new HashMap<>() : 
            userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(u -> u.getId(), u -> u.getName()));
                
        Map<Integer, String> teamNames = teamIds.isEmpty() ? new HashMap<>() : 
            teamRepository.findAllById(teamIds).stream()
                .collect(Collectors.toMap(t -> t.getId(), t -> t.getTeamName()));
                
        Map<Integer, String> unitNames = unitIds.isEmpty() ? new HashMap<>() : 
            unitRepository.findAllById(unitIds).stream()
                .collect(Collectors.toMap(u -> u.getId(), u -> u.getUnitName()));
        
        // Convert to DTOs using the batch-loaded names
        return activities.stream()
            .map(activity -> toDTOWithNames(activity, userNames, teamNames, unitNames))
            .collect(Collectors.toList());
    }
    
    private ActivityDTO toDTOWithNames(Activity activity, Map<Integer, String> userNames, 
                                      Map<Integer, String> teamNames, Map<Integer, String> unitNames) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setName(activity.getName());
        dto.setLocation(activity.getLocation());
        dto.setStartTime(activity.getStartTime());
        dto.setEndTime(activity.getEndTime());
        dto.setNotes(activity.getNotes());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setUpdatedAt(activity.getUpdatedAt());
        dto.setPinned(activity.getPinned());
        
        List<ActivityParticipantDTO> participants = activity.getParticipants().stream()
            .map(p -> {
                ActivityParticipantDTO pDto = new ActivityParticipantDTO();
                pDto.setId(p.getId());
                pDto.setParticipantType(p.getParticipantType());
                pDto.setParticipantId(p.getParticipantId());
                
                Integer participantId = p.getParticipantId().intValue();
                String participantName = switch (p.getParticipantType()) {
                    case "USER" -> userNames.getOrDefault(participantId, "Unknown User");
                    case "TEAM" -> teamNames.getOrDefault(participantId, "Unknown Team");
                    case "UNIT" -> unitNames.getOrDefault(participantId, "Unknown Unit");
                    default -> "Unknown";
                };
                pDto.setParticipantName(participantName);
                
                return pDto;
            })
            .collect(Collectors.toList());
            
        dto.setParticipants(participants);
        return dto;
    }

    private ActivityParticipantDTO toParticipantDTO(ActivityParticipant entity) {
        ActivityParticipantDTO dto = new ActivityParticipantDTO();
        dto.setId(entity.getId());
        dto.setParticipantType(entity.getParticipantType());
        dto.setParticipantId(entity.getParticipantId());
        
        // Use caching or batch queries for participant names to reduce database hits
        // For now, we'll set a generic name to avoid N+1 queries
        String participantName = entity.getParticipantType() + "_" + entity.getParticipantId();
        
        // TODO: Implement batch loading or caching for participant names
        // This is a temporary solution to avoid N+1 queries
        dto.setParticipantName(participantName);
        
        return dto;
    }
}
