package com.project.quanlycanghangkhong.scheduler;

import com.project.quanlycanghangkhong.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationCleanupScheduler {
    @Autowired
    private NotificationService notificationService;

    // Chạy mỗi ngày lúc 2h sáng
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldNotifications() {
        notificationService.deleteOldNotifications();
    }
}
