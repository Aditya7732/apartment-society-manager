package com.society.manager.entity;

import com.society.manager.enums.ComplaintCategory;
import com.society.manager.enums.ComplaintPriority;
import com.society.manager.enums.ComplaintStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "complaints")
public class Complaint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    private Staff assignedStaff;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplaintPriority priority = ComplaintPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @Column(name = "closed_date")
    private LocalDateTime closedDate;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComplaintComment> comments = new ArrayList<>();

    public Complaint() {}

    public Complaint(UUID id, Resident resident, Flat flat, Staff assignedStaff, String title, String description, ComplaintCategory category, ComplaintPriority priority, ComplaintStatus status, String resolution, LocalDateTime closedDate, List<ComplaintComment> comments) {
        this.id = id;
        this.resident = resident;
        this.flat = flat;
        this.assignedStaff = assignedStaff;
        this.title = title;
        this.description = description;
        this.category = category;
        if (priority != null) this.priority = priority;
        if (status != null) this.status = status;
        this.resolution = resolution;
        this.closedDate = closedDate;
        if (comments != null) this.comments = comments;
    }

    public static ComplaintBuilder builder() { return new ComplaintBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Resident getResident() { return resident; }
    public void setResident(Resident resident) { this.resident = resident; }
    public Flat getFlat() { return flat; }
    public void setFlat(Flat flat) { this.flat = flat; }
    public Staff getAssignedStaff() { return assignedStaff; }
    public void setAssignedStaff(Staff assignedStaff) { this.assignedStaff = assignedStaff; }
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
    public List<ComplaintComment> getComments() { return comments; }
    public void setComments(List<ComplaintComment> comments) { this.comments = comments; }

    public static class ComplaintBuilder {
        private UUID id;
        private Resident resident;
        private Flat flat;
        private Staff assignedStaff;
        private String title;
        private String description;
        private ComplaintCategory category;
        private ComplaintPriority priority = ComplaintPriority.MEDIUM;
        private ComplaintStatus status = ComplaintStatus.OPEN;
        private String resolution;
        private LocalDateTime closedDate;
        private List<ComplaintComment> comments = new ArrayList<>();

        public ComplaintBuilder id(UUID id) { this.id = id; return this; }
        public ComplaintBuilder resident(Resident resident) { this.resident = resident; return this; }
        public ComplaintBuilder flat(Flat flat) { this.flat = flat; return this; }
        public ComplaintBuilder assignedStaff(Staff assignedStaff) { this.assignedStaff = assignedStaff; return this; }
        public ComplaintBuilder title(String title) { this.title = title; return this; }
        public ComplaintBuilder description(String description) { this.description = description; return this; }
        public ComplaintBuilder category(ComplaintCategory category) { this.category = category; return this; }
        public ComplaintBuilder priority(ComplaintPriority priority) { this.priority = priority; return this; }
        public ComplaintBuilder status(ComplaintStatus status) { this.status = status; return this; }
        public ComplaintBuilder resolution(String resolution) { this.resolution = resolution; return this; }
        public ComplaintBuilder closedDate(LocalDateTime closedDate) { this.closedDate = closedDate; return this; }
        public ComplaintBuilder comments(List<ComplaintComment> comments) { this.comments = comments; return this; }

        public Complaint build() {
            return new Complaint(id, resident, flat, assignedStaff, title, description, category, priority, status, resolution, closedDate, comments);
        }
    }
}
