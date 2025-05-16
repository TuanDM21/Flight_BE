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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.repository.TeamRepository;
import com.project.quanlycanghangkhong.repository.UnitRepository;
import com.project.quanlycanghangkhong.service.NotificationService;
import com.project.quanlycanghangkhong.model.Team;
import com.project.quanlycanghangkhong.model.Unit;
import com.project.quanlycanghangkhong.model.User;

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
    public List<ActivityDTO> searchActivities(String name, String location) {
        return activityRepository.findAll().stream()
                .filter(a -> (name == null || a.getName().toLowerCase().contains(name.toLowerCase()))
                        && (location == null || a.getLocation().toLowerCase().contains(location.toLowerCase())))
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
    public List<ActivityDTO> getActivitiesForUser(Integer userId) {
        List<Integer> teamIds = teamRepository.findTeamIdsByUserId(userId);
        List<Integer> unitIds = unitRepository.findUnitIdsByUserId(userId);
        List<Activity> activities = activityRepository.findActivitiesForUser(userId, teamIds, unitIds);
        return activities.stream().map(this::toDTO).collect(Collectors.toList());
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
    public void pinActivity(Long id, boolean pinned) {
        Activity activity = activityRepository.findById(id).orElseThrow();
        activity.setPinned(pinned);
        activityRepository.save(activity);
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
        List<ActivityParticipantDTO> participants = activityParticipantRepository.findByActivityId(activity.getId())
                .stream().map(this::toParticipantDTO).collect(Collectors.toList());
        dto.setParticipants(participants);
        return dto;
    }

    private ActivityParticipantDTO toParticipantDTO(ActivityParticipant entity) {
        ActivityParticipantDTO dto = new ActivityParticipantDTO();
        dto.setId(entity.getId());
        dto.setParticipantType(entity.getParticipantType());
        dto.setParticipantId(entity.getParticipantId());
        // Lấy participantName thực tế
        if ("USER".equals(entity.getParticipantType())) {
            userRepository.findById(entity.getParticipantId().intValue())
                .ifPresent(u -> dto.setParticipantName(u.getName()));
        } else if ("TEAM".equals(entity.getParticipantType())) {
            teamRepository.findById(entity.getParticipantId().intValue())
                .ifPresent(t -> dto.setParticipantName(t.getTeamName()));
        } else if ("UNIT".equals(entity.getParticipantType())) {
            unitRepository.findById(entity.getParticipantId().intValue())
                .ifPresent(u -> dto.setParticipantName(u.getUnitName()));
        }
        return dto;
    }
}
