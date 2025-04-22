package com.project.quanlycanghangkhong.repository;

import com.project.quanlycanghangkhong.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(Integer userId, LocalDateTime after);

    long countByUserIdAndIsReadFalseAndCreatedAtAfter(Integer userId, LocalDateTime after);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :before")
    void deleteOldNotifications(@Param("before") LocalDateTime before);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.type = :type AND n.relatedId = :relatedId AND n.createdAt > :after")
    List<Notification> findRecentByUserTypeRelatedId(@Param("userId") Integer userId, @Param("type") String type, @Param("relatedId") Integer relatedId, @Param("after") LocalDateTime after);

    boolean existsByTypeAndRelatedIdAndUserId(String type, Integer relatedId, Integer userId);
}
