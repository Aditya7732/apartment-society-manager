package com.society.manager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "complaint_comments")
public class ComplaintComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ComplaintComment() {}

    public ComplaintComment(UUID id, Complaint complaint, User user, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.complaint = complaint;
        this.user = user;
        this.comment = comment;
        if (createdAt != null) this.createdAt = createdAt;
    }

    public static ComplaintCommentBuilder builder() { return new ComplaintCommentBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) { this.complaint = complaint; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class ComplaintCommentBuilder {
        private UUID id;
        private Complaint complaint;
        private User user;
        private String comment;
        private LocalDateTime createdAt = LocalDateTime.now();

        public ComplaintCommentBuilder id(UUID id) { this.id = id; return this; }
        public ComplaintCommentBuilder complaint(Complaint complaint) { this.complaint = complaint; return this; }
        public ComplaintCommentBuilder user(User user) { this.user = user; return this; }
        public ComplaintCommentBuilder comment(String comment) { this.comment = comment; return this; }
        public ComplaintCommentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ComplaintComment build() {
            return new ComplaintComment(id, complaint, user, comment, createdAt);
        }
    }
}
