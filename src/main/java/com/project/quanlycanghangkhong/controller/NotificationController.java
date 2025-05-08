package com.project.quanlycanghangkhong.controller;

import com.project.quanlycanghangkhong.model.Notification;
import com.project.quanlycanghangkhong.service.NotificationService;
import com.project.quanlycanghangkhong.model.User;
import com.project.quanlycanghangkhong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserRepository userRepository;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null ? user.getId() : null;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
        Integer userId = getCurrentUserId();
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(notificationService.getRecentNotifications(userId));
    }

    @PostMapping("/read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer id) {  
        Integer userId = getCurrentUserId();
        if (userId == null) return ResponseEntity.status(401).build();
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> countUnread() {
        Integer userId = getCurrentUserId();
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(notificationService.countUnread(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Integer id) {
        Integer userId = getCurrentUserId();
        if (userId == null) return ResponseEntity.status(401).build();
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.ok().build();
    }
}
