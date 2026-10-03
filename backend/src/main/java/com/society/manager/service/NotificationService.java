package com.society.manager.service;

import com.society.manager.dto.notification.NotificationDto;
import com.society.manager.enums.NotificationType;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void createNotification(UUID userId, String title, String message, NotificationType type, String referenceId);
    List<NotificationDto> getUserNotifications(UUID userId);
    List<NotificationDto> getUnreadUserNotifications(UUID userId);
    long getUnreadCount(UUID userId);
    void markAsRead(UUID notificationId, UUID userId);
    void markAllAsRead(UUID userId);
}
