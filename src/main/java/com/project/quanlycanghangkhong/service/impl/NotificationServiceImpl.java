package com.project.quanlycanghangkhong.service.impl;

import com.project.quanlycanghangkhong.model.Notification;
import com.project.quanlycanghangkhong.repository.NotificationRepository;
import com.project.quanlycanghangkhong.service.NotificationService;
import com.project.quanlycanghangkhong.repository.UserRepository;
import com.project.quanlycanghangkhong.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    // Danh sách type được phép push notification
    private static final List<String> ALLOWED_PUSH_TYPES = List.of(
        "FLIGHT", "ACTIVITY"// Thêm các type bạn muốn cho phép push
    );

    @Override
    public void createNotification(Integer userId, String type, String title, String content, Integer relatedId, boolean overwriteIfExists) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        if (overwriteIfExists) {
            List<Notification> old = notificationRepository.findRecentByUserTypeRelatedId(userId, type, relatedId, oneWeekAgo);
            for (Notification n : old) notificationRepository.delete(n);
        }
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setContent(content);
        n.setRelatedId(relatedId);
        n.setCreatedAt(LocalDateTime.now());
        n.setIsRead(false);
        notificationRepository.save(n);

        // Push notification nếu user có expoPushToken và type hợp lệ
        if (ALLOWED_PUSH_TYPES.contains(type)) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null && user.getExpoPushToken() != null) {
                sendExpoPush(user.getExpoPushToken(), title, content);
            }
        }
    }

    @Override
    public void createNotifications(List<Integer> userIds, String type, String title, String content, Integer relatedId, boolean overwriteIfExists) {
        for (Integer userId : userIds) {
            createNotification(userId, type, title, content, relatedId, overwriteIfExists);
        }
    }

    @Override
    public List<Notification> getRecentNotifications(Integer userId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<String> allowedTypes = List.of("ACTIVITY", "FLIGHT"); // Thêm các type bạn muốn
        return notificationRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, oneWeekAgo)
            .stream()
            .filter(n -> allowedTypes.contains(n.getType()))
            .toList();
    }

    @Override
    public void markAsRead(Integer notificationId, Integer userId) {
        Notification n = notificationRepository.findById(notificationId).orElse(null);
        if (n != null && n.getUserId().equals(userId)) {
            n.setIsRead(true);
            notificationRepository.save(n);
        }
    }

    @Override
    public long countUnread(Integer userId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        return notificationRepository.countByUserIdAndIsReadFalseAndCreatedAtAfter(userId, oneWeekAgo);
    }

    @Override
    @Transactional
    public void deleteOldNotifications() {
        LocalDateTime before = LocalDateTime.now().minusDays(7);
        notificationRepository.deleteOldNotifications(before);
    }

    @Override
    public void deleteNotification(Integer notificationId, Integer userId) {
        Notification n = notificationRepository.findById(notificationId).orElse(null);
        if (n != null && n.getUserId().equals(userId)) {
            notificationRepository.delete(n);
        }
    }

    @Override
    public void sendExpoPush(String expoPushToken, String title, String body) {
        String url = "https://exp.host/--/api/v2/push/send";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        // Loại bỏ dấu ngoặc kép thừa nếu có trong token
        String cleanToken = expoPushToken.replace("\"", "").trim();
        Map<String, Object> message = new java.util.HashMap<>();
        message.put("to", cleanToken);
        message.put("sound", "default");
        message.put("title", title);
        message.put("body", body);
        List<Map<String, Object>> payload = java.util.Collections.singletonList(message);
        System.out.println("[Push] Endpoint: " + url);
        System.out.println("[Push] Payload: " + payload);
        HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            System.out.println("[Push] Status code: " + response.getStatusCode());
            System.out.println("[Push] Response body: " + response.getBody());
            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("[Push] Lỗi khi gửi push notification: " + response.getBody());
            } else {
                System.out.println("[Push] Đã gửi push notification thành công!");
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.out.println("[Push] Exception khi gửi push notification: " + e.getMessage());
            System.out.println("[Push] Response body lỗi: " + e.getResponseBodyAsString());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[Push] Exception khi gửi push notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasSentReminder(Integer userId, Long activityId) {
        return notificationRepository.existsByTypeAndRelatedIdAndUserId("REMINDER", activityId.intValue(), userId);
    }

    @Override
    public void sendPushOnly(String expoPushToken, String title, String body) {
        sendExpoPush(expoPushToken, title, body);
    }

    @Override
    public void markReminderSent(Integer userId, Long activityId) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType("REMINDER");
        n.setTitle("Đã gửi push reminder");
        n.setContent("");
        n.setRelatedId(activityId.intValue());
        n.setCreatedAt(java.time.LocalDateTime.now());
        n.setIsRead(true);
        notificationRepository.save(n);
        System.out.println("[Push] Đã lưu notification reminder cho userId: " + userId + ", activityId: " + activityId);
    }
}
