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

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

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

        // Push notification nếu user có expoPushToken
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getExpoPushToken() != null) {
            sendExpoPush(user.getExpoPushToken(), title, content);
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
        return notificationRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, oneWeekAgo);
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
    public void sendExpoPush(String expoPushToken, String title, String body) {
        String url = "https://exp.host/--/api/v2/push/send";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String payload = String.format(
            "{\"to\":\"%s\",\"sound\":\"default\",\"title\":\"%s\",\"body\":\"%s\"}",
            expoPushToken, title, body
        );
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }
}
