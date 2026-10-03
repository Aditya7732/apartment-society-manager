package com.society.manager.entity;

import com.society.manager.enums.AudienceType;
import com.society.manager.enums.NoticePriority;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notices")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NoticePriority priority = NoticePriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AudienceType audience = AudienceType.ALL;

    @Column(name = "publish_date")
    private LocalDateTime publishDate = LocalDateTime.now();

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "attachment_path", length = 255)
    private String attachmentPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    public Notice() {}

    public Notice(UUID id, String title, String content, NoticePriority priority, AudienceType audience, LocalDateTime publishDate, LocalDateTime expiryDate, String attachmentPath, User createdBy) {
        this.id = id;
        this.title = title;
        this.content = content;
        if (priority != null) this.priority = priority;
        if (audience != null) this.audience = audience;
        if (publishDate != null) this.publishDate = publishDate;
        this.expiryDate = expiryDate;
        this.attachmentPath = attachmentPath;
        this.createdBy = createdBy;
    }

    public static NoticeBuilder builder() { return new NoticeBuilder(); }

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
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public static class NoticeBuilder {
        private UUID id;
        private String title;
        private String content;
        private NoticePriority priority = NoticePriority.MEDIUM;
        private AudienceType audience = AudienceType.ALL;
        private LocalDateTime publishDate = LocalDateTime.now();
        private LocalDateTime expiryDate;
        private String attachmentPath;
        private User createdBy;

        public NoticeBuilder id(UUID id) { this.id = id; return this; }
        public NoticeBuilder title(String title) { this.title = title; return this; }
        public NoticeBuilder content(String content) { this.content = content; return this; }
        public NoticeBuilder priority(NoticePriority priority) { this.priority = priority; return this; }
        public NoticeBuilder audience(AudienceType audience) { this.audience = audience; return this; }
        public NoticeBuilder publishDate(LocalDateTime publishDate) { this.publishDate = publishDate; return this; }
        public NoticeBuilder expiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; return this; }
        public NoticeBuilder attachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; return this; }
        public NoticeBuilder createdBy(User createdBy) { this.createdBy = createdBy; return this; }

        public Notice build() {
            return new Notice(id, title, content, priority, audience, publishDate, expiryDate, attachmentPath, createdBy);
        }
    }
}
