package com.society.manager.dto.notice;

import com.society.manager.enums.AudienceType;
import com.society.manager.enums.NoticePriority;

import java.time.LocalDateTime;
import java.util.UUID;

public class NoticeDto {
    private UUID id;
    private String title;
    private String content;
    private NoticePriority priority;
    private AudienceType audience;
    private LocalDateTime publishDate;
    private LocalDateTime expiryDate;
    private String attachmentPath;
    private String createdByName;
    private LocalDateTime createdAt;
    private boolean active;

    public NoticeDto() {}

    public NoticeDto(UUID id, String title, String content, NoticePriority priority, AudienceType audience, LocalDateTime publishDate, LocalDateTime expiryDate, String attachmentPath, String createdByName, LocalDateTime createdAt, boolean active) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.audience = audience;
        this.publishDate = publishDate;
        this.expiryDate = expiryDate;
        this.attachmentPath = attachmentPath;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
        this.active = active;
    }

    public static NoticeDtoBuilder builder() { return new NoticeDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public NoticePriority getPriority() { return priority; }
    public void setPriority(NoticePriority priority) { this.priority = priority; }
    public AudienceType getAudience() { return audience; }
    public void setAudience(AudienceType audience) { this.audience = audience; }
    public LocalDateTime getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDateTime publishDate) { this.publishDate = publishDate; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public static class NoticeDtoBuilder {
        private UUID id;
        private String title;
        private String content;
        private NoticePriority priority;
        private AudienceType audience;
        private LocalDateTime publishDate;
        private LocalDateTime expiryDate;
        private String attachmentPath;
        private String createdByName;
        private LocalDateTime createdAt;
        private boolean active;

        public NoticeDtoBuilder id(UUID id) { this.id = id; return this; }
        public NoticeDtoBuilder title(String title) { this.title = title; return this; }
        public NoticeDtoBuilder content(String content) { this.content = content; return this; }
        public NoticeDtoBuilder priority(NoticePriority priority) { this.priority = priority; return this; }
        public NoticeDtoBuilder audience(AudienceType audience) { this.audience = audience; return this; }
        public NoticeDtoBuilder publishDate(LocalDateTime publishDate) { this.publishDate = publishDate; return this; }
        public NoticeDtoBuilder expiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; return this; }
        public NoticeDtoBuilder attachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; return this; }
        public NoticeDtoBuilder createdByName(String createdByName) { this.createdByName = createdByName; return this; }
        public NoticeDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public NoticeDtoBuilder active(boolean active) { this.active = active; return this; }

        public NoticeDto build() {
            return new NoticeDto(id, title, content, priority, audience, publishDate, expiryDate, attachmentPath, createdByName, createdAt, active);
        }
    }
}
