package com.fashion.app.service.notification;

import com.fashion.app.model.Notification;
import com.fashion.app.model.User;

import java.util.List;

public interface NotificationService {
    void createNotification(Long userId, String title, String content, String type, Long relatedId);
    List<Notification> getMyNotifications(Long userId);
    long countUnread(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
}
