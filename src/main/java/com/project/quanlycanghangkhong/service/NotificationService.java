package com.project.quanlycanghangkhong.service;

import com.project.quanlycanghangkhong.model.Notification;
import java.util.List;

public interface NotificationService {
    void createNotification(Integer userId, String type, String title, String content, Integer relatedId, boolean overwriteIfExists);
    void createNotifications(List<Integer> userIds, String type, String title, String content, Integer relatedId, boolean overwriteIfExists);
    List<Notification> getRecentNotifications(Integer userId);
    void markAsRead(Integer notificationId, Integer userId);
    long countUnread(Integer userId);
    void deleteOldNotifications();
    void sendExpoPush(String expoPushToken, String title, String body);
}
