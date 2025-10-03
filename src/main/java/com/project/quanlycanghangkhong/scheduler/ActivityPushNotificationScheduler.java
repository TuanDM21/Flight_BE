package com.project.quanlycanghangkhong.scheduler;

import com.project.quanlycanghangkhong.model.Activity;
import com.project.quanlycanghangkhong.model.ActivityParticipant;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.ActivityParticipantRepository;
import com.project.quanlycanghangkhong.repository.ActivityRepository;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;  // ✅ COMMENTED: Import không cần thiết
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Component
public class ActivityPushNotificationScheduler {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ActivityParticipantRepository participantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    // @Scheduled(fixedRate = 60000)  // ✅ COMMENTED: Tắt auto push notifications
    public void sendPushNotificationsBeforeActivity() {
        System.out.println("[Scheduler] Kiểm tra gửi push notification trước 30 phút cho activity...");
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime now = LocalDateTime.now(zoneId);
        LocalDateTime in30Min = now.plusMinutes(30);
        System.out.println("[Scheduler] now (VN): " + now + ", in30Min (VN): " + in30Min);
        List<Activity> activities = activityRepository.findActivitiesStartingBetween(now, in30Min);
        System.out.println("[Scheduler] Số activity sắp diễn ra trong 30 phút tới: " + activities.size());
        for (Activity activity : activities) {
            System.out.println("[Scheduler] Xét activity: " + activity.getId() + " - " + activity.getTitle() + " (startDate: " + activity.getStartDate() + ")");
            List<ActivityParticipant> participants = participantRepository.findByActivityId(activity.getId());
            System.out.println("[Scheduler] Số participant: " + participants.size());
            Set<Integer> notifiedUserIds = new HashSet<>();
            for (ActivityParticipant participant : participants) {
                System.out.println("[Scheduler] Participant: id=" + participant.getParticipantId() + ", type=" + participant.getParticipantType());
                if ("USER".equals(participant.getParticipantType())) {
                    Long userId = participant.getParticipantId();
                    User user = userRepository.findById(userId.intValue()).orElse(null);
                    System.out.println("[Scheduler] User: " + (user != null ? user.getId() + ", expoPushToken=" + user.getExpoPushToken() : "null"));
                    if (user != null && user.getExpoPushToken() != null && !notifiedUserIds.contains(user.getId())) {
                        boolean alreadySent = notificationService.hasSentReminder(user.getId(), activity.getId());
                        System.out.println("[Scheduler] Chuẩn bị gửi push cho userId: " + userId + ", activityId: " + activity.getId() + ", alreadySent: " + alreadySent);
                        if (!alreadySent) {
                            notificationService.sendPushOnly(
                                user.getExpoPushToken(),
                                "Sắp diễn ra hoạt động: " + activity.getTitle(),
                                "Bắt đầu lúc: " + activity.getStartDate()
                            );
                            notificationService.markReminderSent(user.getId(), activity.getId());
                            System.out.println("[Scheduler] Đã gửi push notification reminder cho userId: " + userId + ", activityId: " + activity.getId());
                        }
                        notifiedUserIds.add(user.getId());
                    }
                } else if ("TEAM".equals(participant.getParticipantType())) {
                    Integer teamId = participant.getParticipantId().intValue();
                    List<Integer> userIds = userRepository.findUserIdsByTeamId(teamId);
                    System.out.println("[Scheduler] TEAM participant, userIds: " + userIds);
                    for (Integer userId : userIds) {
                        if (!notifiedUserIds.contains(userId)) {
                            User user = userRepository.findById(userId).orElse(null);
                            System.out.println("[Scheduler] User in TEAM: " + (user != null ? user.getId() + ", expoPushToken=" + user.getExpoPushToken() : "null"));
                            if (user != null && user.getExpoPushToken() != null) {
                                boolean alreadySent = notificationService.hasSentReminder(user.getId(), activity.getId());
                                System.out.println("[Scheduler] Chuẩn bị gửi push cho userId: " + userId + ", activityId: " + activity.getId() + ", alreadySent: " + alreadySent);
                                if (!alreadySent) {
                                    notificationService.sendPushOnly(
                                        user.getExpoPushToken(),
                                        "Sắp diễn ra hoạt động: " + activity.getTitle(),
                                        "Bắt đầu lúc: " + activity.getStartDate()
                                    );
                                    notificationService.markReminderSent(user.getId(), activity.getId());
                                    System.out.println("[Scheduler] Đã gửi push notification reminder cho userId: " + userId + ", activityId: " + activity.getId());
                                }
                                notifiedUserIds.add(user.getId());
                            }
                        }
                    }
                } else if ("UNIT".equals(participant.getParticipantType())) {
                    Integer unitId = participant.getParticipantId().intValue();
                    List<Integer> userIds = userRepository.findUserIdsByUnitId(unitId);
                    System.out.println("[Scheduler] UNIT participant, userIds: " + userIds);
                    for (Integer userId : userIds) {
                        if (!notifiedUserIds.contains(userId)) {
                            User user = userRepository.findById(userId).orElse(null);
                            System.out.println("[Scheduler] User in UNIT: " + (user != null ? user.getId() + ", expoPushToken=" + user.getExpoPushToken() : "null"));
                            if (user != null && user.getExpoPushToken() != null) {
                                boolean alreadySent = notificationService.hasSentReminder(user.getId(), activity.getId());
                                System.out.println("[Scheduler] Chuẩn bị gửi push cho userId: " + userId + ", activityId: " + activity.getId() + ", alreadySent: " + alreadySent);
                                if (!alreadySent) {
                                    notificationService.sendPushOnly(
                                        user.getExpoPushToken(),
                                        "Sắp diễn ra hoạt động: " + activity.getTitle(),
                                        "Bắt đầu lúc: " + activity.getStartDate()
                                    );
                                    notificationService.markReminderSent(user.getId(), activity.getId());
                                    System.out.println("[Scheduler] Đã gửi push notification reminder cho userId: " + userId + ", activityId: " + activity.getId());
                                }
                                notifiedUserIds.add(user.getId());
                            }
                        }
                    }
                }
            }
        }
    }
}
