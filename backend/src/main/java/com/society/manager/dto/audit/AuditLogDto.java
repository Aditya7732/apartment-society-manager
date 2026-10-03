package com.society.manager.dto.audit;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLogDto {
    private UUID id;
    private String userName;
    private String userRole;
    private String action;
    private String entityType;
    private String entityId;
    private String ipAddress;
    private String previousValue;
    private String newValue;
    private LocalDateTime createdAt;

    public AuditLogDto() {}

    public AuditLogDto(UUID id, String userName, String userRole, String action, String entityType, String entityId, String ipAddress, String previousValue, String newValue, LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.userRole = userRole;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.ipAddress = ipAddress;
        this.previousValue = previousValue;
        this.newValue = newValue;
        this.createdAt = createdAt;
    }

    public static AuditLogDtoBuilder builder() { return new AuditLogDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getPreviousValue() { return previousValue; }
    public void setPreviousValue(String previousValue) { this.previousValue = previousValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class AuditLogDtoBuilder {
        private UUID id;
        private String userName;
        private String userRole;
        private String action;
        private String entityType;
        private String entityId;
        private String ipAddress;
        private String previousValue;
        private String newValue;
        private LocalDateTime createdAt;

        public AuditLogDtoBuilder id(UUID id) { this.id = id; return this; }
        public AuditLogDtoBuilder userName(String userName) { this.userName = userName; return this; }
        public AuditLogDtoBuilder userRole(String userRole) { this.userRole = userRole; return this; }
        public AuditLogDtoBuilder action(String action) { this.action = action; return this; }
        public AuditLogDtoBuilder entityType(String entityType) { this.entityType = entityType; return this; }
        public AuditLogDtoBuilder entityId(String entityId) { this.entityId = entityId; return this; }
        public AuditLogDtoBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public AuditLogDtoBuilder previousValue(String previousValue) { this.previousValue = previousValue; return this; }
        public AuditLogDtoBuilder newValue(String newValue) { this.newValue = newValue; return this; }
        public AuditLogDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public AuditLogDto build() {
            return new AuditLogDto(id, userName, userRole, action, entityType, entityId, ipAddress, previousValue, newValue, createdAt);
        }
    }
}
