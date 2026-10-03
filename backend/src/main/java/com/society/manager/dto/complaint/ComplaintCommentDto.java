package com.society.manager.dto.complaint;

import java.time.LocalDateTime;
import java.util.UUID;

public class ComplaintCommentDto {
    private UUID id;
    private UUID complaintId;
    private UUID userId;
    private String userName;
    private String userRole;
    private String comment;
    private LocalDateTime createdAt;

    public ComplaintCommentDto() {}

    public ComplaintCommentDto(UUID id, UUID complaintId, UUID userId, String userName, String userRole, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.complaintId = complaintId;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public static ComplaintCommentDtoBuilder builder() { return new ComplaintCommentDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getComplaintId() { return complaintId; }
    public void setComplaintId(UUID complaintId) { this.complaintId = complaintId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class ComplaintCommentDtoBuilder {
        private UUID id;
        private UUID complaintId;
        private UUID userId;
        private String userName;
        private String userRole;
        private String comment;
        private LocalDateTime createdAt;

        public ComplaintCommentDtoBuilder id(UUID id) { this.id = id; return this; }
        public ComplaintCommentDtoBuilder complaintId(UUID complaintId) { this.complaintId = complaintId; return this; }
        public ComplaintCommentDtoBuilder userId(UUID userId) { this.userId = userId; return this; }
        public ComplaintCommentDtoBuilder userName(String userName) { this.userName = userName; return this; }
        public ComplaintCommentDtoBuilder userRole(String userRole) { this.userRole = userRole; return this; }
        public ComplaintCommentDtoBuilder comment(String comment) { this.comment = comment; return this; }
        public ComplaintCommentDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ComplaintCommentDto build() {
            return new ComplaintCommentDto(id, complaintId, userId, userName, userRole, comment, createdAt);
        }
    }
}
