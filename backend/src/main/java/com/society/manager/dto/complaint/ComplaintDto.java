package com.society.manager.dto.complaint;

import com.society.manager.enums.ComplaintCategory;
import com.society.manager.enums.ComplaintPriority;
import com.society.manager.enums.ComplaintStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ComplaintDto {
    private UUID id;
    private UUID residentId;
    private String residentName;
    private String residentPhone;
    private UUID flatId;
    private String flatNumber;
    private String buildingName;
    private UUID assignedStaffId;
    private String assignedStaffName;
    private String title;
    private String description;
    private ComplaintCategory category;
    private ComplaintPriority priority;
    private ComplaintStatus status;
    private String resolution;
    private LocalDateTime closedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ComplaintCommentDto> comments;

    public ComplaintDto() {}

    public ComplaintDto(UUID id, UUID residentId, String residentName, String residentPhone, UUID flatId, String flatNumber, String buildingName, UUID assignedStaffId, String assignedStaffName, String title, String description, ComplaintCategory category, ComplaintPriority priority, ComplaintStatus status, String resolution, LocalDateTime closedDate, LocalDateTime createdAt, LocalDateTime updatedAt, List<ComplaintCommentDto> comments) {
        this.id = id;
        this.residentId = residentId;
        this.residentName = residentName;
        this.residentPhone = residentPhone;
        this.flatId = flatId;
        this.flatNumber = flatNumber;
        this.buildingName = buildingName;
        this.assignedStaffId = assignedStaffId;
        this.assignedStaffName = assignedStaffName;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.resolution = resolution;
        this.closedDate = closedDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.comments = comments;
    }

    public static ComplaintDtoBuilder builder() { return new ComplaintDtoBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getResidentId() { return residentId; }
    public void setResidentId(UUID residentId) { this.residentId = residentId; }
    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
    public String getResidentPhone() { return residentPhone; }
    public void setResidentPhone(String residentPhone) { this.residentPhone = residentPhone; }
    public UUID getFlatId() { return flatId; }
    public void setFlatId(UUID flatId) { this.flatId = flatId; }
    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public UUID getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(UUID assignedStaffId) { this.assignedStaffId = assignedStaffId; }
    public String getAssignedStaffName() { return assignedStaffName; }
    public void setAssignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }
    public ComplaintPriority getPriority() { return priority; }
    public void setPriority(ComplaintPriority priority) { this.priority = priority; }
    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public LocalDateTime getClosedDate() { return closedDate; }
    public void setClosedDate(LocalDateTime closedDate) { this.closedDate = closedDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<ComplaintCommentDto> getComments() { return comments; }
    public void setComments(List<ComplaintCommentDto> comments) { this.comments = comments; }

    public static class ComplaintDtoBuilder {
        private UUID id;
        private UUID residentId;
        private String residentName;
        private String residentPhone;
        private UUID flatId;
        private String flatNumber;
        private String buildingName;
        private UUID assignedStaffId;
        private String assignedStaffName;
        private String title;
        private String description;
        private ComplaintCategory category;
        private ComplaintPriority priority;
        private ComplaintStatus status;
        private String resolution;
        private LocalDateTime closedDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<ComplaintCommentDto> comments;

        public ComplaintDtoBuilder id(UUID id) { this.id = id; return this; }
        public ComplaintDtoBuilder residentId(UUID residentId) { this.residentId = residentId; return this; }
        public ComplaintDtoBuilder residentName(String residentName) { this.residentName = residentName; return this; }
        public ComplaintDtoBuilder residentPhone(String residentPhone) { this.residentPhone = residentPhone; return this; }
        public ComplaintDtoBuilder flatId(UUID flatId) { this.flatId = flatId; return this; }
        public ComplaintDtoBuilder flatNumber(String flatNumber) { this.flatNumber = flatNumber; return this; }
        public ComplaintDtoBuilder buildingName(String buildingName) { this.buildingName = buildingName; return this; }
        public ComplaintDtoBuilder assignedStaffId(UUID assignedStaffId) { this.assignedStaffId = assignedStaffId; return this; }
        public ComplaintDtoBuilder assignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; return this; }
        public ComplaintDtoBuilder title(String title) { this.title = title; return this; }
        public ComplaintDtoBuilder description(String description) { this.description = description; return this; }
        public ComplaintDtoBuilder category(ComplaintCategory category) { this.category = category; return this; }
        public ComplaintDtoBuilder priority(ComplaintPriority priority) { this.priority = priority; return this; }
        public ComplaintDtoBuilder status(ComplaintStatus status) { this.status = status; return this; }
        public ComplaintDtoBuilder resolution(String resolution) { this.resolution = resolution; return this; }
        public ComplaintDtoBuilder closedDate(LocalDateTime closedDate) { this.closedDate = closedDate; return this; }
        public ComplaintDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ComplaintDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public ComplaintDtoBuilder comments(List<ComplaintCommentDto> comments) { this.comments = comments; return this; }

        public ComplaintDto build() {
            return new ComplaintDto(id, residentId, residentName, residentPhone, flatId, flatNumber, buildingName, assignedStaffId, assignedStaffName, title, description, category, priority, status, resolution, closedDate, createdAt, updatedAt, comments);
        }
    }
}
