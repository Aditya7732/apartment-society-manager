package com.society.manager.dto.notification;

import com.society.manager.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationDto {
    private UUID id;
    private String title;
    private String message;
    private NotificationType type;
    private boolean read;
    private String referenceId;
    private LocalDateTime createdAt;

    public NotificationDto() {}

    public NotificationDto(UUID id, String title, String message, NotificationType type, boolean read, String referenceId, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = read;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
    }

    public static NotificationDtoBuilder builder() { return new NotificationDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class NotificationDtoBuilder {
        private UUID id;
        private String title;
        private String message;
        private NotificationType type;
        private boolean read;
        private String referenceId;
        private LocalDateTime createdAt;

        public NotificationDtoBuilder id(UUID id) { this.id = id; return this; }
        public NotificationDtoBuilder title(String title) { this.title = title; return this; }
        public NotificationDtoBuilder message(String message) { this.message = message; return this; }
        public NotificationDtoBuilder type(NotificationType type) { this.type = type; return this; }
        public NotificationDtoBuilder read(boolean read) { this.read = read; return this; }
        public NotificationDtoBuilder referenceId(String referenceId) { this.referenceId = referenceId; return this; }
        public NotificationDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public NotificationDto build() {
            return new NotificationDto(id, title, message, type, read, referenceId, createdAt);
        }
    }
}
